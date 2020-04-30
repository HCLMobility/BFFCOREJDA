package com.jda.mobility.framework.extensions.service.impl.migration.exporter;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.entity.Form;
import com.jda.mobility.framework.extensions.repository.FlowRepository;
import com.jda.mobility.framework.extensions.service.impl.migration.FileSuffix;
import com.jda.mobility.framework.extensions.service.impl.migration.References;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class FlowExporterTest {

    @Mock
    private FlowRepository repo;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void noFlow() {

        when(repo.findById(any(UUID.class))).thenReturn(Optional.empty());

        FlowExporter flowExporter = new FlowExporter(
                repo,
                new MockFormExporter(),
                Set.of(new MockExporter(Set.of()))
        );

        assertThrows(NoSuchElementException.class, () ->
                flowExporter.exportToJson(UUID.randomUUID()));
    }

    @Test
    void noForms() {
        String flowName = "empty";
        Flow flow = flow(flowName, UUID.randomUUID(), List.of());
        when(repo.findById(flow.getUid())).thenReturn(Optional.of(flow));

        FlowExporter flowExporter = new FlowExporter(
                repo,
                new MockFormExporter(),
                Set.of(new MockExporter(Set.of()))
        );

        Map<String, JsonNode> nodes = flowExporter.exportToJson(flow.getUid());

        JsonNode flowNode = nodes.get(FileSuffix.FLOW.prepend(flowName));

        assertFlowNode(flowName, flow.getUid(), flowNode);
        assertFalse(
                nodes.keySet().stream().anyMatch(key -> key.contains(FileSuffix.FORM.suffix())),
                "There should be no form nodes present in the map"
        );
        assertOtherNodes(flowName, nodes);

    }

    @Test
    void flowWithForms() {
        String flowName = "has-forms";
        Form form1 = form("one", UUID.randomUUID());
        Form form2 = form("two", UUID.randomUUID());
        Flow flow = flow(flowName, UUID.randomUUID(), List.of(form1, form2));

        when(repo.findById(flow.getUid())).thenReturn(Optional.of(flow));

        FlowExporter flowExporter = new FlowExporter(
                repo,
                new MockFormExporter(),
                Set.of(new MockExporter(Set.of(form1.getName(), form2.getName())))
        );

        Map<String, JsonNode> nodes = flowExporter.exportToJson(flow.getUid());

        JsonNode flowNode = nodes.get(FileSuffix.FLOW.prepend(flowName));

        assertFlowNode(flowName, flow.getUid(), flowNode);
        assertFormNode(form1.getName(), nodes);
        assertFormNode(form2.getName(), nodes);
        assertOtherNodes(flowName, nodes);
    }

    private void assertFlowNode(String name, UUID uid, JsonNode node) {
        assertEquals(name, node.path("name").asText());
        assertEquals(uid.toString(), node.path("uid").asText());
    }

    private void assertFormNode(String name, Map<String, JsonNode> nodes) {
        JsonNode jsonNode = nodes.get(FileSuffix.FORM.prepend(name));
        assertNotNull(jsonNode, "There should be a form node for " + name);
        assertEquals(FileSuffix.FORM.suffix(), jsonNode.asText());
    }

    private void assertOtherNodes(String flowName, Map<String, JsonNode> nodes) {
        assertNodeOf(flowName, FileSuffix.API, nodes);
        assertNodeOf(flowName, FileSuffix.API_REGISTRY, nodes);
        assertNodeOf(flowName, FileSuffix.CONTEXT_VAL, nodes);
        assertNodeOf(flowName, FileSuffix.GLOBAL_VAR, nodes);
        assertNodeOf(flowName, FileSuffix.CUSTOM_COMPONENT, nodes);
        assertNodeOf(flowName, FileSuffix.RESOURCE_BUNDLE, nodes);
        assertNodeOf(flowName, FileSuffix.MENU, nodes);
    }

    private void assertNodeOf(String flowName, FileSuffix fs, Map<String, JsonNode> nodes) {
        JsonNode jsonNode = nodes.get(fs.prepend(flowName));
        assertNotNull(jsonNode, "There should be a node for " + fs + " values ");
        assertEquals(fs.suffix(), jsonNode.asText());
    }

    private Flow flow(String name, UUID uid, List<Form> forms) {
        Flow flow = new Flow();
        flow.setName(name);
        flow.setUid(uid);
        flow.setForms(forms);
        return flow;
    }

    private Form form(String name, UUID uid) {
        Form form = new Form();
        form.setUid(uid);
        form.setName(name);
        return form;
    }

    private static class MockFormExporter extends FormExporter {

        MockFormExporter() {
            super(new ObjectMapper());
        }

        @Override
        public Map<String, JsonNode> exportToJson(Form form, References refs) {
            refs.resourceBundleKeys.add(form.getName());
            return Map.of(FileSuffix.FORM.prepend(form.getName()),
                    JsonNodeFactory.instance.textNode(FileSuffix.FORM.suffix()));
        }
    }

    private static class MockExporter implements FlowSpecificExporter {

        private final Set<String> expectedResourceBundleKeys;
        private FileSuffix currentSuffix;

        MockExporter(Set<String> expectedResourceBundleKeys) {
            this.expectedResourceBundleKeys = expectedResourceBundleKeys;
        }

        @Override
        public Map<String, JsonNode> serializeEntitiesForFlow(Flow flow, References references) {
            assertEquals(expectedResourceBundleKeys, references.resourceBundleKeys);
            return Map.of(currentSuffix.prepend(flow.getName()),
                    JsonNodeFactory.instance.textNode(currentSuffix.suffix()));
        }

        @Override
        public boolean canExport(FileSuffix suffix) {
            if (suffix == FileSuffix.FORM || suffix == FileSuffix.FLOW) {
                return false;
            }
            currentSuffix = suffix;
            return true;
        }
    }
}