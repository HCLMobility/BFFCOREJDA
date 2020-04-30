package com.jda.mobility.framework.extensions.service.impl.migration.importer;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.entity.Form;
import com.jda.mobility.framework.extensions.repository.FlowRepository;
import com.jda.mobility.framework.extensions.utils.FormDependencyUtil;
import org.springframework.stereotype.Service;

import static com.jda.mobility.framework.extensions.service.impl.migration.importer.FlowConflictResolutionStrategy.IGNORE_NEW;
import static com.jda.mobility.framework.extensions.service.impl.migration.importer.FlowConflictResolutionStrategy.RENAME_NEW;
import static java.util.Map.entry;

/**
 * Imports {@link Flow flow} and {@link Form form} entities.
 */
@Service
public class FormFlowImporter {

    private final FlowRepository repo;
    private final FormDependencyUtil interdependencies;
    private final ObjectMapper mapper;
    private final FlowDiffTool diffTool;

    public FormFlowImporter(FlowRepository repo,
                            FormDependencyUtil interdependencies,
                            ObjectMapper mapper,
                            FlowDiffTool diffTool) {
        this.repo = repo;
        this.interdependencies = interdependencies;
        this.mapper = mapper;
        this.diffTool = diffTool;
    }

    /**
     * Deserializes the flow and its forms (if present) into the appropriate entities
     * and then saves those entities in the database.
     * <p>
     * Flows and forms already present in the database that have the same {@code UUID}s
     * as the imported entities will be replaced entirely.
     * <p>
     * If any issues occur during the import process, they will be added to the specified
     * issues list.
     *
     * @param flowBytes Contains the serialized flow (in JSON form).
     * @param formsBytes Each byte array in the list contain a serialized form (in JSON form).
     * @param context The context of the overall import. Any errors, warnings, or notes for
     * informational purposes will be added to the context.
     * @return A {@code map} containing zero or more JsonNodes that define the changes that
     * were made to the database in {@link JsonPatch} format.
     */
    public Map<String, JsonNode> doImport(byte[] flowBytes,
                                          List<byte[]> formsBytes,
                                          ImportContext context) {
        Flow flow = deserialize(flowBytes, Flow.class, context);
        if (flow == null) {
            return Map.of();
        }

        List<Form> forms = formsBytes.stream()
                .map(bytes -> deserialize(bytes, Form.class, context))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (forms.size() != formsBytes.size()) {
            // We had at least one form that failed to deserialize.
            return Map.of();
        }

        return context.run(flow.getUid().toString(), () -> {
            forms.forEach(flow::addForm);
            Flow original = repo.findById(flow.getUid()).orElse(null);
            boolean foundUidMatch = original != null;

            Flow saved = maybeSaveFlow(flow, foundUidMatch, context);

            if (saved != null) {
                updateInterdependencies(saveFlow(flow, foundUidMatch));
                Map<String, JsonPatch> diffs = diffTool.diff(original, saved);
                return jsonifyDiffs(diffs);
            }
            else {
                return Map.of();
            }
        });
    }

    /**
     * Determines if the imported flow should be saved and does so if appropriate.
     * <p>
     * Under normal circumstances, the flow will be saved. However, it is possible
     * for the imported flow to conflict with a flow already in the database that
     * has a different UUID. In these situations, the context's conflict resolution
     * strategy will determine if the imported flow should be saved or ignored.
     *
     * @param imported The imported flow to be saved
     * @param foundUidMatch Indicates that there exists a flow already in the database
     * that has the same UUID as the flow being imported. If such a flow exists it
     * will be replaced with the imported flow (assuming the conflict resolution
     * strategy does not cause the import to be ignored entirely).
     * @param context The import context.
     * @return Either {@code null} if the imported flow was not saved, or a {@code Flow}
     * instance if it was.
     */
    private Flow maybeSaveFlow(Flow imported, boolean foundUidMatch, ImportContext context) {
        FlowConflictResolutionStrategy strategy = context.getFlowConflictResolutionStrategy();
        Flow conflict = findPossibleConflict(imported, strategy);

        if (conflict == null) {
            return saveFlow(imported, foundUidMatch);
        }
        else if (strategy == IGNORE_NEW) {
            context.note("Detected conflict for flow. Ignoring imported flow due to conflict resolution strategy");
            // We're handling the conflict by ignoring the imported flow. Exit without saving
            return null;
        }
        else if (strategy == RENAME_NEW) {
            String uniqueName = getUniqueName(imported);

            if (!imported.getUid().equals(conflict.getUid()) || uniqueName.equals(conflict.getName())) {
                // If the uids don't match we, must rename. If the uids do match, we have to account
                // for the case where we've imported the same flow multiple times and already renamed
                // it once. In that scenario, we're going to end up removing the previously renamed
                // flow (since the uids match) and we need to rename this so it doesn't collide with
                // the flow the earlier import collided with when it was renamed.
                imported.setName(uniqueName);
                context.note("Detected conflict for flow. Imported flow has been renamed to " + imported.getName() +
                        " as required by conflict resolution strategy");
                // We've updated the name in the imported flow but we still need to save it.
            }
            return saveFlow(imported, foundUidMatch);
        }
        else {
            context.raise("Detected conflict for flow but unrecognized conflict resolution strategy " +
                    strategy + " is specified.");
            throw new IllegalArgumentException("Unsupported conflict resolution strategy " + strategy);
        }
    }

    /**
     * It is possible for two environments to create flows with the same name
     * and version in parallel that are not the same flows and won't have the
     * same UUIDs. The name/version combination is a unique constraint on the
     * flow table, so we need to check if the flow we are importing will
     * collide with an existing flow.
     *
     * @param imported The flow being imported.
     * @param strategy The conflict resolution strategy to use
     * @return Possibly a Flow that conflicts with the flow being imported.
     * If {@code null} is returned there is definitely a conflict. If a flow
     * is returned, it <em>may</em> be a conflict. Whether it is or not
     * depends on the particular conflict resolution strategy in use.
     */
    private Flow findPossibleConflict(Flow imported, FlowConflictResolutionStrategy strategy) {
        String name = imported.getName();
        long version = imported.getVersion();

        Optional<Flow> conflict = repo.findByNameAndVersion(name, version).stream().findFirst();

        if (conflict.isEmpty()) {
            if (strategy == RENAME_NEW) {
                // If we're going to try to rename the imported flow on
                // conflicts, make sure we haven't already done so and
                // look again for what the renamed flow would be.
                conflict = repo.findByNameAndVersion(getUniqueName(imported), version).stream().findFirst();
            }
        }
        return conflict.orElse(null);
    }

    private Flow saveFlow(Flow flow, boolean deleteFirst) {
        UUID uid = flow.getUid();
        if (deleteFirst) {
            repo.deleteById(uid);
        }
        return repo.save(flow);
    }

    private void updateInterdependencies(Flow flow) {
        UUID defaultFormId = flow.getDefaultFormId();
        flow.getForms().forEach(form -> {
            // TODO improve these method names. They are not good
            interdependencies.managePublishedWorkFlowDependency(form);
            interdependencies.findLinkedPublishedFormIds(form, defaultFormId);
            interdependencies.manageWorkFlowDependency(form);
            interdependencies.findlinkedForms(form, defaultFormId);
        });
    }

    private <T> T deserialize(byte[] contents, Class<T> type, ImportContext context) {
        try {
            return mapper.readValue(contents, type);
        }
        catch (IOException e) {
            context.raise("Unable to parse entity of type : " + type, e);
        }
        return null;
    }

    private String getUniqueName(Flow flow) {
        return flow.getName() + " " + flow.getUid().toString();
    }

    private Map<String, JsonNode> jsonifyDiffs(Map<String, JsonPatch> diffs) {
        return diffs.entrySet().stream()
                .map(e -> entry(e.getKey(), (JsonNode) mapper.valueToTree(e.getValue())))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }
}
