package com.jda.mobility.framework.extensions.service.impl.migration.exporter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.jda.mobility.framework.extensions.entity.CustomComponentMaster;
import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.entity.ProductConfig;
import com.jda.mobility.framework.extensions.repository.CustomComponentMasterRepository;
import com.jda.mobility.framework.extensions.repository.ProductConfigRepository;
import com.jda.mobility.framework.extensions.service.impl.migration.FileSuffix;
import com.jda.mobility.framework.extensions.service.impl.migration.References;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class CustomComponentExporterTest {
    @Mock
    private CustomComponentMasterRepository customComponentsRepo;

    @Mock
    private ProductConfigRepository productConfigsRepo;

    private UUID goodRoleUid = UUID.randomUUID();
    private UUID expectedProductUid = UUID.randomUUID();

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void init() {
        initMocks(this);
        when(productConfigsRepo.findByRoleMasterUid(any(UUID.class)))
                .thenAnswer(invocation -> {
                    UUID uuid = invocation.getArgument(0, UUID.class);
                    if (uuid.equals(goodRoleUid)) {
                        ProductConfig config = new ProductConfig();
                        config.setUid(expectedProductUid);
                        return List.of(config);
                    }
                    else {
                        return List.of();
                    }
                });
    }

    @Test
    void noComponents() {
        when(customComponentsRepo.findByProductConfigIdIn(List.of(expectedProductUid))).thenReturn(List.of());

        CustomComponentExporter exporter = new CustomComponentExporter(customComponentsRepo, productConfigsRepo, mapper);

        JsonNode jsonNode = exporter.serializeExportableEntities(goodRoleUid);

        assertTrue(jsonNode.isArray(), "serialized node expected to be array.");
        assertEquals(0, jsonNode.size(), "Serialized array node should have been empty");
    }

    @Test
    void multipleComponents() {
        CustomComponentMaster one = customComponent(UUID.randomUUID(), "one");
        CustomComponentMaster two = customComponent(UUID.randomUUID(), "two");

        when(customComponentsRepo.findByProductConfigIdIn(Set.of(expectedProductUid)))
                .thenReturn(Lists.newArrayList(one, two));

        CustomComponentExporter exporter = new CustomComponentExporter(customComponentsRepo, productConfigsRepo, mapper);

        JsonNode jsonNode = exporter.serializeExportableEntities(goodRoleUid);

        assertTrue(jsonNode.isArray(), "serialized node expected to be array.");
        assertEquals(2, jsonNode.size(), "Serialized array node should have two nodes");

        assertComponentNode(one.getUid().toString(), "one", jsonNode.path(0));
        assertComponentNode(two.getUid().toString(), "two", jsonNode.path(1));
    }

    @Test
    void noComponentsInRequestedLevel() {
        when(customComponentsRepo.findByProductConfigIdIn(any())).thenAnswer(invocation -> {
            Set uuids = invocation.getArgument(0, Set.class);
            if (!uuids.isEmpty()) {
                throw new IllegalStateException("Expected no uuids to be passed due to no product config being found");
            }
            return Collections.emptyList();
        });

        CustomComponentExporter exporter = new CustomComponentExporter(customComponentsRepo, productConfigsRepo, mapper);

        JsonNode jsonNode = exporter.serializeExportableEntities(UUID.randomUUID());

        assertTrue(jsonNode.isArray(), "serialized node expected to be array.");
        assertEquals(0, jsonNode.size(), "Serialized array node should have been empty");
    }

    @Test
    void forFlowNoCustomComponents() {
        UUID productId = UUID.randomUUID();
        CustomComponentMaster unusedCC = new CustomComponentMaster();
        unusedCC.setName("unused");
        unusedCC.setProductConfigId(productId);

        // We're not going to pass in any custom component references, so this method should not actually
        // be called. Even so, we'll add a value to return so if it does get called we'll detect that.
        // I prefer this to making verify calls.
        when(customComponentsRepo.findById(any(UUID.class))).thenReturn(Optional.of(unusedCC));

        CustomComponentExporter exporter = new CustomComponentExporter(customComponentsRepo, productConfigsRepo, mapper);
        Map<String, JsonNode> exports =
                exporter.serializeEntitiesForFlow(flow("no-ccs", productId), new References());

        JsonNode jsonNode = exports.get(FileSuffix.CUSTOM_COMPONENT.prepend("no-ccs"));

        assertNotNull(jsonNode, "There should be an exported JsonNode");
        assertTrue(jsonNode.isArray(), "serialized node expected to be array.");
        assertEquals(0, jsonNode.size(), "Serialized array node should have been empty");
    }

    @Test
    void forFlowWithCustomComponents() {
        UUID productId = UUID.randomUUID();
        CustomComponentMaster first = customComponent(UUID.randomUUID(), "first", productId);
        CustomComponentMaster second = customComponent(UUID.randomUUID(), "second", productId);
        Map<UUID, CustomComponentMaster> components = Map.of(first.getUid(), first, second.getUid(), second);

        when(customComponentsRepo.findById(any(UUID.class))).thenAnswer(
                invocation -> Optional.ofNullable(components.get(invocation.getArgument(0, UUID.class))));

        CustomComponentExporter exporter = new CustomComponentExporter(customComponentsRepo, productConfigsRepo, mapper);
        References references = new References();
        references.customComponentIds.addAll(List.of(second.getUid().toString(), first.getUid().toString()));

        Map<String, JsonNode> exports =
                exporter.serializeEntitiesForFlow(flow("some-ccs", productId), references);

        JsonNode jsonNode = exports.get(FileSuffix.CUSTOM_COMPONENT.prepend("some-ccs"));

        assertNotNull(jsonNode, "There should be an exported JsonNode");
        assertTrue(jsonNode.isArray(), "serialized node expected to be array.");
        assertEquals(2, jsonNode.size(), "Serialized array node should have two nodes");

        assertComponentNode(first.getUid().toString(), "first", jsonNode.path(0));
        assertComponentNode(second.getUid().toString(), "second", jsonNode.path(1));
    }

    @Test
    void forFlowWithCustomComponentsInAnotherLayer() {
        UUID productId = UUID.randomUUID();
        CustomComponentMaster anotherLayer = customComponent(UUID.randomUUID(), "another-layer", UUID.randomUUID());
        CustomComponentMaster thisLayer = customComponent(UUID.randomUUID(), "this-layer", productId);
        Map<UUID, CustomComponentMaster> components =
                Map.of(anotherLayer.getUid(), anotherLayer, thisLayer.getUid(), thisLayer);

        when(customComponentsRepo.findById(any(UUID.class))).thenAnswer(
                invocation -> Optional.ofNullable(components.get(invocation.getArgument(0, UUID.class))));

        CustomComponentExporter exporter = new CustomComponentExporter(customComponentsRepo, productConfigsRepo, mapper);
        References references = new References();
        references.customComponentIds.addAll(List.of(anotherLayer.getUid().toString(), thisLayer.getUid().toString()));

        Map<String, JsonNode> exports =
                exporter.serializeEntitiesForFlow(flow("multiple-layers", productId), references);

        JsonNode jsonNode = exports.get(FileSuffix.CUSTOM_COMPONENT.prepend("multiple-layers"));

        assertNotNull(jsonNode, "There should be an exported JsonNode");
        assertTrue(jsonNode.isArray(), "serialized node expected to be array.");
        assertEquals(1, jsonNode.size(), "Serialized array node should have one node");

        assertComponentNode(thisLayer.getUid().toString(), "this-layer", jsonNode.path(0));
    }

    private void assertComponentNode(String uid, String name, JsonNode node) {
        assertEquals(uid, node.path("uid").asText());
        assertEquals(name, node.path("name").asText());
    }

    private CustomComponentMaster customComponent(UUID uid, String name) {
        return customComponent(uid, name, null);
    }

    private CustomComponentMaster customComponent(UUID uid, String name, UUID productId) {
        CustomComponentMaster component = new CustomComponentMaster();
        component.setUid(uid);
        component.setName(name);
        component.setProductConfigId(productId);
        return component;
    }

    private Flow flow(String name, UUID productId) {
        Flow flow = new Flow();
        flow.setName(name);

        ProductConfig product = new ProductConfig();
        product.setUid(productId);

        flow.setProductConfig(product);

        return flow;
    }
}