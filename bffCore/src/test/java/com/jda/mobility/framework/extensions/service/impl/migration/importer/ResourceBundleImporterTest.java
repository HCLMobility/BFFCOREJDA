package com.jda.mobility.framework.extensions.service.impl.migration.importer;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.entity.ResourceBundle;
import com.jda.mobility.framework.extensions.repository.ResourceBundleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import static com.jda.mobility.framework.extensions.service.impl.migration.importer.ImporterUtils.invoke;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ImporterTest
class ResourceBundleImporterTest {

    @Autowired
    private ResourceBundleRepository repository;

    private ResourceBundleImporter importer;

    @Value(Resources.PATH + "resourceBundle/good.resource-bundles.json")
    private Resource goodJson;

    @Value(Resources.PATH + "resourceBundle/tweaked.resource-bundles.json")
    private Resource tweakedJson;

    @BeforeEach
    void setUp() {
        importer = new ResourceBundleImporter(repository, new ObjectMapper());
    }

    @Test
    void goodImport() {
        List<String> issues = invoke(importer, goodJson);

        assertThat(issues, hasSize(0));

        assertEquals(5, repository.count());

        assertBundle("a-label", EN_US, "AAAAA");
        assertBundle("disabled-button-maybe", EN_US, "Possibly Disabled");
        assertBundle("fancy-label", EN_US, "Fancy Labeled Button!");
        assertBundle("menu-1", EN_US, "Menu 1");
        assertBundle("menu-2", EN_US, "Too");
    }

    @Test
    void tweakedImport() {
        goodImport();
        List<String> issues = invoke(importer, tweakedJson);

        assertThat(issues, hasSize(0));

        assertEquals(6, repository.count());

        assertBundle("a-label", EN_US, "AAAAA");
        assertBundle("disabled-button-maybe", EN_US, "Possibly Disabled");
        assertBundle("fancy-label", EN_US, "Fancy Labeled Button! with extra text");
        assertBundle("menu-1", EN_US, "Menu 1");
        assertBundle("menu-2", EN_US, "Too");
        assertBundle("newOne", "fr-FR", "Nouveau");
    }

    @Test
    void malformedInput() {
        List<String> issues = invoke(importer, "bad input".getBytes(), "bad.json");

        assertEquals(1, issues.size(), "There should have been an issue importing");
        assertEquals(List.of("Unable to parse file."), issues);
    }

    private void assertBundle(String key, String locale, String value) {
        ResourceBundle bundle =
                repository.findByLocaleAndRbkeyAndType(locale, key, "ADMIN_UI");

        assertNotNull(bundle, "expected bundle to exist for key " + key);

        assertEquals(value, bundle.getRbvalue());
    }

    private static final String EN_US = "en-US";
}