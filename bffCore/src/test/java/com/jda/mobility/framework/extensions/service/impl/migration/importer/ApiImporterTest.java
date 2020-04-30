package com.jda.mobility.framework.extensions.service.impl.migration.importer;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.entity.ApiMaster;
import com.jda.mobility.framework.extensions.entity.ApiRegistry;
import com.jda.mobility.framework.extensions.repository.ApiMasterRepository;
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
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ImporterTest
class ApiImporterTest {

    @Autowired
    private ApiMasterRepository apiRepo;

    @Autowired
    private ApiRegistryRepository registryRepo;

    @Value(Resources.PATH + "api/good.apis.json")
    private Resource goodJson;

    @Value(Resources.PATH + "api/bad.apis.json")
    private Resource badJson;

    private ApiImporter importer;

    @BeforeEach
    void setUp() {
        importer = new ApiImporter(apiRepo, registryRepo, new ObjectMapper());
    }

    @Test
    @SqlMergeMode(MergeMode.MERGE)
    @Sql("registry/preexisting-registries.sql")
    void goodImport() {
        List<String> issues = invoke(importer, goodJson);

        assertEquals(List.of(), issues);

        List<ApiMaster> apis = apiRepo.findAll();
        assertEquals(3, apis.size());

        Set<String> endpoints =
                apis.stream().map(ApiMaster::getRequestEndpoint).collect(Collectors.toSet());

        assertEquals(Set.of("/dockLocations", "/transportEquipment", "/yardLocations"), endpoints);
    }

    @Test
    @SqlMergeMode(MergeMode.MERGE)
    @Sql("registry/preexisting-registries.sql")
    void updatingImport() {
        ApiRegistry registry =
                registryRepo.findByApiTypeAndNameAndRoleMaster_level("INTERNAL", "yard", 0).orElseThrow();

        ApiMaster api = new ApiMaster();
        api.setName("listYardLocationsUsingGET");
        api.setVersion("1.0");
        api.setResponseSchema("{}");
        api.setApiRegistry(registry);
        api.setRequestEndpoint("/yardLocations");
        api.setRequestMethod("GET");

        api = apiRepo.save(api);

        assertThat(api.getRequestQuery(), is(blankOrNullString()));

        List<String> issues = invoke(importer, goodJson);

        apiRepo.findById(api.getUid());

        assertEquals(List.of(), issues);

        assertThat(api.getRequestQuery(), containsString("locationStatus"));
    }

    @Test
    @SqlMergeMode(MergeMode.MERGE)
    @Sql("registry/preexisting-registries.sql")
    void missingRegistry() {
        List<String> issues = invoke(importer, badJson);
        assertThat(issues, hasSize(3));

        assertThat(issues, hasItems(allOf(
                containsString("Cannot add api "),
                containsString("Owning registry ")
        )));
    }

    @Test
    void malformedInput() {
        List<String> issues = invoke(importer, "bad input".getBytes(), "bad.json");

        assertEquals(1, issues.size(), "There should have been an issue importing");
        assertEquals(List.of("Unable to parse file."), issues);
    }
}