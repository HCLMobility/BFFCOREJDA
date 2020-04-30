package com.jda.mobility.framework.extensions.service.impl.migration.exporter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.entity.ApiMaster;
import com.jda.mobility.framework.extensions.entity.ApiRegistry;
import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.repository.ApiMasterRepository;
import com.jda.mobility.framework.extensions.service.impl.migration.ApiKey;
import com.jda.mobility.framework.extensions.service.impl.migration.FileSuffix;
import com.jda.mobility.framework.extensions.service.impl.migration.References;
import com.jda.mobility.framework.extensions.service.impl.migration.exporter.mixin.Mixins;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * Exporter for {@link ApiMaster APIs}.
 * <p>
 * Exported APIs refer to their owning {@link ApiRegistry}s but
 * do not include the actual definitions of those registries.
 * The {@link RegistryExporter registry exporter} is responsible
 * for exporting registry data.
 */
@Service
public class ApiExporter implements Exporter, FlowSpecificExporter {

    private final ApiMasterRepository repo;
    private final ObjectMapper mapper;

    public ApiExporter(ApiMasterRepository repo) {
        this.repo = repo;
        this.mapper = new ObjectMapper()
                .addMixIn(ApiMaster.class, Mixins.Api.class)
                .addMixIn(ApiRegistry.class, Mixins.OwnerRegistry.class);
    }

    @Override
    public FileSuffix fileSuffix() {
        return FileSuffix.API;
    }

    @Override
    public boolean canExport(FileSuffix suffix) {
        return suffix == fileSuffix();
    }

    @Override
    public JsonNode serializeExportableEntities(UUID level) {
        // Only APIs for the requested level are included.
        // Additionally, APIs are always sorted by name and version
        // to provide consistent output.
        List<ApiMaster> apis = repo.findAll(Sort.by("name", "version"))
                .stream()
                .filter(api -> api.getApiRegistry().getRoleMaster().getUid() == level)
                .collect(Collectors.toList());

        return serializeEntities(apis);
    }


    @Override
    public Map<String, JsonNode> serializeEntitiesForFlow(Flow flow, References references) {
        // The References object will contain refs to all the APIs used by the
        // flow, but some of those APIs may be part of lower layers. We don't
        // want to include those APIs in the export for this form since that
        // will mix layer data.
        UUID level = flow.getProductConfig().getRoleMaster().getUid();

        List<ApiMaster> apisInUse = references.apis.stream()
                // It's possible there were invalid API references, so first
                // ensure that the refs are not obviously invalid.
                .filter(ApiKey::isValid)
                .map(api ->
                        repo.findByRequestEndpointAndRequestMethodAndApiRegistryNameAndApiRegistryApiType(
                                api.endpoint, api.method, api.registryName, api.type)
                )
                .flatMap(Optional::stream)
                // Remove APIs that are not in the same level as the specified flow
                .filter(api -> level.equals(api.getApiRegistry().getRoleMaster().getUid()))
                .sorted(Comparator.comparing(ApiMaster::getName).thenComparing(ApiMaster::getVersion))
                .collect(Collectors.toList());

        return Map.of(fileSuffix().prepend(flow.getName()), serializeEntities(apisInUse));
    }

    private JsonNode serializeEntities(List<ApiMaster> apis) {
        return mapper.valueToTree(apis);
    }
}
