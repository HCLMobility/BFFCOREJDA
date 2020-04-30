package com.jda.mobility.framework.extensions.service.impl.migration.importer;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.entity.ApiRegistry;
import com.jda.mobility.framework.extensions.repository.ApiRegistryRepository;
import com.jda.mobility.framework.extensions.service.impl.migration.FileSuffix;
import org.springframework.stereotype.Service;

/**
 * Imports {@link ApiRegistry registry} entities.
 * <p>
 * Apis are <em>not</em> imported by this class.
 * They are handled by {@link ApiImporter}. This importer
 * must run before the {@code ApiImporter} to ensure that any
 * registries referred to in the imported apis already exist.
 * The {@link ImportCoordinator} is responsible for the ordering
 * of the imports.
 * <p>
 * Registry {@code UUID}s are not included in the imports and are not
 * relevant. Two different databases may contain the same registry tied
 * to two different {@code UUID}s. This is ok. When looking for existing
 * registries, the other unique properties of registries are used.
 */
@Service
public class ApiRegistryImporter implements BasicImporter<ApiRegistry> {

    private final ApiRegistryRepository repo;
    private final ObjectMapper mapper;

    public ApiRegistryImporter(ApiRegistryRepository repo,
                               ObjectMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public boolean canImport(FileSuffix suffix) {
        return suffix == FileSuffix.API_REGISTRY;
    }

    @Override
    public TypeReference<List<ApiRegistry>> deserializationTarget() {
        return TYPE_REFERENCE;
    }

    @Override
    public ObjectMapper objectMapper() {
        return mapper;
    }

    @Override
    public void doImport(byte[] contents, Path fileToImport, ImportContext context) {
        deserialize(contents, context)
                .forEach(registry -> {
                    Optional<ApiRegistry> existingRegistry =
                            repo.findByApiTypeAndNameAndRoleMaster_name(registry.getApiType(), registry.getName(),
                                    registry.getRoleMaster().getName());

                    if (existingRegistry.isEmpty()) {
                        // If we don't already have the registry, add it.
                        repo.save(registry);
                    }
                    else {
                        // If the registry already exists, we have nothing else to do.
                        // Note: In the future it may make sense to update the existing
                        // registry properties to match the imported ones, but currently
                        // they aren't used for anything important, so we'll just ignore
                        // them.
                        context.note("Registry " + registry.getName() +
                                " already exists. Leaving registry untouched.");
                    }
                });
    }

    private static final TypeReference<List<ApiRegistry>> TYPE_REFERENCE = new TypeReference<>() {};
}
