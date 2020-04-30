package com.jda.mobility.framework.extensions.service.impl.migration.exporter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.entity.ResourceBundle;
import com.jda.mobility.framework.extensions.repository.ResourceBundleRepository;
import com.jda.mobility.framework.extensions.service.impl.migration.FileSuffix;
import com.jda.mobility.framework.extensions.service.impl.migration.References;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ResourceBundleType.ADMIN_UI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class ResourceBundleExporterTest {

    @Mock
    private ResourceBundleRepository repo;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void noResourceBundles() {
        when(repo.findByTypeOrderByLocaleAscRbkeyAscRbvalueAsc(ADMIN_UI.getType()))
                .thenReturn(List.of());

        ResourceBundleExporter exporter = new ResourceBundleExporter(repo);
        JsonNode jsonNode = exporter.serializeExportableEntities(null);

        assertTrue(jsonNode.isArray(), "serialized node expected to be array.");
        assertEquals(0, jsonNode.size(), "Serialized array node should have been empty");
    }

    @Test
    void oneResourceBundle() {
        ResourceBundle bundle = resourceBundle("hi", "Hi!", "en-US");
        when(repo.findByTypeOrderByLocaleAscRbkeyAscRbvalueAsc(ADMIN_UI.getType()))
                .thenReturn(List.of(bundle));

        ResourceBundleExporter exporter = new ResourceBundleExporter(repo);
        JsonNode jsonNode = exporter.serializeExportableEntities(null);

        assertTrue(jsonNode.isArray(), "serialized node expected to be array");
        assertEquals(1, jsonNode.size(), "Serialized array node should contain single bundle");

        assertResourceBundle("hi", "Hi!", "en-US", jsonNode.path(0));
    }

    @Test
    void multipleResourceBundles() {
        ResourceBundle bundle1 = resourceBundle("bye", "Bye Bye", "en-US");
        ResourceBundle bundle2 = resourceBundle("bye", "Ciao", "it-IT");
        ResourceBundle bundle3 = resourceBundle("bye", "Bonjour", "fr-FR");

        when(repo.findByTypeOrderByLocaleAscRbkeyAscRbvalueAsc(ADMIN_UI.getType()))
                .thenReturn(List.of(bundle1, bundle2, bundle3));

        ResourceBundleExporter exporter = new ResourceBundleExporter(repo);
        JsonNode jsonNode = exporter.serializeExportableEntities(null);

        assertTrue(jsonNode.isArray(), "serialized node expected to be array");
        assertEquals(3, jsonNode.size(), "Serialized array node should contain single bundle");

        assertResourceBundle("bye", "Bye Bye", "en-US", jsonNode.path(0));
        assertResourceBundle("bye", "Ciao", "it-IT", jsonNode.path(1));
        assertResourceBundle("bye", "Bonjour", "fr-FR", jsonNode.path(2));
    }

    @Test
    void noBundlesInFlow() {

        when(repo.findByRbkeyIn(any())).thenReturn(Collections.emptyList());

        ResourceBundleExporter exporter = new ResourceBundleExporter(repo);

        Map<String, JsonNode> exports = exporter.serializeEntitiesForFlow(flow("none"), new References());

        JsonNode jsonNode = exports.get(FileSuffix.RESOURCE_BUNDLE.prepend("none"));

        assertNotNull(jsonNode, "There should be an exported JsonNode");
        assertTrue(jsonNode.isArray(), "serialized node expected to be array.");
        assertEquals(0, jsonNode.size(), "Serialized array node should have been empty");
    }

    @Test
    void multipleBundlesInFlow() {

        References refs = new References();
        refs.resourceBundleKeys.addAll(List.of("c", "b", "a"));

        List<ResourceBundle> bundles = Lists.newArrayList(
                resourceBundle("c", "C it", "it"),
                resourceBundle("c", "C en", "en"),
                resourceBundle("a", "A fr", "fr"),
                resourceBundle("b", "B fr", "fr"),
                resourceBundle("a", "A it", "it"),
                resourceBundle("b", "B en", "en"),
                resourceBundle("b", "B it", "it")
        );

        when(repo.findByRbkeyIn(refs.resourceBundleKeys)).thenReturn(bundles);

        ResourceBundleExporter exporter = new ResourceBundleExporter(repo);

        Map<String, JsonNode> exports = exporter.serializeEntitiesForFlow(flow("multiple"), refs);

        JsonNode jsonNode = exports.get(FileSuffix.RESOURCE_BUNDLE.prepend("multiple"));

        assertNotNull(jsonNode, "There should be an exported JsonNode");
        assertTrue(jsonNode.isArray(), "serialized node expected to be array.");
        assertEquals(7, jsonNode.size(), "Serialized array node should have seven nodes");
    }

    private void assertResourceBundle(String key, String value, String locale, JsonNode bundleNode) {
        assertFalse(bundleNode.has("uid"), "uids should not be serialized");
        assertEquals(key, bundleNode.path("rbkey").asText());
        assertEquals(value, bundleNode.path("rbvalue").asText());
        assertEquals(locale, bundleNode.path("locale").asText());
        assertEquals(ADMIN_UI.getType(), bundleNode.path("type").asText());
    }

    private ResourceBundle resourceBundle(String key, String value, String locale) {
        ResourceBundle bundle = new ResourceBundle();
        bundle.setUid(UUID.randomUUID());
        bundle.setRbkey(key);
        bundle.setRbvalue(value);
        bundle.setLocale(locale);
        bundle.setType(ADMIN_UI.getType());

        return bundle;
    }

    private Flow flow(String name) {
        Flow flow = new Flow();
        flow.setName(name);
        return flow;
    }
}