package com.jda.mobility.framework.extensions.service.impl.migration.importer;

import java.nio.file.Path;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.entity.ResourceBundle;
import com.jda.mobility.framework.extensions.repository.ResourceBundleRepository;
import com.jda.mobility.framework.extensions.service.impl.migration.FileSuffix;
import org.springframework.stereotype.Service;

/**
 * Imports {@link ResourceBundle resource bundle} entities.
 * <p>
 * Bundle {@code UUID}s are not maintained during export or
 * import. The same bundle in two different systems will likely
 * have different {@code UUID}s. Matching of bundles is done
 * using their other uniquely identifying properties.
 */
@Service
public class ResourceBundleImporter implements BasicImporter<ResourceBundle> {

    private final ResourceBundleRepository repo;
    private final ObjectMapper mapper;

    public ResourceBundleImporter(ResourceBundleRepository repo,
                                  ObjectMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    public void doImport(byte[] contents, Path fileToImport, ImportContext context) {
        deserialize(contents, context)
                .forEach(rb -> {
                    ResourceBundle existingRb = repo.findByLocaleAndRbkeyAndType(
                            rb.getLocale(), rb.getRbkey(), rb.getType());
                    if (existingRb != null) {
                        existingRb.setRbvalue(rb.getRbvalue());
                    }
                    else {
                        repo.save(rb);
                    }
                });
    }

    @Override
    public boolean canImport(FileSuffix suffix) {
        return suffix == FileSuffix.RESOURCE_BUNDLE;
    }

    @Override
    public TypeReference<List<ResourceBundle>> deserializationTarget() {
        return TYPE_REFERENCE;
    }

    @Override
    public ObjectMapper objectMapper() {
        return mapper;
    }

    private static final TypeReference<List<ResourceBundle>> TYPE_REFERENCE = new TypeReference<>() {};
}
