package com.jda.mobility.framework.extensions.service.impl.migration.importer;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.github.fge.jsonpatch.JsonPatch;
import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.entity.Form;
import com.jda.mobility.framework.extensions.service.impl.migration.FileSuffix;
import com.jda.mobility.framework.extensions.service.impl.migration.exporter.FlowExporter;
import com.jda.mobility.framework.extensions.service.impl.migration.exporter.FormExporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ImporterTest
@Import({FlowExporter.class, FormExporter.class})
class FlowDiffToolTest {

    @Autowired
    private FlowExporter flowExporter;

    @Autowired
    private ObjectMapper mapper;

    @Value(Resources.PATH + "formFlow/good.flow.json")
    private Resource goodFlow;

    @Value(Resources.PATH + "formFlow/good-form1.form.json")
    private Resource goodForm1;

    @Value(Resources.PATH + "formFlow/good-form2.form.json")
    private Resource goodForm2;

    private FlowDiffTool diffTool;

    @BeforeEach
    void setUp() {
        diffTool = new FlowDiffTool(flowExporter);
    }

    @Test
    void addNewFlowNoForms() {
        Flow newFlow = read(goodFlow, Flow.class);
        Map<String, JsonPatch> diffs = diffTool.diff(null, newFlow);

        assertDiffContains(
                List.of(createAddUidOperation(newFlow.getUid())),
                diffs.get(FileSuffix.FLOW.prepend(newFlow.getName()))
        );
    }

    @Test
    void addFlowNewForms() {
        Flow newFlow = read(goodFlow, Flow.class);
        Form newForm1 = read(goodForm1, Form.class);
        Form newForm2 = read(goodForm2, Form.class);

        newFlow.addForm(newForm1);
        newFlow.addForm(newForm2);

        Map<String, JsonPatch> diffs = diffTool.diff(null, newFlow);

        assertDiffContains(
                List.of(createAddUidOperation(newFlow.getUid())),
                diffs.get(FileSuffix.FLOW.prepend(newFlow.getName()))
        );

        assertDiffContains(
                List.of(createAddUidOperation(newForm1.getUid())),
                diffs.get(FileSuffix.FORM.prepend(newForm1.getName()))
        );

        assertDiffContains(
                List.of(createAddUidOperation(newForm2.getUid())),
                diffs.get(FileSuffix.FORM.prepend(newForm2.getName()))
        );


    }

    @Test
    void removeFlowForms() {
        Flow newFlow = read(goodFlow, Flow.class);
        Form newForm1 = read(goodForm1, Form.class);
        Form newForm2 = read(goodForm2, Form.class);

        newFlow.addForm(newForm1);
        newFlow.addForm(newForm2);

        Map<String, JsonPatch> diffs = diffTool.diff(newFlow, read(goodFlow, Flow.class));

        assertEquals(JsonNodeFactory.instance.arrayNode(),
                mapper.valueToTree(diffs.get(FileSuffix.FLOW.prepend(newFlow.getName()))));

        assertDiffContains(
                List.of(createRemoveUidOperation()),
                diffs.get(FileSuffix.FORM.prepend(newForm1.getName()))
        );

        assertDiffContains(
                List.of(createRemoveUidOperation()),
                diffs.get(FileSuffix.FORM.prepend(newForm2.getName()))
        );

    }

    private void assertDiffContains(List<JsonNode> expectedOps, JsonPatch actualDiff) {
        assertNotNull(actualDiff, "There should be a diff present for the flow");
        ArrayNode operations = mapper.valueToTree(actualDiff);

        expectedOps.forEach(expected -> {
            matchOp: {
                for (JsonNode actual : operations) {
                    if (expected.equals(actual)) {
                        break matchOp;
                    }
                }
                fail("expected operation : " + expected + " not found in " + operations);
            }
        });
    }

    private <T> T read(Resource resource, Class<T> type) {
        byte[] bytes = ImporterUtils.read(resource);
        try {
            return mapper.readValue(bytes, type);
        }
        catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }


    private JsonNode createRemoveUidOperation() {
        return JsonNodeFactory.instance.objectNode()
                .put("op", "remove")
                .put("path", "/uid");
    }

    private JsonNode createAddUidOperation(UUID uid) {
        return JsonNodeFactory.instance.objectNode()
                .put("op", "add")
                .put("path", "/uid")
                .put("value", uid.toString());
    }


}