package com.jda.mobility.framework.extensions.service.impl.migration.exporter;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jda.mobility.framework.extensions.entity.AppConfigDetail;
import com.jda.mobility.framework.extensions.entity.AppConfigMaster;
import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.repository.AppConfigMasterRepository;
import com.jda.mobility.framework.extensions.service.impl.migration.FileSuffix;
import com.jda.mobility.framework.extensions.service.impl.migration.References;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import static com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.AppCfgRequestType.APPLICATION;

/**
 * Exports {@link AppConfigMaster app config} entities.
 * <p>
 * This exporter can export to {@code *.app-configs.json},
 * {@code *.global-vars.json} and {@code *.context-vals.json} files.
 * <p>
 * AppConfig master (and {@link AppConfigDetail detail}) {@code UUID}s
 * are not included in export files since the {@code UUID}s are generated
 * and may vary across systems.
 * <p>
 * The data model supports multiple detail records tied to a single
 * master, but at most <em>one</em> of those detail records should
 * be exported and imported. Detail records may be associated with
 * sessions to store global or context values for that session. Those
 * details should never be included in an export. This exporter
 * enforces that either no detail is included in the export or only
 * a single detail is present for each config.
 */
@Service
public class AppConfigExporter implements Exporter, FlowSpecificExporter {

    private final AppConfigMasterRepository repo;

    public AppConfigExporter(AppConfigMasterRepository repo) {
        this.repo = repo;
    }

    @Override
    public FileSuffix fileSuffix() {
        return FileSuffix.APP_CONFIG;
    }

    @Override
    public boolean canExport(FileSuffix suffix) {
        return suffix == fileSuffix() || suffix == FileSuffix.GLOBAL_VAR || suffix == FileSuffix.CONTEXT_VAL;
    }

    @Override
    public JsonNode serializeExportableEntities(UUID level) {
        return serializeEntities(repo.findAll(Sort.by("configType", "configName")));
    }

    @Override
    public Map<String, JsonNode> serializeEntitiesForFlow(Flow flow, References references) {
        String flowName = flow.getName();
        // For flow related exports only the `CONTEXT` and `GLOBAL` context
        // types are included. `APPLICATION` type configs are not form specific.
        // Technically, `GLOBAL` configs aren't either, but if a new global
        // is introduced for a flow, we want to be sure we include it.
        return Map.of(
                FileSuffix.CONTEXT_VAL.prepend(flowName),
                    serializeConfigsOfTypeWithNames("CONTEXT", references.contextVals),
                FileSuffix.GLOBAL_VAR.prepend(flowName),
                    serializeConfigsOfTypeWithNames("GLOBAL", references.globalVars)
        );
    }

    private JsonNode serializeConfigsOfTypeWithNames(String type, Collection<String> names) {
        List<AppConfigMaster> configs =
                repo.findByConfigNameInAndConfigType(names, type);

        // Since the type is constant, we only need to sort by name here.
        configs.sort(Comparator.comparing(AppConfigMaster::getConfigName));

        return serializeEntities(configs);
    }

    private JsonNode serializeEntities(List<AppConfigMaster> appConfigs) {
        JsonNodeFactory instance = JsonNodeFactory.instance;
        ArrayNode nodes = instance.arrayNode();

        appConfigs.forEach(master -> {
            ObjectNode masterNode = instance.objectNode()
                    .put("configName", master.getConfigName())
                    .put("configType", master.getConfigType());

            // In reality, detailNodes could be removed and we could tie the
            // master node directly to a single optional detail node. However,
            // the data model supports multiple details. So using an array here
            // simplifies the deserialization process on the import side.
            //
            // There is a larger issue with the design of the app config schema
            // where what are essentially constant values are stored in the same
            // place as extremely transactional values (session state), but that
            // is outside the scope of import/export.

            ArrayNode detailNodes = instance.arrayNode();
            List<AppConfigDetail> details = master.getAppConfigDetails();
            if (isApplicationConfig(master) && CollectionUtils.isNotEmpty(details)) {
                details.forEach(detail -> detailNodes.add(instance.objectNode()
                        .put("description", detail.getDescription())
                        .put("configValue", detail.getConfigValue())
                ));
            }

            if (!detailNodes.isEmpty()) {
                masterNode.set("appConfigDetails", detailNodes);
            }

            nodes.add(masterNode);
        });

        return nodes;
    }

    /**
     * Determines if the config record is an Application level config.
     */
    public static boolean isApplicationConfig(AppConfigMaster config) {
        return APPLICATION.getType().equals(config.getConfigType());
    }
}
