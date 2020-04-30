package com.jda.mobility.framework.extensions.service.impl.migration.exporter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jda.mobility.framework.extensions.entity.ApiMaster;
import com.jda.mobility.framework.extensions.entity.AppConfigMaster;
import com.jda.mobility.framework.extensions.entity.CustomComponentMaster;
import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.entity.Form;
import com.jda.mobility.framework.extensions.entity.MenuMaster;
import com.jda.mobility.framework.extensions.entity.ResourceBundle;
import com.jda.mobility.framework.extensions.repository.FlowRepository;
import com.jda.mobility.framework.extensions.service.impl.migration.FileSuffix;
import com.jda.mobility.framework.extensions.service.impl.migration.References;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

/**
 * Responsible for exporting an individual {@link Flow} along with
 * the dependent entities all {@link Form}s in the flow use.
 * <p>
 * These dependencies are:
 * <ul>
 *     <li>
 *         {@link MenuMaster Form Context Menus}
 *     </li>
 *     <li>
 *         {@link AppConfigMaster Global Variables used}
 *     </li>
 *     <li>
 *         {@link AppConfigMaster Context Values used}
 *     </li>
 *     <li>
 *         {@link ResourceBundle Localized Text Keys used}
 *     </li>
 *     <li>
 *         {@link ApiMaster APIs used}
 *     </li>
 *     <li>
 *         {@link com.jda.mobility.framework.extensions.entity.ApiRegistry API Registries used}
 *     </li>
 *     <li>
 *         {@link CustomComponentMaster Custom Components used}
 *     </li>
 * </ul>
 */
@Service
public class FlowExporter {

    private final FormExporter formExporter;
    private final FlowRepository flows;
    private final Set<FlowSpecificExporter> exporters;
    private final ObjectMapper mapper;
    private final JsonNodeFactory factory = JsonNodeFactory.instance;

    public FlowExporter(FlowRepository flows,
                        FormExporter formExporter,
                        Set<FlowSpecificExporter> exporters) {
        this.formExporter = formExporter;
        this.flows = flows;
        this.exporters = exporters;
        this.mapper = new ObjectMapper()
                .setSerializationInclusion(Include.NON_DEFAULT);
    }

    public Map<String, JsonNode> exportToJson(UUID flowId) {
        return exportToJson(flows.findById(flowId).orElseThrow(), true);
    }

    /**
     * Serializes the various entities associated with the flow into JSON
     * and returns them in a map keyed by the name of the file that should
     * hold that JSON blob.
     * @param flow The flow to export
     * @param includeDependencies Indicates if the exported JSON should include
     * entities upon which the form depends. NOTE: forms will <em>always</em>
     * be included, regardless of the value passed here. Dependencies in this
     * context refers to apis, configs, resource bundles, etc.
     * @return A map containing serialized entities. This map may be empty, but
     * should <em>never</em> be {@code null}.
     */
    public Map<String, JsonNode> exportToJson(Flow flow, boolean includeDependencies) {
        Map<String, JsonNode> exports = new HashMap<>();

        String flowName = flow.getName();
        exports.put(FileSuffix.FLOW.prepend(flowName), serialize(flow));

        // We're going to track all the dependencies used by the forms in
        // this flow. This object will be updated by the form exporter for
        // each form it encounters.
        References refs = new References();

        List<Form> forms = flow.getForms();
        if (CollectionUtils.isNotEmpty(forms)) {
            for (Form form : forms) {
                exports.putAll(formExporter.exportToJson(form, refs));
            }
        }

        if (!includeDependencies) {
            return exports;
        }

        for (FileSuffix fs : FileSuffix.values()) {
            for (FlowSpecificExporter exporter : exporters) {
                if (exporter.canExport(fs)) {
                    exports.putAll(exporter.serializeEntitiesForFlow(flow, refs));
                }
            }

        }

        return exports;
    }

    private ObjectNode serialize(Flow entity) {
        ObjectNode node = mapper.valueToTree(entity);
        // Remove everything but the uid in product config.
        // that's the only value we need.
        node.replace("productConfig", factory.objectNode()
                .put("uid", node.path("productConfig").path("uid").asText()));
        return node;
    }

}
