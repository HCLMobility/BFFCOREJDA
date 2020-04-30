package com.jda.mobility.framework.extensions.service.impl.migration.importer;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.diff.JsonDiff;
import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.service.impl.migration.FileSuffix;
import com.jda.mobility.framework.extensions.service.impl.migration.exporter.FlowExporter;
import org.springframework.stereotype.Service;

/**
 * This tool produces a diff in the form of a {@link JsonPatch}
 * between two flows.
 * <p>
 * The full diff may include changes in multiple files (one for
 * the flow and one for each form in that flow).
 * <p>
 * A {@link JsonPatch} consists of a list of operations
 * (such as {@code add} or {@code remove}) that when applied
 * would transform the starting flow object into the ending
 * flow object.
 * <p>
 * A {@link FlowExporter} is used to produce the JSON for the
 * starting and ending form states that will be diffed.
 * <p>
 * For more information on JsonPatch see
 * <ul>
 *      <li><a href="https://github.com/java-json-tools/json-patch">https://github.com/java-json-tools/json-patch</a></li>
 *      <li><a href="http://jsonpatch.com/">http://jsonpatch.com</a></li>
 * </ul>
 */
@Service
public class FlowDiffTool {

    private final FlowExporter flowExporter;

    public FlowDiffTool(FlowExporter flowExporter) {
        this.flowExporter = flowExporter;
    }

    /**
     * Produces a collection of {@link JsonPatch}es (one
     * for the flow and one for each of the flow's forms)
     * that when applied to the current flow, will transform
     * it into the target flow.
     *
     * @param current The current state of the flow. It is
     * ok to pass {@code null} here if there is no current
     * flow.
     * @param target The new desired state of the same flow.
     * It is ok to pass {@code null} here if the desired state
     * is the removal of the flow.
     */
    public Map<String, JsonPatch> diff(Flow current, Flow target) {
        Map<String, JsonPatch> diffs = new HashMap<>();
        Map<String, JsonNode> start = serialize(current);
        Map<String, JsonNode> end = serialize(target);

        // First loop over all the exported nodes from the current state
        // and diff them against the desired end state.
        start.forEach((name, startJson) -> {
            // Remove matching nodes from the desired end state. We're
            // going to handle any remaining end state nodes separately
            // as those represent brand-new nodes that don't exist in
            // the current state.
            JsonNode endJson = end.remove(name);
            if (endJson == null) {
                // If there is no end state for a node, that means it
                // has been removed. We'll create an empty object node
                // to diff against, which will produce a bunch of 'remove'
                // operations.
                endJson = JsonNodeFactory.instance.objectNode();
            }
            diffs.put(name, JsonDiff.asJsonPatch(startJson, endJson));
        });

        // Any nodes left in end represent new nodes. So we diff them against an
        // empty object node to produce the 'add' operations we want.
        end.forEach((name, endJson) -> {
            diffs.put(name, JsonDiff.asJsonPatch(JsonNodeFactory.instance.objectNode(), endJson));
        });

        return diffs;
    }

    private Map<String, JsonNode> serialize(Flow flow) {
        if (flow == null) {
            return Map.of();
        }

        Map<String, JsonNode> exports = flowExporter.exportToJson(flow, false);

        // Remove `publishedForm` property from all Form nodes. It's not diffable.
        exports.forEach((key, node) -> {
            FileSuffix.from(key).ifPresent(suffix -> {
                if (suffix == FileSuffix.FORM && node.isObject()) {
                    ObjectNode form = (ObjectNode) node;
                    form.remove("publishedForm");
                }
            });
        });

        return exports;
    }


}
