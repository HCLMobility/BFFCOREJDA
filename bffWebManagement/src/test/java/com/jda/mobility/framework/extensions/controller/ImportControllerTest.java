package com.jda.mobility.framework.extensions.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.jda.mobility.framework.extensions.service.ImportService;
import com.jda.mobility.framework.extensions.service.impl.migration.importer.ImportContext;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ImportAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import static com.jda.mobility.framework.extensions.service.impl.migration.importer.FlowConflictResolutionStrategy.IGNORE_NEW;
import static com.jda.mobility.framework.extensions.service.impl.migration.importer.FlowConflictResolutionStrategy.RENAME_NEW;
import static com.jda.mobility.framework.extensions.util.UnzipHelper.unzip;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

class ImportControllerTest {

    @Mock
    private MultipartFile multipartFile;

    @BeforeEach
    void beforeEach() {
        initMocks(this);
    }

    @Test
    void validateWithErrors() throws IOException {
        ImportController controller = new ImportController(
                new MockImportService(Map.of(), List.of("There was a problem")));
        MockHttpServletResponse response = new MockHttpServletResponse();
        controller.importFile(multipartFile, ImportAction.VALIDATE, IGNORE_NEW, response);

        Map<String, JsonNode> files = unzip(response.getContentAsByteArray());
        JsonNode results = files.get("import-results.json");
        assertEquals("There was a problem", results.path("errors").path("$").path(0).asText());
        assertTrue(results.path("dryRun").asBoolean(), "dryRun should be set to true");
        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
    }

    @Test
    void validateWithNoErrors() throws IOException {
        ImportController controller = new ImportController(
                new MockImportService(Map.of(), List.of()));
        MockHttpServletResponse response = new MockHttpServletResponse();
        controller.importFile(multipartFile, ImportAction.VALIDATE, RENAME_NEW, response);

        Map<String, JsonNode> files = unzip(response.getContentAsByteArray());
        JsonNode results = files.get("import-results.json");
        assertTrue(results.path("errors").isEmpty(), "issues should be empty");
        assertTrue(results.path("dryRun").asBoolean(), "dryRun should be set to true");
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    void validateDefaultConflictResolutionStrategy() throws IOException {
        ImportController controller = new ImportController(
                new MockImportService(Map.of(), List.of()));

        MockHttpServletResponse response = new MockHttpServletResponse();
        controller.importFile(multipartFile, ImportAction.VALIDATE, null, response);

        Map<String, JsonNode> files = unzip(response.getContentAsByteArray());
        assertEquals("RENAME_NEW",
                files.get("import-results.json").path("flowConflictResolutionStrategy").asText());
    }

    @Test
    void importWithNoErrors() throws IOException {

        ImportController controller = new ImportController(
                new MockImportService(
                        Map.of("good.flow.json", JsonNodeFactory.instance.textNode("good!")),
                        List.of())
        );
        MockHttpServletResponse response = new MockHttpServletResponse();
        controller.importFile(multipartFile, ImportAction.SAVE_PUBLISH, IGNORE_NEW, response);

        Map<String, JsonNode> files = unzip(response.getContentAsByteArray());
        JsonNode results = files.get("import-results.json");
        assertTrue(results.path("errors").isEmpty(), "issues should be empty");
        assertFalse(results.path("dryRun").asBoolean(), "dryRun should be set to false");
        assertEquals("good!", files.get("good.flow.json").asText());
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    void importWithErrors() throws IOException {
        ImportController controller = new ImportController(
            new MockImportService(Map.of(), List.of("Bad Stuff!")));

        MockHttpServletResponse response = new MockHttpServletResponse();
        controller.importFile(multipartFile, ImportAction.SAVE_PUBLISH, RENAME_NEW, response);

        Map<String, JsonNode> files = unzip(response.getContentAsByteArray());
        assertEquals("Bad Stuff!",
                files.get("import-results.json").path("errors").path("$").path(0).asText());
        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
    }

    private static class MockImportService implements ImportService {

        private final Map<String, JsonNode> results;
        private final List<String> issues;

        MockImportService(Map<String, JsonNode> results, List<String> issues) {
            this.results = new HashMap<>(results);
            this.issues = issues;
        }

        @Override
        public Map<String, JsonNode> importFile(MultipartFile file,
                                                ImportContext context) {
            issues.forEach(context::raise);
            return results;
        }
    }
}
