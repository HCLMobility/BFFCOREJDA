package com.jda.mobility.framework.extensions.service.impl.migration.importer;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.entity.MenuMaster;
import com.jda.mobility.framework.extensions.repository.MenuMasterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import static com.jda.mobility.framework.extensions.service.impl.migration.importer.ImporterUtils.invoke;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ImporterTest
class MenuImporterTest {

    @Autowired
    private MenuMasterRepository repository;

    @Value(Resources.PATH + "menu/main.menus.json")
    private Resource mainJson;

    @Value(Resources.PATH + "menu/form.menus.json")
    private Resource formJson;

    @Value(Resources.PATH + "menu/tweaked-form.menus.json")
    private Resource tweakedFormJson;

    @Value(Resources.PATH + "menu/tweaked-main.menus.json")
    private Resource tweakedMainJson;

    private MenuImporter importer;

    @BeforeEach
    void setUp() {
        importer = new MenuImporter(repository, new ObjectMapper());
    }

    @Test
    void importFormMenus() {
        List<String> issues = invoke(importer, formJson);

        assertThat(issues, hasSize(0));

        List<MenuMaster> menus =
                repository.findByLinkedFormId(UUID.fromString("c8755d0e-c896-4f71-88ce-34064ff09ed6"));

        assertThat(menus, hasSize(2));

        Set<String> menuNames = menus.stream().map(MenuMaster::getMenuName).collect(Collectors.toSet());

        assertEquals(Set.of("menu-1", "menu-2"), menuNames);
    }

    @Test
    void importTweakedFormMenus() {
        importFormMenus();
        List<String> issues = invoke(importer, tweakedFormJson);

        assertThat(issues, hasSize(0));

        List<MenuMaster> menus =
                repository.findByLinkedFormId(UUID.fromString("c8755d0e-c896-4f71-88ce-34064ff09ed6"));

        assertThat(menus, hasSize(1));

        Set<String> menuNames = menus.stream().map(MenuMaster::getMenuName).collect(Collectors.toSet());

        assertEquals(Set.of("different-name"), menuNames);
    }

    @Test
    void importMainMenus() {
        List<String> issues = invoke(importer, mainJson);

        assertThat(issues, hasSize(0));

        List<MenuMaster> menus =
                repository.findAllByLinkedFormIdIsNullOrderByMenuName();

        assertThat(menus, hasSize(2));

        Set<String> menuNames = menus.stream().map(MenuMaster::getMenuName).collect(Collectors.toSet());

        assertEquals(Set.of("root", "sub"), menuNames);
    }

    @Test
    void importTweakedMainMenus() {
        importMainMenus();

        List<String> issues = invoke(importer, tweakedMainJson);

        assertThat(issues, hasSize(0));

        List<MenuMaster> menus =
                repository.findAllByLinkedFormIdIsNullOrderByMenuName();

        assertThat(menus, hasSize(3));

        Set<String> menuNames = menus.stream().map(MenuMaster::getMenuName).collect(Collectors.toSet());

        assertEquals(Set.of("root", "sub1", "sub2"), menuNames);
    }

    @Test
    void malformedInput() {
        List<String> issues = invoke(importer, "bad input".getBytes(), "bad.json");

        assertEquals(1, issues.size(), "There should have been an issue importing");
        assertEquals(List.of("Unable to parse file."), issues);
    }
}