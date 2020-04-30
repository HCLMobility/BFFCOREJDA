package com.jda.mobility.framework.extensions.service.impl.migration.exporter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.jda.mobility.framework.extensions.entity.ApiMaster;
import com.jda.mobility.framework.extensions.entity.ApiRegistry;
import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.entity.ProductConfig;
import com.jda.mobility.framework.extensions.entity.RoleMaster;
import com.jda.mobility.framework.extensions.repository.ApiRegistryRepository;
import com.jda.mobility.framework.extensions.service.impl.migration.ApiKey;
import com.jda.mobility.framework.extensions.service.impl.migration.FileSuffix;
import com.jda.mobility.framework.extensions.service.impl.migration.References;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class RegistryExporterTest {

    @Mock
    private ApiRegistryRepository repo;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void noRegistries() {
        when(repo.findByRoleMasterUidOrderByName(any(UUID.class))).thenReturn(List.of());

        RegistryExporter exporter = new RegistryExporter(repo);
        JsonNode jsonNode = exporter.serializeExportableEntities(UUID.randomUUID());

        assertTrue(jsonNode.isArray(), "serialized node expected to be array.");
        assertEquals(0, jsonNode.size(), "Serialized array node should have been empty");
    }

    @Test
    void oneRegistry() {
        UUID roleId = UUID.randomUUID();
        ApiRegistry registry = registry("foo", "INTERNAL", roleId);
        when(repo.findByRoleMasterUidOrderByName(roleId)).thenReturn(List.of(registry));

        RegistryExporter exporter = new RegistryExporter(repo);
        JsonNode jsonNode = exporter.serializeExportableEntities(roleId);

        assertTrue(jsonNode.isArray(), "serialized node expected to be array");
        assertEquals(1, jsonNode.size(), "Serialized array node should contain single registry");

        assertRegistryNode(roleId, "foo", "INTERNAL", jsonNode.path(0));
    }

    @Test
    void multipleRegistries() {
        UUID roleId = UUID.randomUUID();
        ApiRegistry registry1 = registry("one", "INTERNAL", roleId);
        ApiRegistry registry2 = registry("two", "THIRD_PARTY", roleId);
        ApiRegistry registry3 = registry("three", "ORCHESTRATION", roleId);
        when(repo.findByRoleMasterUidOrderByName(roleId))
                .thenReturn(List.of(registry1, registry2, registry3));

        RegistryExporter exporter = new RegistryExporter(repo);
        JsonNode jsonNode = exporter.serializeExportableEntities(roleId);

        assertTrue(jsonNode.isArray(), "serialized node expected to be array");
        assertEquals(3, jsonNode.size(), "Serialized array node should contain single registry");

        assertRegistryNode(roleId, "one", "INTERNAL", jsonNode.path(0));
        assertRegistryNode(roleId, "two", "THIRD_PARTY", jsonNode.path(1));
        assertRegistryNode(roleId, "three", "ORCHESTRATION", jsonNode.path(2));
    }

    @Test
    void noRegistriesForFlow() {
        // We're not going to pass in any api keys, so this method should not actually
        // be called. Even so, we'll add a value to return so if it does get called we'll
        // detect that. I prefer this to making verify calls.
        when(repo.findByApiTypeAndNameAndRoleMaster_name(anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(new ApiRegistry()));
        RegistryExporter exporter = new RegistryExporter(repo);
        Map<String, JsonNode> exports =
                exporter.serializeEntitiesForFlow(flow("none", UUID.randomUUID()), new References());

        JsonNode jsonNode = exports.get(FileSuffix.API_REGISTRY.prepend("none"));

        assertNotNull(jsonNode, "There should be an exported JsonNode");
        assertTrue(jsonNode.isArray(), "serialized node expected to be array.");
        assertEquals(0, jsonNode.size(), "Serialized array node should have been empty");
    }

    @Test
    void noMultipleRegistriesForFlow() {

        References refs = new References();
        refs.apis.addAll(List.of(
                new ApiKey("one", "/one", "GET", "INTERNAL"),
                new ApiKey("one", "/two", "GET", "INTERNAL"),
                new ApiKey("two", "/one/2", "GET", "INTERNAL"),
                new ApiKey("two", "/two/2", "GET", "INTERNAL")
        ));

        UUID level = UUID.randomUUID();

        ApiRegistry one = registry("one", "INTERNAL", level);
        ApiRegistry two = registry("two", "INTERNAL", level);

        when(repo.findByApiTypeAndNameAndRoleMaster_name(anyString(), anyString(), eq("test-level")))
                .thenAnswer(invocation -> {
                    String type = invocation.getArgument(0, String.class);
                    String name = invocation.getArgument(1, String.class);

                    if (type.equals("INTERNAL") && name.equals("one")) {
                        return Optional.of(one);
                    }
                    else if (type.equals("INTERNAL") && name.equals("two")) {
                        return Optional.of(two);
                    }
                    return Optional.empty();
                });

        RegistryExporter exporter = new RegistryExporter(repo);
        Map<String, JsonNode> exports =
                exporter.serializeEntitiesForFlow(flow("multiple", level), refs);

        JsonNode jsonNode = exports.get(FileSuffix.API_REGISTRY.prepend("multiple"));

        assertNotNull(jsonNode, "There should be an exported JsonNode");
        assertTrue(jsonNode.isArray(), "serialized node expected to be array.");
        assertEquals(2, jsonNode.size(), "Serialized array node should have two nodes");

        assertRegistryNode(level, "one", "INTERNAL", jsonNode.path(0));
        assertRegistryNode(level, "two", "INTERNAL", jsonNode.path(1));
    }

    private void assertRegistryNode(UUID levelId, String expectedName, String expectedType, JsonNode registryNode) {
        assertFalse(registryNode.has("uid"), "uids should not be serialized");
        assertEquals(expectedName, registryNode.path("name").asText());
        assertEquals(expectedType, registryNode.path("apiType").asText());
        assertEquals("/base", registryNode.path("basePath").asText());
        assertEquals("/context", registryNode.path("contextPath").asText());
        assertEquals("alpha1", registryNode.path("apiVersion").asText());
        assertEquals("80", registryNode.path("port").asText());

        JsonNode roleMaster = registryNode.path("roleMaster");
        String roleUid = roleMaster.path("uid").isMissingNode() ? roleMaster.asText() : roleMaster.path("uid").asText();
        assertEquals(levelId.toString(), roleUid);

        assertFalse(registryNode.has("apiMasters"), "registry should not have apis inlined into it");
    }

    private ApiRegistry registry(String name, String apiType, UUID level) {
        RoleMaster role = new RoleMaster();
        role.setUid(level);

        ApiRegistry registry = new ApiRegistry();
        registry.setUid(UUID.randomUUID());
        registry.setName(name);
        registry.setApiType(apiType);
        registry.setRoleMaster(role);
        registry.setContextPath("/context");
        registry.setBasePath("/base");
        registry.setApiVersion("alpha1");
        registry.setPort("80");
        // These shouldn't be shown in the output, but add one
        // to ensure that's the case.
        registry.setApiMasters(List.of(new ApiMaster()));

        return registry;
    }

    private Flow flow(String name, UUID level) {
        Flow flow = new Flow();
        flow.setName(name);

        ProductConfig pc = new ProductConfig();
        RoleMaster rm = new RoleMaster();
        rm.setName("test-level");
        rm.setUid(level);

        pc.setRoleMaster(rm);
        flow.setProductConfig(pc);

        return flow;
    }
}