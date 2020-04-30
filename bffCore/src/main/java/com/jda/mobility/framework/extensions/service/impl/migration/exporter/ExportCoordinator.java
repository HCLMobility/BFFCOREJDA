package com.jda.mobility.framework.extensions.service.impl.migration.exporter;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import com.jda.mobility.framework.extensions.entity.RoleMaster;
import com.jda.mobility.framework.extensions.entity.UserRole;
import com.jda.mobility.framework.extensions.repository.UserRoleRepository;
import com.jda.mobility.framework.extensions.service.ExportService;
import com.jda.mobility.framework.extensions.service.impl.migration.FileSuffix;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ExportType;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

/**
 * Exports various entities as {@link JsonNode}s.
 */
@Service
public class ExportCoordinator implements ExportService {

    private final Multimap<FileSuffix, Exporter> globalExporters = HashMultimap.create();
    private final FlowExporter flowExporter;
    private final UserRoleRepository roles;

    public ExportCoordinator(Set<Exporter> globalExporters,
                             FlowExporter flowExporter,
                             UserRoleRepository roles) {
        this.flowExporter = flowExporter;
        this.roles = roles;
        cacheExportersByFileSuffix(globalExporters);
    }

    /**
     * Exports the requested types of entities
     * @param flowId If {@code toBeExported} is a single value of type {@link ExportType#FLOW}
     * this value <em>cannot</em> be {@code null}. It specifies which flow to export. Note:
     * a single invocation of this method can export at most one flow.
     * @param toBeExported A list of entity types to export. Note: the {@link ExportType#ALL}
     * will not export any flows. Additionally, if the type {@link ExportType#FLOW} is included
     * with any other type, it will be ignored.
     * @param userId The id of the user performing the export. This is used to limit the data
     * being exported to the level the user belongs to.
     * @return A map containing file names mapped to JSON containing the exported entities.
     * This map may be empty but it will <em>never</em> be {@code null}.
     */
    public Map<String, JsonNode> exportData(UUID flowId, ExportType[] toBeExported, String userId) {

        Map<String, JsonNode> exports = new HashMap<>();

        if (toBeExported.length == 1 && toBeExported[0] == ExportType.FLOW) {
            if (flowId == null) {
                throw new IllegalArgumentException("Flow export requested with no flowId specified.");
            }
            exports.putAll(flowExporter.exportToJson(flowId));
        }
        else {
            exports.putAll(exportNonFlow(toBeExported, userId));
        }

        return exports;
    }

    private Map<String, JsonNode> exportNonFlow(ExportType[] toBeExported, String userId) {

        UUID level = roles.findByUserId(userId)
                .map(UserRole::getRoleMaster)
                .map(RoleMaster::getUid)
                .orElseThrow(() -> new IllegalStateException("Unauthorized user " + userId + " cannot export"));

        Map<String, JsonNode> exports = new HashMap<>();

        for (ExportType type : toBeExported) {
            ImmutableSet<FileSuffix> suffixes = TYPES_TO_SUFFIXES.get(type);
            if (CollectionUtils.isEmpty(suffixes)) {
                throw new IllegalArgumentException("Export type " + type + " is not supported.");
            }

            for (FileSuffix suffix : suffixes) {
                Collection<Exporter> exporters = this.globalExporters.get(suffix);
                if (CollectionUtils.isEmpty(exporters)) {
                    throw new IllegalStateException("Export type " + type + " has no exporter.");
                }

                for (Exporter exporter : exporters) {
                    JsonNode jsonNode = exporter.serializeExportableEntities(level);
                    exports.put(suffix.prepend("All"), jsonNode);
                }
            }
        }

        return exports;
    }

    private void cacheExportersByFileSuffix(Set<Exporter> exporters) {
        for (FileSuffix suffix : FileSuffix.values()) {
            for (Exporter exporter : exporters) {
                if (exporter.fileSuffix() == suffix) {
                    globalExporters.put(suffix, exporter);
                }
            }
        }
    }

    /**
     * Each supported ExportType is mapped to one or more {@link FileSuffix}es. Exporters
     * are associated with FileSuffixes.
     */
    private static final ImmutableSetMultimap<ExportType, FileSuffix> TYPES_TO_SUFFIXES =
            ImmutableSetMultimap.<ExportType, FileSuffix>builder()
                    .putAll(ExportType.ALL, List.of(
                            FileSuffix.CUSTOM_COMPONENT,
                            FileSuffix.RESOURCE_BUNDLE,
                            FileSuffix.API,
                            FileSuffix.API_REGISTRY,
                            FileSuffix.MENU,
                            FileSuffix.APP_CONFIG
                    ))
                    .put(ExportType.CUSTOM_CONTROL, FileSuffix.CUSTOM_COMPONENT)
                    .put(ExportType.MENU, FileSuffix.MENU)
                    .put(ExportType.APP_CONFIG, FileSuffix.APP_CONFIG)
                    .put(ExportType.RESOURCE_BUNDLE, FileSuffix.RESOURCE_BUNDLE)
                    .putAll(ExportType.REGISTRY, List.of(FileSuffix.API, FileSuffix.API_REGISTRY))
                    .build();
}
