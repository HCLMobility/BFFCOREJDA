package com.jda.mobility.framework.extensions.service.impl.migration.importer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.service.impl.migration.FileSuffix;

/**
 * Defines the interface that all 'Basic' importer classes should adhere to.
 * <p>
 * Basic importers are expected to be able to import a list of entities where
 * the entities are serialized in a JSON array.
 * @param <T> The type of the entity to be imported.
 */
public interface BasicImporter<T> {

    /**
     * Indicates if the importer is able to import entities from
     * files that have the specified suffix.
     * @param suffix The suffix in question. ({@code {name}.{type}.json})
     * @return {@code true} if the importer can handle the file.
     */
    boolean canImport(FileSuffix suffix);

    /**
     * Used by the {@link #objectMapper() object mapper} to deserialize
     * entities from JSON.
     * @return A {@code TypeReference} to a {@link List} of type {@code T}.
     */
    TypeReference<List<T>> deserializationTarget();

    /**
     * @return The {@code mapper} that should be used to use to deserialize
     * entities from JSON
     */
    ObjectMapper objectMapper();

    /**
     * Implementations should deserialize entities from the passed in bytes
     * and persist them in the database as appropriate for that entity type.
     * <p>
     * Any issues encountered during import should be added to the passed in
     * {@code context}. The implementation may throw exceptions, but it
     * is ok to just add problems to the context. If the context contains
     * errors at the end of the import process, any database changes will
     * be rolled back.
     *
     * @see #deserialize(byte[], ImportContext)
     * @param contents Contains the serialized JSON entities to import.
     * @param fileToImport The source file the bytes originated from
     * @param context Add any problems to this list.
     */
    void doImport(byte[] contents, Path fileToImport, ImportContext context);

    /**
     * The default implementation of this method uses {@link #objectMapper()},
     * and {@link #deserializationTarget()} to deserialize the bytes into
     * a list of entities.
     * <p>
     * Any problems encountered during deserialization will be added to the
     * issues list and an empty {@link List} will be returned.
     * @param contents Contains the JSON to deserialize
     * @param context Deserialization problems will be added to this context.
     * @return A list of entities. This may be empty, but it will never be
     * {@code null}.
     */
    default List<T> deserialize(byte[] contents, ImportContext context) {
        try {
            return objectMapper().readValue(contents, deserializationTarget());
        }
        catch (IOException e) {
            context.raise("Unable to parse file.", e);
        }
        return Collections.emptyList();
    }
}
