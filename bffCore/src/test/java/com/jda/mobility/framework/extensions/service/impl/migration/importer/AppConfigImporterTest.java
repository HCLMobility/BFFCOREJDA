package com.jda.mobility.framework.extensions.service.impl.migration.importer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.entity.AppConfigDetail;
import com.jda.mobility.framework.extensions.entity.AppConfigMaster;
import com.jda.mobility.framework.extensions.repository.AppConfigMasterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import static com.jda.mobility.framework.extensions.service.impl.migration.importer.AppConfigImporter.DEFAULT_OR_HOME_FLOW_UPDATE_NOTE;
import static com.jda.mobility.framework.extensions.service.impl.migration.importer.ImporterUtils.contextualInvoke;
import static com.jda.mobility.framework.extensions.service.impl.migration.importer.ImporterUtils.invoke;
import static com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.DEFAULT_FLOW_KEY;
import static com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.HOME_FLOW_KEY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ImporterTest
class AppConfigImporterTest {

    @Autowired
    private AppConfigMasterRepository repository;

    @Value(Resources.PATH + "appConfig/initial.app-configs.json")
    private Resource initialJson;

    @Value(Resources.PATH + "appConfig/update.app-configs.json")
    private Resource updateJson;

    @Value(Resources.PATH + "appConfig/add.app-configs.json")
    private Resource addJson;

    @Value(Resources.PATH + "appConfig/invalid.app-configs.json")
    private Resource invalidJson;

    private AppConfigImporter importer;

    @BeforeEach
    void setUp() {
        importer = new AppConfigImporter(repository, new ObjectMapper());
    }

    @Test
    void goodImport() {
        ImportContext context = contextualInvoke(importer, initialJson);

        assertEquals(Map.<String, List<String>>of(), context.getErrors());

        List<AppConfigMaster> configs = repository.findAll();

        assertThat(configs, hasSize(34));

        // Test a 'random' set of configs
        assertApplicationConfig("BOTTOMMARGIN", VALUE_TEN);
        assertApplicationConfig("BOTTOMPADDING", VALUE_TEN);
        assertApplicationConfig("CENTREPADDING", VALUE_TEN);
        assertApplicationConfig("CONTROLSPACING", "30");
        assertApplicationConfig("DEFAULT_FLOW_ID", "07af4433-b67d-4c86-ac30-b21f87ac74b3");
        assertConfigExists("WarehouseName", "GLOBAL");

        String noteKey = "$.initial.app-configs.json.%s/APPLICATION";

        Map<String, List<String>> notes = context.getNotes();

        List<String> homeFlowNotes = notes.get(String.format(noteKey, HOME_FLOW_KEY));

        assertEquals(
                List.of(String.format(DEFAULT_OR_HOME_FLOW_UPDATE_NOTE, HOME_FLOW_KEY, HOME_FLOW_ID, null)),
                homeFlowNotes
        );

        List<String> defaultFlowNotes = notes.get(String.format(noteKey, DEFAULT_FLOW_KEY));

        assertEquals(
                List.of(String.format(DEFAULT_OR_HOME_FLOW_UPDATE_NOTE, DEFAULT_FLOW_KEY, DEFAULT_FLOW_ID, null)),
                defaultFlowNotes
        );
    }

    @Test
    void updateImport() {
        goodImport();

        ImportContext context = contextualInvoke(importer, updateJson);

        assertEquals(Map.<String, List<String>>of(), context.getErrors());

        List<AppConfigMaster> configs = repository.findAll();

        assertThat(configs, hasSize(34));

        // Check updated configs
        assertApplicationConfig("BOTTOMMARGIN", VALUE_ELEVEN);
        assertApplicationConfig("BOTTOMPADDING", VALUE_ELEVEN);
        assertApplicationConfig("CENTREPADDING", VALUE_ELEVEN);
        assertApplicationConfig("CONTROLSPACING", "31");
        assertApplicationConfig("DEFAULT_FLOW_ID", UPDATED_FLOW_ID);

        Map<String, List<String>> notes = context.getNotes();

        String noteKey = "$.update.app-configs.json.%s/APPLICATION";

        List<String> homeFlowNotes = notes.get(String.format(noteKey, HOME_FLOW_KEY));

        assertEquals(
                List.of(String.format(DEFAULT_OR_HOME_FLOW_UPDATE_NOTE,
                        HOME_FLOW_KEY, UPDATED_FLOW_ID, HOME_FLOW_ID)),
                homeFlowNotes
        );

        List<String> defaultFlowNotes = notes.get(String.format(noteKey, DEFAULT_FLOW_KEY));

        assertEquals(
                List.of(String.format(DEFAULT_OR_HOME_FLOW_UPDATE_NOTE,
                        DEFAULT_FLOW_KEY, UPDATED_FLOW_ID, DEFAULT_FLOW_ID)),
                defaultFlowNotes
        );
    }

    @Test
    void addImport() {
        goodImport();

        List<String> issues = invoke(importer, addJson);

        assertThat(issues, hasSize(0));

        List<AppConfigMaster> configs = repository.findAll();


        assertThat(configs, hasSize(36));

        assertConfigExists("Other", "CONTEXT");
        assertConfigExists("NewOne", "CONTEXT");
    }

    @Test
    void invalidImport() {

        List<String> issues = invoke(importer, invalidJson);

        assertThat(issues, hasSize(1));
        assertThat(issues, hasItem(containsString("Multiple details found for config")));

        List<AppConfigMaster> configs = repository.findAll();

        assertThat(configs, hasSize(0));
    }

    @Test
    void malformedInput() {
        List<String> issues = invoke(importer, "bad input".getBytes(), "bad.json");

        assertEquals(1, issues.size(), "There should have been an issue importing");
        assertEquals(List.of("Unable to parse file."), issues);
    }

    private void assertApplicationConfig(String configName, String value) {
        AppConfigMaster config = assertConfigExists(configName, APPLICATION);
        List<AppConfigDetail> details = config.getAppConfigDetails();
        assertThat(details, is(not(empty())));
        assertEquals(value, getStaticValue(details).getConfigValue());
    }

    private AppConfigMaster assertConfigExists(String configName, String configType) {
        AppConfigMaster config = repository.findByConfigNameAndConfigType(configName, configType);
        assertNotNull(config);
        return config;
    }

    private AppConfigDetail getStaticValue(List<AppConfigDetail> details) {
        List<AppConfigDetail> matching = details.stream()
                .filter(d -> d.getFlowId() == null && d.getUserId() == null && d.getDeviceName() == null)
                .collect(Collectors.toList());

        assertThat(matching, hasSize(1));
        return matching.get(0);
    }

    private static final String APPLICATION = "APPLICATION";
    private static final String VALUE_TEN = "10";
    private static final String VALUE_ELEVEN = "11";
    private static final String DEFAULT_FLOW_ID = "07af4433-b67d-4c86-ac30-b21f87ac74b3";
    private static final String HOME_FLOW_ID = "be0757de-24fc-4f5c-bc52-28ceccb0a2de";
    private static final String UPDATED_FLOW_ID = "aaaaaaaa-b67d-4c86-ac30-b21f87ac74b3";
}