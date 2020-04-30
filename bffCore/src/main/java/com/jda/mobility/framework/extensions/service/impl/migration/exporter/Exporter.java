package com.jda.mobility.framework.extensions.service.impl.migration.exporter;

import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.jda.mobility.framework.extensions.service.impl.migration.FileSuffix;

/**
 * Defines the interface that all 'basic' exporters must adhere to.
 * <p>
 * Exporters are responsible for serializing database entities into
 * JSON. Each individual exporter implementation decides which entities
 * it exports and the manner in which the entities to export are
 * determined.
 */
public interface Exporter {

    /**
     * @return Indicates which type of entity is exported by an implementation.
     */
    FileSuffix fileSuffix();

    /**
     * Implementations should use this method to fetch the appropriate entities
     * to export and serialize them into JSON.
     * @param level If an entity (or entities) are tied to a particular level
     * (not all entities are), this value will indicate which level is being
     * requested. In general, exporters should filter out entities that don't
     * belong to this level from the serialized output.
     * @return An arbitrary JSON structure representing the entities that were
     * exported. This structure may be 'empty', but should <em>not</em> be {@code
     * null}.
     */
    JsonNode serializeExportableEntities(UUID level);
}
