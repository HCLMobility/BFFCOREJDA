package com.jda.mobility.framework.extensions.service.impl.migration.importer;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.entity.CustomComponentMaster;
import com.jda.mobility.framework.extensions.repository.CustomComponentMasterRepository;
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
class CustomComponentImporterTest {

    @Autowired
    private CustomComponentMasterRepository repository;

    private CustomComponentImporter importer;

    @Value(Resources.PATH + "customComponent/good.custom-components.json")
    private Resource goodJson;

    @Value(Resources.PATH + "customComponent/tweaked.custom-components.json")
    private Resource tweakedJson;

    @BeforeEach
    void setUp() {
        importer = new CustomComponentImporter(repository, new ObjectMapper());
    }

    @Test
    void goodImport() {
        List<String> issues = invoke(importer, goodJson);

        assertThat(issues, hasSize(0));

        CustomComponentMaster component =
                repository.findById(UUID.fromString("dc841587-75c3-4193-9b12-efbe8424c117")).orElseThrow();

        assertThat(component.getFields(), hasSize(2));
        assertEquals("Test-this-out", component.getName());
    }

    @Test
    void tweakImport() {
        goodImport();

        List<String> issues = invoke(importer, tweakedJson);

        assertThat(issues, hasSize(0));

        CustomComponentMaster component =
                repository.findById(UUID.fromString("dc841587-75c3-4193-9b12-efbe8424c117")).orElseThrow();

        assertThat(component.getFields(), hasSize(1));
        assertEquals("Test-this-out", component.getName());
    }

    @Test
    void malformedInput() {
        List<String> issues = invoke(importer, "bad input".getBytes(), "bad.json");

        assertEquals(1, issues.size(), "There should have been an issue importing");
        assertEquals(List.of("Unable to parse file."), issues);
    }
}