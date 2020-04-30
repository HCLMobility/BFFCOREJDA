package com.jda.mobility.framework.extensions.service.impl.migration.exporter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.entity.ApiRegistry;
import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.repository.ApiRegistryRepository;
import com.jda.mobility.framework.extensions.service.impl.migration.FileSuffix;
import com.jda.mobility.framework.extensions.service.impl.migration.References;
import com.jda.mobility.framework.extensions.service.impl.migration.exporter.mixin.Mixins;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

/**
 * Exports {@link ApiRegistry registry} entities.
 * <p>
 * Exported registries do <em>not</em> include any
 * APIs they contain. APIs are exported separately
 * using {@link ApiExporter}. {@code UUID}s are
 * <em>not</em> included in the exported data.
 */
@Service
public class RegistryExporter implements Exporter, FlowSpecificExporter {

    private final ApiRegistryRepository repo;
    private final ObjectMapper mapper;

    public RegistryExporter(ApiRegistryRepository repo) {
        this.repo = repo;
        this.mapper = new ObjectMapper()
                .addMixIn(ApiRegistry.class, Mixins.ApiRegistry.class);
    }


    @Override
    public FileSuffix fileSuffix() {
        return FileSuffix.API_REGISTRY;
    }

    @Override
    public boolean canExport(FileSuffix suffix) {
        return suffix == fileSuffix();
    }

    @Override
    public JsonNode serializeExportableEntities(UUID level) {
        return serializeEntities(repo.findByRoleMasterUidOrderByName(level));
    }

    @Override
    public Map<String, JsonNode> serializeEntitiesForFlow(Flow flow, References references) {
        // We only want to include registries that belong to the same
        // level as the flow being exported. Flows shouldn't have
        // references to any apis in a higher layer and apis in a
        // lower layer should be exported and imported by users that
        // manage that layer.
        String levelName = flow.getProductConfig().getRoleMaster().getName();

        List<ApiRegistry> registries = references.apis.stream()
                .map(api -> Pair.of(api.type, api.registryName))
                .distinct()
                .map(pair -> repo.findByApiTypeAndNameAndRoleMaster_name(pair.getLeft(), pair.getRight(), levelName))
                .flatMap(Optional::stream)
                .sorted(Comparator.comparing(ApiRegistry::getName))
                .collect(Collectors.toList());

        return Map.of(fileSuffix().prepend(flow.getName()), serializeEntities(registries));
    }

    private JsonNode serializeEntities(List<ApiRegistry> entities) {
        return mapper.valueToTree(entities);
    }
}
