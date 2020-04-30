package com.jda.mobility.framework.extensions.service.impl.migration.importer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.sql.DataSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.entity.Form;
import com.jda.mobility.framework.extensions.repository.FlowRepository;
import com.jda.mobility.framework.extensions.utils.FormDependencyUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;

import static com.jda.mobility.framework.extensions.service.impl.migration.importer.FlowConflictResolutionStrategy.IGNORE_NEW;
import static com.jda.mobility.framework.extensions.service.impl.migration.importer.FlowConflictResolutionStrategy.RENAME_NEW;
import static com.jda.mobility.framework.extensions.service.impl.migration.importer.ImporterUtils.read;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ImporterTest
@Import(FormDependencyUtil.class)
class FormFlowImporterTest {

    @Autowired
    private FlowRepository repository;

    @Autowired
    private FormDependencyUtil interdependencies;

    @Autowired
    private DataSource dataSource;

    @Value(Resources.PATH + "formFlow/good.flow.json")
    private Resource goodFlow;

    @Value(Resources.PATH + "formFlow/good-form1.form.json")
    private Resource goodForm1;

    @Value(Resources.PATH + "formFlow/good-form2.form.json")
    private Resource goodForm2;

    @Value(Resources.PATH + "formFlow/conflicting.flow.json")
    private Resource conflictingFlow;

    private MockDiffTool diffTool;

    private FormFlowImporter importer;

    @BeforeEach
    void setUp() {
        diffTool = new MockDiffTool();
        importer = new FormFlowImporter(repository, interdependencies, new ObjectMapper(), diffTool);
    }

    @Test
    void goodImport() {
        ImportContext context = context(IGNORE_NEW);
        importer.doImport(read(goodFlow), List.of(read(goodForm1), read(goodForm2)), context);

        assertThat(context.getErrors().values(), hasSize(0));

        assertNull(diffTool.original);
        assertEquals("be0757de-24fc-4f5c-bc52-28ceccb0a2de", diffTool.updated.getUid().toString());

        Flow flow = repository.findById(UUID.fromString("be0757de-24fc-4f5c-bc52-28ceccb0a2de"))
                .orElseThrow(() -> new NoSuchElementException("Flow was not successfully imported"));

        Set<String> names = flow.getForms().stream().map(Form::getName).collect(Collectors.toSet());

        assertEquals(Set.of("Howdy", "Next"), names);
    }

    @Test
    void updateImport() {
        goodImport();

        ImportContext context = context(RENAME_NEW);

        // This is not ideal, but h2 isn't handling foreign key constraints
        // properly. Since h2 is only used for testing, we're just going
        // to temporarily disable foreign key constraints while doing this
        // update. Currently, the constraints work correctly in SQL Server
        // so I feel only somewhat uncomfortable about disabling them here
        // in the test.
        withoutConstraints(() ->
                importer.doImport(read(goodFlow), List.of(read(goodForm1)), context)
        );

        assertThat(context.getErrors().values(), hasSize(0));
        assertEquals("be0757de-24fc-4f5c-bc52-28ceccb0a2de", diffTool.original.getUid().toString());
        assertEquals("be0757de-24fc-4f5c-bc52-28ceccb0a2de", diffTool.updated.getUid().toString());

        Flow flow = repository.findById(UUID.fromString("be0757de-24fc-4f5c-bc52-28ceccb0a2de"))
                .orElseThrow(() -> new NoSuchElementException("Flow was not successfully imported"));

        Set<String> names = flow.getForms().stream().map(Form::getName).collect(Collectors.toSet());

        assertEquals(Set.of("Howdy"), names);
    }

    @Test
    void conflictRenameNew() {
        goodImport();

        ImportContext context = context(RENAME_NEW);

        // See the full comment about this in the updateImport test
        withoutConstraints(() ->
                importer.doImport(read(conflictingFlow), List.of(), context)
        );

        Flow flow = repository.findById(UUID.fromString("be0757de-24fc-1234-5678-28ceccb0a2de"))
                .orElseThrow(() -> new NoSuchElementException("Flow was not successfully imported"));

        Flow oldFlow = repository.findById(UUID.fromString("be0757de-24fc-4f5c-bc52-28ceccb0a2de"))
                .orElseThrow(() -> new NoSuchElementException("Flow was not successfully imported"));


        assertThat(context.getErrors().values(), hasSize(0));
        assertThat(flow.getName(), containsString(flow.getUid().toString()));
        assertThat(oldFlow.getName(), is("Test"));
    }

    @Test
    void conflictRenameNewMultipleTimes() {
        goodImport();

        ImportContext context = context(RENAME_NEW);

        // See the full comment about this in the updateImport test
        withoutConstraints(() -> {
            importer.doImport(read(conflictingFlow), List.of(), context);
            importer.doImport(read(conflictingFlow), List.of(), context);
            importer.doImport(read(conflictingFlow), List.of(), context);
            importer.doImport(read(conflictingFlow), List.of(), context);
        });

        Flow flow = repository.findById(UUID.fromString("be0757de-24fc-1234-5678-28ceccb0a2de"))
                .orElseThrow(() -> new NoSuchElementException("Flow was not successfully imported"));

        Flow oldFlow = repository.findById(UUID.fromString("be0757de-24fc-4f5c-bc52-28ceccb0a2de"))
                .orElseThrow(() -> new NoSuchElementException("Flow was not successfully imported"));

        assertThat(context.getErrors().values(), hasSize(0));
        List<String> notes = context.getNotes().get("$.be0757de-24fc-1234-5678-28ceccb0a2de");
        assertThat(notes, hasSize(4));
        assertThat(notes, allOf(everyItem(containsString("Detected conflict"))));
        assertThat(flow.getName(), containsString(flow.getUid().toString()));
        assertThat(oldFlow.getName(), is("Test"));
    }

    @Test
    void conflictIgnoreNew() {
        goodImport();

        ImportContext context = context(IGNORE_NEW);

        // See the full comment about this in the updateImport test
        withoutConstraints(() ->
                importer.doImport(read(conflictingFlow), List.of(), context)
        );

        assertNull(repository.findById(UUID.fromString("be0757de-24fc-1234-5678-28ceccb0a2de"))
                .orElse(null));

        Flow flow = repository.findById(UUID.fromString("be0757de-24fc-4f5c-bc52-28ceccb0a2de"))
                .orElseThrow(() -> new NoSuchElementException("Flow was not successfully imported"));


        assertThat(context.getErrors().values(), hasSize(0));
        List<String> notes = context.getNotes().get("$.be0757de-24fc-1234-5678-28ceccb0a2de");
        assertThat(notes, hasSize(1));
        assertThat(notes, allOf(everyItem(containsString("Detected conflict"))));
        assertThat(flow.getName(), is("Test"));
    }

    @Test
    void malformedFlow() {
        ImportContext context = context(IGNORE_NEW);
        byte[] badBytes = "bad input".getBytes();
        importer.doImport(badBytes, List.of(read(goodForm1)), context);

        assertEquals(1, context.getErrors().size(), "There should have been an issue importing");
        assertEquals(List.of("Unable to parse entity of type : " + Flow.class), context.getErrors().get("$"));
    }

    @Test
    void malformedForm() {
        ImportContext context = context(RENAME_NEW);
        byte[] badBytes = "bad input".getBytes();
        importer.doImport(read(goodFlow), List.of(read(goodForm1), badBytes), context);

        assertEquals(1, context.getErrors().size(), "There should have been an issue importing");
        assertEquals(List.of("Unable to parse entity of type : " + Form.class), context.getErrors().get("$"));
    }

    private void withoutConstraints(Runnable runnable) {
        try (Connection connection = dataSource.getConnection()) {
            connection.createStatement().execute("SET foreign_key_checks = 0");
            try {
                runnable.run();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            connection.createStatement().execute("SET foreign_key_checks = 1");
        }
        catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private ImportContext context(FlowConflictResolutionStrategy strategy) {
        return new ImportContext(false, "form-flow", strategy);
    }

    private static class MockDiffTool extends FlowDiffTool {

        private Flow original;
        private Flow updated;

        MockDiffTool() {
            super(null);
        }

        @Override
        public Map<String, JsonPatch> diff(Flow current, Flow target) {
            this.original = current;
            this.updated = target;
            return Map.of();
        }
    }


}