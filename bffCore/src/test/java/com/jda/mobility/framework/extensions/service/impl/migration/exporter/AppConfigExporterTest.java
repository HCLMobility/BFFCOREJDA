package com.jda.mobility.framework.extensions.service.impl.migration.exporter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.jda.mobility.framework.extensions.entity.AppConfigDetail;
import com.jda.mobility.framework.extensions.entity.AppConfigMaster;
import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.repository.AppConfigMasterRepository;
import com.jda.mobility.framework.extensions.service.impl.migration.FileSuffix;
import com.jda.mobility.framework.extensions.service.impl.migration.References;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class AppConfigExporterTest {

    @Mock
    private AppConfigMasterRepository repo;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void noConfigs() {
        when(repo.findAll(any(Sort.class))).thenReturn(List.of());

        AppConfigExporter exporter = new AppConfigExporter(repo);
        JsonNode jsonNode = exporter.serializeExportableEntities(null);

        assertTrue(jsonNode.isArray(), "serialized node expected to be array.");
        assertEquals(0, jsonNode.size(), "Serialized array node should have been empty");
    }

    @Test
    void singleConfigNoDetails() {
        AppConfigMaster config = config("test", APPLICATION);

        when(repo.findAll(any(Sort.class))).thenReturn(List.of(config));

        AppConfigExporter exporter = new AppConfigExporter(repo);
        JsonNode jsonNode = exporter.serializeExportableEntities(null);

        assertTrue(jsonNode.isArray(), "serialized node expected to be array");
        assertEquals(1, jsonNode.size(), "Serialized array node should contain one config");

        assertConfigNode("test", APPLICATION, List.of(), jsonNode.path(0));
    }

    @Test
    void singleConfigWithDetails() {
        AppConfigMaster config = config("Stuff", APPLICATION);
        detail("test", "100px", config);

        when(repo.findAll(any(Sort.class))).thenReturn(List.of(config));

        AppConfigExporter exporter = new AppConfigExporter(repo);
        JsonNode jsonNode = exporter.serializeExportableEntities(null);

        assertTrue(jsonNode.isArray(), "serialized node expected to be array");
        assertEquals(1, jsonNode.size(), "Serialized array node should contain one config");

        assertConfigNode("Stuff", "APPLICATION", List.of(Pair.of("test", "100px")), jsonNode.path(0));
    }

    @Test
    void multipleConfigsWithDetails() {
        AppConfigMaster a = config("A", CONTEXT);
        detail("flow", "hi", a).setFlowId(UUID.randomUUID());
        detail("something", "hey", a);
        AppConfigMaster b = config("B", GLOBAL);
        detail("user", "bye", b).setUserId("super");
        detail("device", "yup", b).setDeviceName("RDT001");
        AppConfigMaster c = config("C", APPLICATION);
        detail("test", "100px", c).setUserId("super");

        when(repo.findAll(any(Sort.class))).thenReturn(List.of(a, b, c));

        AppConfigExporter exporter = new AppConfigExporter(repo);
        JsonNode jsonNode = exporter.serializeExportableEntities(null);

        assertTrue(jsonNode.isArray(), "serialized node expected to be array");
        assertEquals(3, jsonNode.size(), "Serialized array node should contain three configs");

        assertConfigNode("A", CONTEXT, List.of(), jsonNode.path(0));
        assertConfigNode("B", GLOBAL, List.of(), jsonNode.path(1));
        assertConfigNode("C", APPLICATION, List.of(Pair.of("test", "100px")), jsonNode.path(2));
    }

    @Test
    void noConfigsForFlow() {

        when(repo.findByConfigNameInAndConfigType(any(), anyString()))
                .thenReturn(Collections.emptyList());

        AppConfigExporter exporter = new AppConfigExporter(repo);

        Map<String, JsonNode> exports =
                exporter.serializeEntitiesForFlow(flow("none"), new References());

        JsonNode globalsNode = exports.get(FileSuffix.GLOBAL_VAR.prepend("none"));

        assertNotNull(globalsNode, "There should be an exported globals node");
        assertTrue(globalsNode.isArray(), "serialized globals node expected to be array.");
        assertEquals(0, globalsNode.size(), "Serialized globals array node should have been empty");

        JsonNode contextNode = exports.get(FileSuffix.CONTEXT_VAL.prepend("none"));

        assertNotNull(contextNode, "There should be an exported context node");
        assertTrue(contextNode.isArray(), "serialized context node expected to be array.");
        assertEquals(0, contextNode.size(), "Serialized contextarray node should have been empty");
    }

    @Test
    void multipleConfigsForFlow() {

        when(repo.findByConfigNameInAndConfigType(Set.of("g1", "g2"), GLOBAL))
                .thenReturn(Lists.newArrayList(config("g2", GLOBAL), config("g1", GLOBAL)));
        when(repo.findByConfigNameInAndConfigType(Set.of("c1", "c2"), CONTEXT))
                .thenReturn(Lists.newArrayList(config("c2", CONTEXT), config("c1", CONTEXT)));

        References refs = new References();
        refs.globalVars.addAll(Set.of("g1", "g2"));
        refs.contextVals.addAll(Set.of("c1", "c2"));

        AppConfigExporter exporter = new AppConfigExporter(repo);

        Map<String, JsonNode> exports =
                exporter.serializeEntitiesForFlow(flow("multiple"), refs);

        JsonNode globalsNode = exports.get(FileSuffix.GLOBAL_VAR.prepend("multiple"));

        assertNotNull(globalsNode, "There should be an exported globals node");
        assertTrue(globalsNode.isArray(), "serialized globals node expected to be array.");
        assertEquals(2, globalsNode.size(), "Serialized globals array node should have been empty");

        assertConfigNode("g1", GLOBAL, List.of(), globalsNode.path(0));
        assertConfigNode("g2", GLOBAL, List.of(), globalsNode.path(1));

        JsonNode contextNode = exports.get(FileSuffix.CONTEXT_VAL.prepend("multiple"));

        assertNotNull(contextNode, "There should be an exported context node");
        assertTrue(contextNode.isArray(), "serialized context node expected to be array.");
        assertEquals(2, contextNode.size(), "Serialized contextarray node should have been empty");

        assertConfigNode("c1", CONTEXT, List.of(), contextNode.path(0));
        assertConfigNode("c2", CONTEXT, List.of(), contextNode.path(1));
    }

    private void assertConfigNode(String configName,
                                  String configType,
                                  List<Pair<String, String>> expectedDetails,
                                  JsonNode node) {
        assertEquals(configName, node.path("configName").asText());
        assertEquals(configType, node.path("configType").asText());

        if (expectedDetails.isEmpty()) {
            assertFalse(node.has("appConfigDetails"), "node should not have details");
        }

        JsonNode detailsNode = node.path("appConfigDetails");

        for (int i = 0; i < expectedDetails.size(); ++i) {
            Pair<String, String> expectedDetail = expectedDetails.get(0);
            JsonNode detailNode = detailsNode.path(i);

            assertEquals(expectedDetail.getLeft(), detailNode.path("description").asText());
            assertEquals(expectedDetail.getRight(), detailNode.path("configValue").asText());
        }
    }

    private AppConfigMaster config(String configName, String configType) {
        AppConfigMaster config = new AppConfigMaster();
        config.setUid(UUID.randomUUID());
        config.setConfigName(configName);
        config.setConfigType(configType);
        config.setAppConfigDetails(new ArrayList<>());

        return config;
    }

    private AppConfigDetail detail(String description, String configValue, AppConfigMaster owner) {
        AppConfigDetail detail = new AppConfigDetail();
        detail.setUid(UUID.randomUUID());
        detail.setDescription(description);
        detail.setConfigValue(configValue);

        owner.getAppConfigDetails().add(detail);
        detail.setAppConfigMaster(owner);

        return detail;
    }

    private Flow flow(String name) {
        Flow flow = new Flow();
        flow.setName(name);
        return flow;
    }

    private static final String GLOBAL = "GLOBAL";
    private static final String CONTEXT = "CONTEXT";
    private static final String APPLICATION = "APPLICATION";
}