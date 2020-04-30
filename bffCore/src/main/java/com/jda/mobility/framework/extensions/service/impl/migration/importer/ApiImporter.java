package com.jda.mobility.framework.extensions.service.impl.migration.importer;

import java.nio.file.Path;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.entity.ApiMaster;
import com.jda.mobility.framework.extensions.entity.ApiRegistry;
import com.jda.mobility.framework.extensions.repository.ApiMasterRepository;
import com.jda.mobility.framework.extensions.repository.ApiRegistryRepository;
import com.jda.mobility.framework.extensions.service.impl.migration.FileSuffix;
import org.springframework.stereotype.Service;

/**
 * Imports {@link ApiMaster api} entities.
 * <p>
 * Registries are <em>not</em> imported by this class.
 * They are handled by {@link ApiRegistryImporter}. The {@code
 * ApiImporter} depends on the {@code ApiRegistryImporter} importer
 * being executed first to ensure that any registries referred to
 * in the imported apis already exist. The {@link ImportCoordinator}
 * is responsible for the ordering of the imports.
 * <p>
 * Api {@code UUID}s are not included in the imports and are not
 * relevant. Two different databases may contain the same Api tied
 * to two different {@code UUID}s. This is ok. When looking for
 * existing Apis, the other unique properties of Apis are used.
 */
@Service
public class ApiImporter implements BasicImporter<ApiMaster> {

    private final ApiMasterRepository apis;
    private final ApiRegistryRepository registries;
    private final ObjectMapper mapper;

    public ApiImporter(ApiMasterRepository apis,
                       ApiRegistryRepository registries,
                       ObjectMapper mapper) {
        this.apis = apis;
        this.registries = registries;
        this.mapper = mapper;
    }

    @Override
    public boolean canImport(FileSuffix suffix) {
        return suffix == FileSuffix.API;
    }

    @Override
    public TypeReference<List<ApiMaster>> deserializationTarget() {
        return TYPE_REFERENCE;
    }

    @Override
    public ObjectMapper objectMapper() {
        return mapper;
    }

    @Override
    public void doImport(byte[] contents, Path fileToImport, ImportContext context) {
        deserialize(contents, context)
                .forEach(api -> {
                    ApiRegistry owner = api.getApiRegistry();
                    String roleName = owner.getRoleMaster().getName();
                    String registryType = owner.getApiType();
                    String registryName = owner.getName();

                    // Check to make sure the registry exists before we save the api.
                    // If the registry isn't present, we cannot save the api.
                    registries.findByApiTypeAndNameAndRoleMaster_name(registryType, registryName, roleName).ifPresentOrElse(
                            registry -> saveApi(registry, api),
                            () -> context.raise("Cannot add api " + api.getName() +
                                    ". Owning registry " + registryName + " does not exist.")
                    );
                });

    }

    private void saveApi(ApiRegistry registry, ApiMaster api) {
        apis.findByApiRegistryAndName(registry, api.getName()).ifPresentOrElse(
                existingApi -> {
                    existingApi.setRequestBody(api.getRequestBody());
                    existingApi.setRequestEndpoint(api.getRequestEndpoint());
                    existingApi.setRequestMethod(api.getRequestMethod());
                    existingApi.setRequestQuery(api.getRequestQuery());
                    existingApi.setRequestPathparams(api.getRequestPathparams());
                    existingApi.setResponseSchema(api.getResponseSchema());
                },
                () -> {
                    api.setApiRegistry(registry);
                    apis.save(api);
                }
        );
    }

    private static final TypeReference<List<ApiMaster>> TYPE_REFERENCE = new TypeReference<>() {};
}
