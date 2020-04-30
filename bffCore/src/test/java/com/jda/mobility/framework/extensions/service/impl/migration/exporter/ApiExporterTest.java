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
import com.jda.mobility.framework.extensions.repository.ApiMasterRepository;
import com.jda.mobility.framework.extensions.service.impl.migration.ApiKey;
import com.jda.mobility.framework.extensions.service.impl.migration.FileSuffix;
import com.jda.mobility.framework.extensions.service.impl.migration.References;
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

class ApiExporterTest {

    @Mock
    private ApiMasterRepository repo;

    @BeforeEach
    void init() {
        initMocks(this);
    }

    @Test
    void noApis() {
        when(repo.findAll(any(Sort.class))).thenReturn(List.of());

        ApiExporter exporter = new ApiExporter(repo);
        JsonNode jsonNode = exporter.serializeExportableEntities(UUID.randomUUID());
        assertTrue(jsonNode.isArray(), "serialized node expected to be array.");
        assertEquals(0, jsonNode.size(), "Serialized array node should have been empty");
    }

    @Test
    void singleApi() {
        UUID levelId = UUID.randomUUID();
        UUID registryId = UUID.randomUUID();
        ApiRegistry registry = registry("reg", registryId, levelId);

        UUID apiId = UUID.randomUUID();
        ApiMaster api = api("api", apiId, registry);

        when(repo.findAll(any(Sort.class))).thenReturn(List.of(api));

        ApiExporter exporter = new ApiExporter(repo);

        JsonNode jsonNode = exporter.serializeExportableEntities(levelId);

        assertTrue(jsonNode.isArray(), "serialized node expected to be array");
        assertEquals(1, jsonNode.size(), "Serialized array node should contain single api");

        assertApiNode(levelId, "api", "reg", jsonNode.path(0));
    }

    @Test
    void twoApisSameRegistry() {
        UUID levelId = UUID.randomUUID();
        ApiRegistry registry = registry("reg", UUID.randomUUID(), levelId);

        ApiMaster api1 = api("api1", UUID.randomUUID(), registry);
        ApiMaster api2 = api("api2", UUID.randomUUID(), registry);

        when(repo.findAll(any(Sort.class))).thenReturn(List.of(api1, api2));

        ApiExporter exporter = new ApiExporter(repo);

        JsonNode jsonNode = exporter.serializeExportableEntities(levelId);

        assertTrue(jsonNode.isArray(), "serialized node expected to be array");
        assertEquals(2, jsonNode.size(), "Serialized array node should contain two apis");

        assertApiNode(levelId, "api1", "reg", jsonNode.path(0));
        assertApiNode(levelId, "api2", "reg", jsonNode.path(1));
    }

    @Test
    void twoApisDifferentRegistries() {
        UUID levelId = UUID.randomUUID();
        ApiRegistry registry1 = registry("reg1", UUID.randomUUID(), levelId);

        ApiMaster api1 = api("api1", UUID.randomUUID(), registry1);

        ApiRegistry registry2 = registry("reg2", UUID.randomUUID(), levelId);
        ApiMaster api2 = api("api2", UUID.randomUUID(), registry2);

        when(repo.findAll(any(Sort.class))).thenReturn(List.of(api1, api2));

        ApiExporter exporter = new ApiExporter(repo);

        JsonNode jsonNode = exporter.serializeExportableEntities(levelId);

        assertTrue(jsonNode.isArray(), "serialized node expected to be array");
        assertEquals(2, jsonNode.size(), "Serialized array node should contain two apis");

        assertApiNode(levelId, "api1", "reg1", jsonNode.path(0));
        assertApiNode(levelId, "api2", "reg2", jsonNode.path(1));
    }

    @Test
    void apisInWrongLevel() {
        UUID levelId = UUID.randomUUID();
        ApiRegistry registry1 = registry("reg1", UUID.randomUUID(), UUID.randomUUID());

        ApiMaster api1 = api("api1", UUID.randomUUID(), registry1);

        ApiRegistry registry2 = registry("reg2", UUID.randomUUID(), UUID.randomUUID());
        ApiMaster api2 = api("api2", UUID.randomUUID(), registry2);

        when(repo.findAll(any(Sort.class))).thenReturn(List.of(api1, api2));

        ApiExporter exporter = new ApiExporter(repo);

        JsonNode jsonNode = exporter.serializeExportableEntities(levelId);

        assertTrue(jsonNode.isArray(), "serialized node expected to be array");
        assertEquals(0, jsonNode.size(), "Serialized array node should no apis");
    }

    @Test
    void oneApiInCorrectLevelOneInWrongLevel() {
        UUID levelId = UUID.randomUUID();
        ApiRegistry registry1 = registry("reg1", UUID.randomUUID(), UUID.randomUUID());

        ApiMaster api1 = api("api1", UUID.randomUUID(), registry1);

        ApiRegistry registry2 = registry("reg2", UUID.randomUUID(), levelId);
        ApiMaster api2 = api("api2", UUID.randomUUID(), registry2);

        when(repo.findAll(any(Sort.class))).thenReturn(List.of(api1, api2));

        ApiExporter exporter = new ApiExporter(repo);

        JsonNode jsonNode = exporter.serializeExportableEntities(levelId);

        assertTrue(jsonNode.isArray(), "serialized node expected to be array");
        assertEquals(1, jsonNode.size(), "Serialized array node should contain single api");

        assertApiNode(levelId, "api2", "reg2", jsonNode.path(0));
    }

    @Test
    void noApisForFlow() {
        // We're not going to pass in any api keys, so this method should not actually
        // be called. Even so, we'll add a value to return so if it does get called we'll
        // detect that. I prefer this to making verify calls.
        when(repo.findByRequestEndpointAndRequestMethodAndApiRegistryNameAndApiRegistryApiType(
                        anyString(), anyString(), anyString(), anyString()
                )).thenReturn(Optional.of(new ApiMaster()));
        ApiExporter exporter = new ApiExporter(repo);

        Map<String, JsonNode> exports =
                exporter.serializeEntitiesForFlow(flow("none", UUID.randomUUID()), new References());

        JsonNode jsonNode = exports.get(FileSuffix.API.prepend("none"));

        assertNotNull(jsonNode, "There should be an exported JsonNode");
        assertTrue(jsonNode.isArray(), "serialized node expected to be array.");
        assertEquals(0, jsonNode.size(), "Serialized array node should have been empty");
    }

    @Test
    void multipleApisForFlow() {
        UUID level = UUID.randomUUID();
        ApiRegistry goodRegistry = registry("good", UUID.randomUUID(), level);
        ApiRegistry badRegistry = registry("bad", UUID.randomUUID(), UUID.randomUUID());

        Map<ApiKey, Optional<ApiMaster>> apis = Map.of(
                new ApiKey("good", "/1", "GET", "INTERNAL"), Optional.of(api("one", UUID.randomUUID(), goodRegistry)),
                new ApiKey("bad", "/2", "GET", "INTERNAL"), Optional.of(api("two", UUID.randomUUID(), badRegistry)),
                new ApiKey("good", "/3", "GET", "INTERNAL"), Optional.of(api("aaa", UUID.randomUUID(), goodRegistry)),
                new ApiKey("", "", "", ""), Optional.empty(),
                new ApiKey("good", "/4", "GET", "INTERNAL"), Optional.of(api("bbb", UUID.randomUUID(), goodRegistry))
        );

        when(repo.findByRequestEndpointAndRequestMethodAndApiRegistryNameAndApiRegistryApiType(
                    anyString(), anyString(), anyString(), anyString()
            )).thenAnswer(invocation -> {
                ApiKey key = new ApiKey(invocation.getArgument(2, String.class),
                        invocation.getArgument(0, String.class),
                        invocation.getArgument(1, String.class),
                        invocation.getArgument(3, String.class)
                );

                return apis.get(key);
            });

        References refs = new References();
        refs.apis.addAll(apis.keySet());

        ApiExporter exporter = new ApiExporter(repo);

        Map<String, JsonNode> exports =
                exporter.serializeEntitiesForFlow(flow("multiple", level), refs);

        JsonNode jsonNode = exports.get(FileSuffix.API.prepend("multiple"));

        assertNotNull(jsonNode, "There should be an exported JsonNode");
        assertTrue(jsonNode.isArray(), "serialized node expected to be array.");
        assertEquals(3, jsonNode.size(), "Serialized array node should have been empty");

        assertApiNode(level, "aaa", "good", jsonNode.path(0));
        assertApiNode(level, "bbb", "good", jsonNode.path(1));
        assertApiNode(level, "one", "good", jsonNode.path(2));
    }

    private void assertApiNode(UUID levelId, String expectedName, String expectedRegistry, JsonNode apiNode) {
        assertFalse(apiNode.has("uid"), "uids should not be serialized");
        assertEquals(expectedName, apiNode.path("name").asText());
        assertEquals("response", apiNode.path("responseSchema").asText());
        assertEquals("pathparams", apiNode.path("requestPathparams").asText());
        assertEquals("query", apiNode.path("requestQuery").asText());
        assertEquals("method", apiNode.path("requestMethod").asText());
        assertEquals("/endpoint", apiNode.path("requestEndpoint").asText());
        assertEquals("body", apiNode.path("requestBody").asText());

        JsonNode owner = apiNode.path("apiRegistry");
        assertFalse(owner.has("uid"), "uids should not be serialized");
        assertEquals("INTERNAL", owner.path("apiType").asText());
        assertEquals(expectedRegistry, owner.path("name").asText());

        JsonNode roleMaster = owner.path("roleMaster");
        String roleUid = roleMaster.path("uid").isMissingNode() ? roleMaster.asText() : roleMaster.path("uid").asText();
        assertEquals(levelId.toString(), roleUid);
    }

    private ApiRegistry registry(String name, UUID registryId, UUID roleId) {
        RoleMaster role = new RoleMaster();
        role.setUid(roleId);

        ApiRegistry registry = new ApiRegistry();
        registry.setName(name);
        registry.setUid(registryId);
        registry.setApiType("INTERNAL");
        registry.setApiVersion("test");
        registry.setBasePath("/test");
        registry.setContextPath("/another-test");
        registry.setVersionId(1);
        registry.setRoleMaster(role);

        return registry;
    }

    private ApiMaster api(String name, UUID id, ApiRegistry registry) {
        ApiMaster api = new ApiMaster();
        api.setApiRegistry(registry);
        api.setName(name);
        api.setUid(id);
        api.setResponseSchema("response");
        api.setRequestPathparams("pathparams");
        api.setRequestQuery("query");
        api.setRequestMethod("method");
        api.setRequestEndpoint("/endpoint");
        api.setRequestBody("body");
        return api;
    }

    private Flow flow(String name, UUID level) {
        Flow flow = new Flow();
        flow.setName(name);

        ProductConfig pc = new ProductConfig();
        RoleMaster rm = new RoleMaster();
        rm.setUid(level);
        pc.setRoleMaster(rm);
        flow.setProductConfig(pc);

        return flow;
    }

}