package com.jda.mobility.framework.extensions.service.impl.migration.exporter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.entity.MenuMaster;
import com.jda.mobility.framework.extensions.repository.MenuMasterRepository;
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

class MenuExporterTest {

    @Mock
    private MenuMasterRepository repo;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void noMenus() {
        when(repo.findAllByLinkedFormIdIsNullOrderByMenuName()).thenReturn(List.of());
        MenuExporter exporter = new MenuExporter(repo, mapper);

        JsonNode jsonNode = exporter.serializeExportableEntities(UUID.randomUUID());

        assertTrue(jsonNode.isArray(), "serialized node expected to be array.");
        assertEquals(0, jsonNode.size(), "Serialized array node should have been empty");
    }

    @Test
    void oneMenu() {
        MenuMaster menu = menu("my-one-menu");
        when(repo.findAllByLinkedFormIdIsNullOrderByMenuName()).thenReturn(List.of(menu));
        MenuExporter exporter = new MenuExporter(repo, mapper);

        JsonNode jsonNode = exporter.serializeExportableEntities(UUID.randomUUID());

        assertTrue(jsonNode.isArray(), "serialized node expected to be array.");
        assertEquals(1, jsonNode.size(), "Serialized array node should contain one menu");

        assertMenuNode(menu.getUid(), "my-one-menu", jsonNode.path(0));
    }

    @Test
    void multipleMenus() {
        MenuMaster menu1 = menu("my-first-menu");
        MenuMaster menu2 = menu("my-second-menu");
        MenuMaster menu3 = menu("my-third-menu");
        MenuMaster menu4 = menu("my-fourth-menu");
        when(repo.findAllByLinkedFormIdIsNullOrderByMenuName())
                .thenReturn(List.of(menu1, menu2, menu3, menu4));
        MenuExporter exporter = new MenuExporter(repo, mapper);

        JsonNode jsonNode = exporter.serializeExportableEntities(UUID.randomUUID());

        assertTrue(jsonNode.isArray(), "serialized node expected to be array.");
        assertEquals(4, jsonNode.size(), "Serialized array node should contain one menu");

        assertMenuNode(menu1.getUid(), "my-first-menu", jsonNode.path(0));
        assertMenuNode(menu2.getUid(), "my-second-menu", jsonNode.path(1));
        assertMenuNode(menu3.getUid(), "my-third-menu", jsonNode.path(2));
        assertMenuNode(menu4.getUid(), "my-fourth-menu", jsonNode.path(3));
    }

    @Test
    void noMenusForFlow() {
        // We're not going to pass in any menu ids, so this method should not actually
        // be called. Even so, we'll add a value to return so if it does get called we'll
        // detect that. I prefer this to making verify calls.
        when(repo.findById(any(UUID.class)))
                .thenReturn(Optional.of(new MenuMaster()));
        MenuExporter exporter = new MenuExporter(repo, mapper);
        Map<String, JsonNode> exports =
                exporter.serializeEntitiesForFlow(flow("none"), new References());

        JsonNode jsonNode = exports.get(FileSuffix.MENU.prepend("none"));

        assertNotNull(jsonNode, "There should be an exported JsonNode");
        assertTrue(jsonNode.isArray(), "serialized node expected to be array.");
        assertEquals(0, jsonNode.size(), "Serialized array node should have been empty");
    }

    @Test
    void multipleMenusForFlow() {
        MenuMaster one = menu("one", UUID.randomUUID());
        MenuMaster two = menu("two", UUID.randomUUID());
        Map<UUID, MenuMaster> menus = Map.of(
                one.getUid(), one,
                two.getUid(), two
        );

        References refs = new References();
        refs.menuIds.addAll(List.of("", two.getUid().toString(), one.getUid().toString()));

        when(repo.findById(any(UUID.class))).thenAnswer(invocation -> {
            UUID uid = invocation.getArgument(0, UUID.class);
            return Optional.of(menus.get(uid));
        });

        MenuExporter exporter = new MenuExporter(repo, mapper);
        Map<String, JsonNode> exports =
                exporter.serializeEntitiesForFlow(flow("multiple"), refs);

        JsonNode jsonNode = exports.get(FileSuffix.MENU.prepend("multiple"));

        assertNotNull(jsonNode, "There should be an exported JsonNode");
        assertTrue(jsonNode.isArray(), "serialized node expected to be array.");
        assertEquals(2, jsonNode.size(), "Serialized array node should have two nodes");

        assertMenuNode(one.getUid(), "one", jsonNode.path(0));
        assertMenuNode(two.getUid(), "two", jsonNode.path(1));
    }

    private void assertMenuNode(UUID uid, String name, JsonNode jsonNode) {
        assertEquals(uid.toString(), jsonNode.path("uid").asText());
        assertEquals(name, jsonNode.path("menuName").asText());
    }

    private MenuMaster menu(String name) {
        return menu(name, UUID.randomUUID());
    }

    private MenuMaster menu(String name, UUID uid) {
        MenuMaster menu = new MenuMaster();
        menu.setUid(uid);
        menu.setMenuName(name);
        return menu;
    }

    private Flow flow(String name) {
        Flow flow = new Flow();
        flow.setName(name);
        return flow;
    }
}