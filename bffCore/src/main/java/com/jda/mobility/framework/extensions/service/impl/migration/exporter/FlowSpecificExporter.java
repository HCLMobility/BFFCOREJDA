package com.jda.mobility.framework.extensions.service.impl.migration.exporter;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.service.impl.migration.FileSuffix;
import com.jda.mobility.framework.extensions.service.impl.migration.References;

/**
 * Defines the interface that all exporters must adhere to if they
 * wish to export normal entities that are tied to specific flows.
 * <p>
 * Unlike the more global {@link Exporter} interface, implementations
 * of this interface are meant to find an export only the entities
 * upon which a specific flow depends.
 */
public interface FlowSpecificExporter {

    /**
     * Implementations should use this method to fetch the appropriate entities
     * for the specified flow and serialize them into JSON.
     *
     * @param flow The flow being exported. Implementations should
     * use this flow (and the level to which it belongs) to determine
     * which entities should be exported.
     * @param references Contains references to entities the flow
     * depends on. Implementations should use the references to
     * determine which specific entities they should export.
     * @return A map of zero or more file name to serialized json
     * mappings. Each entry in the map represents a file containing
     * exported entities of a specific type. This map may be empty
     * but should <em>never</em> be {@code null}.
     */
    Map<String, JsonNode> serializeEntitiesForFlow(Flow flow, References references);

    /**
     * @return Indicates which if the exporter can export the particular entity
     * for a flow.
     */
    boolean canExport(FileSuffix suffix);
}
