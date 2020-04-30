package com.jda.mobility.framework.extensions.service.impl.migration.exporter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.entity.MenuMaster;
import com.jda.mobility.framework.extensions.repository.MenuMasterRepository;
import com.jda.mobility.framework.extensions.service.impl.migration.FileSuffix;
import com.jda.mobility.framework.extensions.service.impl.migration.References;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * Exports {@link MenuMaster menu} entities.
 * <p>
 * Menu {@code UUID}s are included in the exports.
 */
@Service
public class MenuExporter implements Exporter, FlowSpecificExporter {

    private final MenuMasterRepository repo;
    private final ObjectMapper mapper;

    public MenuExporter(MenuMasterRepository repo, ObjectMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public FileSuffix fileSuffix() {
        return FileSuffix.MENU;
    }

    @Override
    public boolean canExport(FileSuffix suffix) {
        return suffix == fileSuffix();
    }

    @Override
    public JsonNode serializeExportableEntities(UUID level) {
        return serializeEntities(repo.findAllByLinkedFormIdIsNullOrderByMenuName());
    }

    @Override
    public Map<String, JsonNode> serializeEntitiesForFlow(Flow flow, References references) {
        List<MenuMaster> menuEntities = references.menuIds.stream()
                .filter(StringUtils::isNotBlank)
                .map(UUID::fromString)
                .map(repo::findById)
                .flatMap(Optional::stream)
                .sorted(Comparator.comparing(MenuMaster::getMenuName))
                .collect(Collectors.toList());

        return Map.of(fileSuffix().prepend(flow.getName()), serializeEntities(menuEntities));
    }

    private JsonNode serializeEntities(List<MenuMaster> entities) {
        return mapper.valueToTree(entities);
    }
}
