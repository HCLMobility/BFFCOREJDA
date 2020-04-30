package com.jda.mobility.framework.extensions.service.impl.migration.importer;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.util.RawValue;
import com.jda.mobility.framework.extensions.service.impl.migration.FileSuffix;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import static com.jda.mobility.framework.extensions.service.impl.migration.importer.FlowConflictResolutionStrategy.RENAME_NEW;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ImportCoordinatorTest {

    private ImportCoordinator coordinator;
    private MockTransactionTemplate template;
    private MockBasicImporter basicImporter;
    private MockFormFlowImporter formFlowImporter;
    private ObjectMapper mapper;

    @BeforeEach
    void init() {
        mapper = new ObjectMapper().configure(Feature.AUTO_CLOSE_TARGET, false);
        template = new MockTransactionTemplate();
        basicImporter = new MockBasicImporter();
        formFlowImporter = new MockFormFlowImporter();
        coordinator = new ImportCoordinator(
                Set.of(basicImporter), formFlowImporter, template);
    }

    @Test
    void dryRunEmptyZip() {
        ImportContext context = context(true);
        coordinator.importFile(new MockMultipartFile("mock.zip", writeToBytes(Map.of())), context);
        verify(template.status, times(1)).setRollbackOnly();
        assertThat(context.getErrors().values(), hasSize(0));
    }

    @Test
    void dryRunWithValidData() {
        Map<String, JsonNode> zipContents =
                Map.of(FileSuffix.MENU.prepend("test"), array(text("menu data")));
        ImportContext context = context(true);
        coordinator.importFile(new MockMultipartFile("mock.zip", writeToBytes(zipContents)), context);

        assertEquals(Map.of("/test.menus.json", "menu data"), basicImporter.importedData);

        verify(template.status, times(1)).setRollbackOnly();
        assertThat(context.getErrors().values(), hasSize(0));
    }

    @Test
    void unrecognizedFilesAreIgnored() {
        Map<String, JsonNode> zipContents =
                Map.of("something.else.json", text("something else"));
        ImportContext context = context();
        coordinator.importFile(new MockMultipartFile("mock.zip", writeToBytes(zipContents)), context);

        assertEquals(Map.of(), basicImporter.importedData);

        verify(template.status, never()).setRollbackOnly();
        assertThat(context.getErrors().values(), hasSize(0));
    }

    @Test
    void allSupportedFiles() {
        Map<String, JsonNode> zipContents = new HashMap<>();
        Map<String, String> expected = new TreeMap<>();

        for (FileSuffix value : FileSuffix.values()) {
            String suffix = value.suffix();
            String fileName = value.prepend("test");

            expected.put("/" + fileName, suffix);

            TextNode text = text(suffix);
            if (value != FileSuffix.FORM && value != FileSuffix.FLOW) {
                // non-form/non-flow imports contents are all JSON arrays
                zipContents.put(fileName, array(text));
            }
            else {
                zipContents.put(fileName, text);
            }
        }

        ImportContext context = context();
        coordinator.importFile(new MockMultipartFile("mock.zip", writeToBytes(zipContents)), context);

        Map<String, String> actuals = new TreeMap<>(basicImporter.importedData);
        actuals.put("/" + FileSuffix.FLOW.prepend("test"), formFlowImporter.flowData);
        actuals.put("/" + FileSuffix.FORM.prepend("test"), formFlowImporter.formData.get(0));

        assertEquals(expected, actuals);

        verify(template.status, never()).setRollbackOnly();
        assertThat(context.getErrors().values(), hasSize(0));
    }

    @Test
    void invalidSupportedFiles() {
        Map<String, JsonNode> zipContents = new HashMap<>();
        for (FileSuffix value : FileSuffix.values()) {
            zipContents.put(value.prepend("test"), invalidJson());
        }
        ImportContext context = context();
        coordinator.importFile(new MockMultipartFile("mock.zip", writeToBytes(zipContents)), context);

        verify(template.status, times(1)).setRollbackOnly();
        // 9 (instead of 10) because Forms are skipped if flows don't parse.
        assertThat(context.getErrors().values(), hasSize(9));
    }

    private JsonNode array(JsonNode node) {
        return JsonNodeFactory.instance.arrayNode(1).add(node);
    }

    private JsonNode invalidJson() {
        return JsonNodeFactory.instance.rawValueNode(new RawValue("}{]["));
    }

    private TextNode text(String text) {
        return JsonNodeFactory.instance.textNode(text);
    }

    private byte[] writeToBytes(Map<String, JsonNode> exports) {
        ObjectWriter writer = mapper.writer();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            try (BufferedOutputStream buffer = new BufferedOutputStream(out);
                 ZipOutputStream zip = new ZipOutputStream(buffer)) {
                for (Entry<String, JsonNode> entry : exports.entrySet()) {
                    zip.putNextEntry(new ZipEntry(entry.getKey()));
                    writer.writeValue(zip, entry.getValue());
                }
            }
        }
        catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

        return out.toByteArray();
    }

    private ImportContext context() {
        return context(false);
    }

    private ImportContext context(boolean dryRun) {
        return new ImportContext(dryRun, "form-flow", RENAME_NEW);
    }



    private static class MockTransactionTemplate extends TransactionTemplate {

        final TransactionStatus status = mock(TransactionStatus.class);

        @Override
        public <T> T execute(TransactionCallback<T> action) throws TransactionException {
            return action.doInTransaction(status);
        }
    }

    private static class MockBasicImporter implements BasicImporter<TextNode> {

        final Map<String, String> importedData = new HashMap<>();

        @Override
        public boolean canImport(FileSuffix suffix) {
            return suffix != FileSuffix.FLOW && suffix != FileSuffix.FORM;
        }

        @Override
        public TypeReference<List<TextNode>> deserializationTarget() {
            return new TypeReference<>() {};
        }

        @Override
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }

        @Override
        public void doImport(byte[] contents, Path fileToImport, ImportContext context) {
            List<TextNode> textNodes = deserialize(contents, context);
            if (CollectionUtils.isNotEmpty(textNodes)) {
                importedData.put(fileToImport.toString(), textNodes.get(0).asText());
            }
        }
    }

    private static class MockFormFlowImporter extends FormFlowImporter {

        String flowData;
        final List<String> formData = new ArrayList<>();

        MockFormFlowImporter() {
            super(null, null, new ObjectMapper(), null);
        }

        @Override
        public Map<String, JsonNode> doImport(byte[] flowBytes,
                                              List<byte[]> formsBytes,
                                              ImportContext context) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                flowData = mapper.readTree(flowBytes).asText();
                for (byte[] bytes : formsBytes) {
                    formData.add(mapper.readTree(bytes).asText());
                }
            }
            catch (IOException e) {
                // Let the superclass fail to parse and populate the issues.
                return super.doImport(flowBytes, formsBytes, context);
            }
            return Map.of();
        }
    }

}