package com.jda.mobility.framework.extensions.service.impl.migration.exporter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.entity.CustomComponentMaster;
import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.entity.ProductConfig;
import com.jda.mobility.framework.extensions.repository.CustomComponentMasterRepository;
import com.jda.mobility.framework.extensions.repository.ProductConfigRepository;
import com.jda.mobility.framework.extensions.service.impl.migration.FileSuffix;
import com.jda.mobility.framework.extensions.service.impl.migration.References;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * Exports {@link CustomComponentMaster custom component} entities.
 * <p>
 * Custom component entities (and their linked children) are exported
 * with {@code UUID}s included.
 */
@Service
public class CustomComponentExporter implements Exporter, FlowSpecificExporter {

    private final CustomComponentMasterRepository repo;
    private final ProductConfigRepository productConfigs;
    private final ObjectMapper mapper;

    public CustomComponentExporter(CustomComponentMasterRepository repo,
                                   ProductConfigRepository productConfigs,
                                   ObjectMapper mapper) {
        this.repo = repo;
        this.productConfigs = productConfigs;
        this.mapper = mapper;
    }


    @Override
    public FileSuffix fileSuffix() {
        return FileSuffix.CUSTOM_COMPONENT;
    }

    @Override
    public boolean canExport(FileSuffix suffix) {
        return suffix == fileSuffix();
    }

    @Override
    public JsonNode serializeExportableEntities(UUID level) {
        Set<UUID> allowedProducts = productConfigs.findByRoleMasterUid(level)
                .stream()
                .map(ProductConfig::getUid)
                .collect(Collectors.toSet());

        return serializeEntities(repo.findByProductConfigIdIn(allowedProducts));
    }

    @Override
    public Map<String, JsonNode> serializeEntitiesForFlow(Flow flow, References references) {
        UUID uid = flow.getProductConfig().getUid();
        List<CustomComponentMaster> components = references.customComponentIds.stream()
                .filter(StringUtils::isNotBlank)
                .map(UUID::fromString)
                .map(repo::findById)
                .flatMap(Optional::stream)
                // Only include custom components for the product config
                // that matches the flow being exported
                .filter(c -> c.getProductConfigId().equals(uid))
                .collect(Collectors.toList());

        return Map.of(fileSuffix().prepend(flow.getName()), serializeEntities(components));
    }

    private JsonNode serializeEntities(List<CustomComponentMaster> entities) {
        entities.sort(Comparator.comparing(CustomComponentMaster::getName));
        return mapper.valueToTree(entities);
    }
}
