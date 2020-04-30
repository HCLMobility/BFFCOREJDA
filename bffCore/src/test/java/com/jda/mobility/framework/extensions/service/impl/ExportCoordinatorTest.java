package com.jda.mobility.framework.extensions.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.jda.mobility.framework.extensions.entity.RoleMaster;
import com.jda.mobility.framework.extensions.entity.UserRole;
import com.jda.mobility.framework.extensions.repository.UserRoleRepository;
import com.jda.mobility.framework.extensions.service.impl.migration.FileSuffix;
import com.jda.mobility.framework.extensions.service.impl.migration.exporter.ExportCoordinator;
import com.jda.mobility.framework.extensions.service.impl.migration.exporter.Exporter;
import com.jda.mobility.framework.extensions.service.impl.migration.exporter.FlowExporter;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ExportType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class ExportCoordinatorTest {

    @Mock
    private UserRoleRepository roles;

    private UUID expectedLevel = UUID.randomUUID();

    @BeforeEach
    void init() {
        initMocks(this);
        when(roles.findByUserId(anyString())).thenAnswer(invocation -> {
            String userId = invocation.getArgument(0, String.class);

            if (SUPER.equals(userId)) {
                UserRole userRole = new UserRole();
                RoleMaster rm = new RoleMaster();
                rm.setUid(expectedLevel);
                userRole.setRoleMaster(rm);
                return Optional.of(userRole);
            }

            return Optional.empty();
        });
    }

    @Test
    void flowWithNoFlowId() {
        ExportCoordinator coordinator =
                new ExportCoordinator(exporters(), new MockFlowExporter(), roles);

        assertThrows(IllegalArgumentException.class,
                () -> coordinator.exportData(null, new ExportType[] { ExportType.FLOW }, SUPER));
    }

    @Test
    void userUnauthorized() {
        ExportCoordinator coordinator =
                new ExportCoordinator(exporters(), new MockFlowExporter(), roles);

        assertThrows(IllegalStateException.class,
                () -> coordinator.exportData(null, new ExportType[] { ExportType.ALL }, "hacker!"));
    }

    @Test
    void justFlow() {
        ExportCoordinator coordinator =
                new ExportCoordinator(exporters(), new MockFlowExporter(), roles);

        UUID flowId = UUID.randomUUID();
        Map<String, JsonNode> nodes =
                coordinator.exportData(flowId, new ExportType[] { ExportType.FLOW }, SUPER);

        assertEquals(1, nodes.size(), "There should only be a single flow node exported");
        JsonNode jsonNode = nodes.get(FileSuffix.FLOW.prepend(MOCK_FLOW));
        assertNotNull(jsonNode, "Expected flow node to be present in map");
        assertEquals(flowId.toString(), jsonNode.asText());
    }

    @Test
    void exportAll() {
        ExportCoordinator coordinator =
                new ExportCoordinator(exporters(), new MockFlowExporter(), roles);

        Map<String, JsonNode> nodes =
                coordinator.exportData(null, new ExportType[] { ExportType.ALL }, SUPER);

        List<FileSuffix> expectedSuffixes = List.of(
                FileSuffix.CUSTOM_COMPONENT,
                FileSuffix.RESOURCE_BUNDLE,
                FileSuffix.API,
                FileSuffix.API_REGISTRY,
                FileSuffix.MENU,
                FileSuffix.APP_CONFIG
        );

        assertNonFlowNodes(nodes, expectedSuffixes);
    }

    @Test
    void exportRegistries() {
        ExportCoordinator coordinator =
                new ExportCoordinator(exporters(), new MockFlowExporter(), roles);

        Map<String, JsonNode> nodes =
                coordinator.exportData(null, new ExportType[] { ExportType.REGISTRY }, SUPER);

        List<FileSuffix> expectedSuffixes = List.of(
                FileSuffix.API,
                FileSuffix.API_REGISTRY
        );

        assertNonFlowNodes(nodes, expectedSuffixes);
    }

    @Test
    void exportAppConfigs() {
        ExportCoordinator coordinator =
                new ExportCoordinator(exporters(), new MockFlowExporter(), roles);

        Map<String, JsonNode> nodes =
                coordinator.exportData(null, new ExportType[] { ExportType.APP_CONFIG }, SUPER);

        List<FileSuffix> expectedSuffixes = List.of(FileSuffix.APP_CONFIG);

        assertNonFlowNodes(nodes, expectedSuffixes);
    }

    @Test
    void exportMenus() {
        ExportCoordinator coordinator =
                new ExportCoordinator(exporters(), new MockFlowExporter(), roles);

        Map<String, JsonNode> nodes =
                coordinator.exportData(null, new ExportType[] { ExportType.MENU }, SUPER);

        List<FileSuffix> expectedSuffixes = List.of(FileSuffix.MENU);

        assertNonFlowNodes(nodes, expectedSuffixes);
    }

    @Test
    void exportCustomComponents() {
        ExportCoordinator coordinator =
                new ExportCoordinator(exporters(), new MockFlowExporter(), roles);

        Map<String, JsonNode> nodes =
                coordinator.exportData(null, new ExportType[] { ExportType.CUSTOM_CONTROL }, SUPER);

        List<FileSuffix> expectedSuffixes = List.of(FileSuffix.CUSTOM_COMPONENT);

        assertNonFlowNodes(nodes, expectedSuffixes);
    }

    @Test
    void exportResourceBundles() {
        ExportCoordinator coordinator =
                new ExportCoordinator(exporters(), new MockFlowExporter(), roles);

        Map<String, JsonNode> nodes =
                coordinator.exportData(null, new ExportType[] { ExportType.RESOURCE_BUNDLE }, SUPER);

        List<FileSuffix> expectedSuffixes = List.of(FileSuffix.RESOURCE_BUNDLE);

        assertNonFlowNodes(nodes, expectedSuffixes);
    }

    @Test
    void exportCombo() {
        ExportCoordinator coordinator =
                new ExportCoordinator(exporters(), new MockFlowExporter(), roles);

        Map<String, JsonNode> nodes =
                coordinator.exportData(null,
                        new ExportType[] { ExportType.RESOURCE_BUNDLE, ExportType.APP_CONFIG, ExportType.MENU },
                        SUPER);

        List<FileSuffix> expectedSuffixes = List.of(
                FileSuffix.RESOURCE_BUNDLE,
                FileSuffix.APP_CONFIG,
                FileSuffix.MENU

        );

        assertNonFlowNodes(nodes, expectedSuffixes);
    }

    @Test
    void exportComboWithFlow() {
        ExportCoordinator coordinator =
                new ExportCoordinator(exporters(), new MockFlowExporter(), roles);

        // flow cannot be included with any other export type
        assertThrows(IllegalArgumentException.class, () ->
                coordinator.exportData(null,
                        new ExportType[] { ExportType.RESOURCE_BUNDLE, ExportType.FLOW },
                        SUPER));
    }

    private void assertNonFlowNodes(Map<String, JsonNode> nodes, List<FileSuffix> expectedSuffixes) {
        assertEquals(expectedSuffixes.size(), nodes.size(), "Unexpected number of export nodes");
        expectedSuffixes.forEach(suffix -> {
            String expectedVal = suffix.prepend("All");
            assertEquals(expectedLevel.toString(), nodes.get(expectedVal).asText());
        });
    }

    private Set<Exporter> exporters() {
        Set<Exporter> exporters = new HashSet<>();
        for (FileSuffix value : FileSuffix.values()) {
            if (value == FileSuffix.FORM || value == FileSuffix.FLOW) {
                continue;
            }
            exporters.add(new MockExporter(value));
        }
        return exporters;
    }

    private static final String SUPER = "super";
    private static final String MOCK_FLOW = "mock-flow";

    private static class MockFlowExporter extends FlowExporter {

        MockFlowExporter() {
            super(null, null, null);
        }

        @Override
        public Map<String, JsonNode> exportToJson(UUID flowId) {
            return Map.of(FileSuffix.FLOW.prepend("mock-flow"),
                    JsonNodeFactory.instance.textNode(flowId.toString()));
        }
    }

    private static class MockExporter implements Exporter {

        private final FileSuffix suffix;

        MockExporter(FileSuffix suffix) {
            this.suffix = suffix;
        }

        @Override
        public FileSuffix fileSuffix() {
            return suffix;
        }

        @Override
        public JsonNode serializeExportableEntities(UUID level) {
            return JsonNodeFactory.instance.textNode(level.toString());
        }
    }

}
