package com.jda.mobility.framework.extensions.service.impl.migration.importer;

import java.nio.file.Path;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.entity.CustomComponentMaster;
import com.jda.mobility.framework.extensions.repository.CustomComponentMasterRepository;
import com.jda.mobility.framework.extensions.service.impl.migration.FileSuffix;
import org.springframework.stereotype.Service;

/**
 * Deserializes custom components from JSON and saves
 * them in the database.
 * <p>
 * All custom components and their sub components retain
 * their {@code UUID}s during import.
 */
@Service
public class CustomComponentImporter implements BasicImporter<CustomComponentMaster> {

    private final CustomComponentMasterRepository repo;
    private final ObjectMapper mapper;

    public CustomComponentImporter(CustomComponentMasterRepository repo,
                                   ObjectMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public boolean canImport(FileSuffix suffix) {
        return suffix == FileSuffix.CUSTOM_COMPONENT;
    }

    @Override
    public TypeReference<List<CustomComponentMaster>> deserializationTarget() {
        return TYPE_REFERENCE;
    }

    @Override
    public ObjectMapper objectMapper() {
        return mapper;
    }

    @Override
    public void doImport(byte[] contents, Path fileToImport, ImportContext context) {
        deserialize(contents, context)
                .forEach(repo::save);
    }

    private static final TypeReference<List<CustomComponentMaster>> TYPE_REFERENCE = new TypeReference<>() {};
}
