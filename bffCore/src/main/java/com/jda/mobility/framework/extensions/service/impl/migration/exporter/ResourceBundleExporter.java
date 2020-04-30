package com.jda.mobility.framework.extensions.service.impl.migration.exporter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.entity.ResourceBundle;
import com.jda.mobility.framework.extensions.repository.ResourceBundleRepository;
import com.jda.mobility.framework.extensions.service.impl.migration.FileSuffix;
import com.jda.mobility.framework.extensions.service.impl.migration.References;
import com.jda.mobility.framework.extensions.service.impl.migration.exporter.mixin.Mixins;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ResourceBundleType;
import org.springframework.stereotype.Service;

/**
 * Exports {@link ResourceBundle resource bundle} entities.
 * <p>
 * Only entities of type {@link ResourceBundleType#ADMIN_UI} are
 * included in the export since this is the only type that is
 * creatable from the UI. Additionally, {@code UUID}s are <em>not</em>
 * included in the export since resource bundles are shared globally
 * but their {@code UUID}s are generated per system.
 */
@Service
public class ResourceBundleExporter implements Exporter, FlowSpecificExporter {

    private final ResourceBundleRepository repo;
    private final ObjectMapper mapper;

    public ResourceBundleExporter(ResourceBundleRepository repo) {
        this.repo = repo;
        this.mapper = new ObjectMapper()
                .addMixIn(ResourceBundle.class, Mixins.ResourceBundle.class);
    }

    @Override
    public FileSuffix fileSuffix() {
        return FileSuffix.RESOURCE_BUNDLE;
    }

    @Override
    public boolean canExport(FileSuffix suffix) {
        return suffix == fileSuffix();
    }

    @Override
    public JsonNode serializeExportableEntities(UUID level) {
        List<ResourceBundle> bundles =
                repo.findByTypeOrderByLocaleAscRbkeyAscRbvalueAsc(ResourceBundleType.ADMIN_UI.getType());
        return serializeEntities(bundles);
    }

    private JsonNode serializeEntities(List<ResourceBundle> entities) {
        return mapper.valueToTree(entities);
    }

    @Override
    public Map<String, JsonNode> serializeEntitiesForFlow(Flow flow, References references) {
        List<ResourceBundle> bundles = repo.findByRbkeyIn(references.resourceBundleKeys);
        bundles.sort(COMPARATOR);
        return Map.of(fileSuffix().prepend(flow.getName()), serializeEntities(bundles));
    }

    private static final Comparator<ResourceBundle> COMPARATOR =
            Comparator.comparing(ResourceBundle::getLocale)
                    .thenComparing(ResourceBundle::getRbkey)
                    .thenComparing(ResourceBundle::getRbvalue);

}
