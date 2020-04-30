package com.jda.mobility.framework.extensions.service.impl.migration.importer;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.entity.ApiRegistry;
import com.jda.mobility.framework.extensions.repository.ApiRegistryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.context.jdbc.SqlMergeMode.MergeMode;

import static com.jda.mobility.framework.extensions.service.impl.migration.importer.ImporterUtils.invoke;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ImporterTest
class ApiRegistryImporterTest {

    @Autowired
    private ApiRegistryRepository repository;

    @Value(Resources.PATH + "registry/good.registries.json")
    private Resource goodJson;

    private ApiRegistryImporter importer;

    @BeforeEach
    void init() {
        importer = new ApiRegistryImporter(repository, new ObjectMapper());
    }

    @Test
    void goodImport() {

        List<String> issues = invoke(importer, goodJson);

        Set<String> importedRegistryNames =
                repository.findAllByOrderByName()
                        .stream()
                        .map(ApiRegistry::getName)
                        .collect(Collectors.toSet());

        assertEquals(Set.of("yard", "picking", "device"), importedRegistryNames);

        assertEquals(List.of(), issues);
    }

    @Test
    @SqlMergeMode(MergeMode.MERGE)
    @Sql(scripts = "registry/preexisting-registries.sql")
    void goodImportWithPreexistingRegistries() {
        List<String> issues = invoke(importer, goodJson);

        Set<String> importedRegistryNames =
                repository.findAllByOrderByName()
                        .stream()
                        .map(ApiRegistry::getName)
                        .collect(Collectors.toSet());

        assertEquals(Set.of("yard", "picking", "device"), importedRegistryNames);

        assertEquals(List.of(), issues);
    }

    @Test
    void malformedInput() {
        List<String> issues = invoke(importer, "bad input".getBytes(), "bad.json");

        assertEquals(1, issues.size(), "There should have been an issue importing");
        assertEquals(List.of("Unable to parse file."), issues);
    }

}