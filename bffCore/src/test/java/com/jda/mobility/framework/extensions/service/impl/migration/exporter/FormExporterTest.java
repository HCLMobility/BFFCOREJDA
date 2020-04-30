package com.jda.mobility.framework.extensions.service.impl.migration.exporter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.entity.Field;
import com.jda.mobility.framework.extensions.entity.Form;
import com.jda.mobility.framework.extensions.service.impl.migration.ApiKey;
import com.jda.mobility.framework.extensions.service.impl.migration.References;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FormExporterTest {

    @Test
    void collectsReferences() {
        FormExporter exporter = new FormExporter(new ObjectMapper());

        UUID formId = UUID.randomUUID();
        Form form = form(formId, "myForm");
        form.setPublished(true);
        form.setPublishedForm(readPublishedJson());

        References references = new References();

        exporter.exportToJson(form, references);

        assertThat(references.globalVars, contains("MyGlob", "DeviceId"));
        assertThat(references.contextVals, contains("ContextVar"));
        assertThat(references.resourceBundleKeys, contains(
                "", "cc1", "logout", "cc2", "test", "home-context-1",
                "form-title", "toast1", "cool", "toast2", "two"
        ));
        assertThat(references.apis,
                contains(new ApiKey("swag", "/transportEquipment", "GET", "INTERNAL")));
        assertThat(references.customComponentIds, contains("", "dc841587-75c3-4193-9b12-efbe8424c117"));
        assertThat(references.menuIds, contains(
                "0555e077-6836-4246-8861-788c1386726d",
                "42f595da-d46d-4921-b02a-478f91320d9c"
        ));
    }

    @Test
    void formMustBePublished() {
        FormExporter exporter = new FormExporter(new ObjectMapper());

        UUID formId = UUID.randomUUID();
        Form form = form(formId, "unpublished");

        References references = new References();

        Map<String, JsonNode> exports = exporter.exportToJson(form, references);

        assertThat(exports, anEmptyMap());
    }

    @Test
    void simpleForm() {
        UUID formId = UUID.randomUUID();
        Form form = form(formId, "simple-form");
        form.setPublished(true);
        form.setPublishedForm("{}".getBytes());

        UUID field1Id = UUID.randomUUID();
        Field field1 = field(field1Id, "field1");

        UUID field2Id = UUID.randomUUID();
        Field field2 = field(field2Id, "field2");

        form.addField(field1);
        field1.addChildFields(field2);

        FormExporter exporter = new FormExporter(new ObjectMapper());
        References references = new References();

        Map<String, JsonNode> exports = exporter.exportToJson(form, references);

        JsonNode jsonNode = exports.get("simple-form.form.json");

        assertTrue(jsonNode.isObject(), "form should be serialized in an object node");
        assertEquals(formId.toString(), jsonNode.path("uid").asText());
        assertEquals("simple-form", jsonNode.path("name").asText());
        assertEquals("field1", jsonNode.path("fields").path(0).path("keys").asText());
        assertEquals(field1Id.toString(), jsonNode.path("fields").path(0).path("uid").asText());
        assertEquals("field2", jsonNode.path("fields").path(0).path("childFields").path(0).path("keys").asText());
        assertEquals(field2Id.toString(), jsonNode.path("fields").path(0).path("childFields").path(0).path("uid").asText());

    }

    private Form form(UUID uid, String name) {
        Form form = new Form();
        form.setUid(uid);
        form.setName(name);
        return form;
    }

    private Field field(UUID uid, String name) {
        Field field = new Field();
        field.setUid(uid);
        field.setKeys(name);
        return field;
    }

    private byte[] readPublishedJson() {
        try {
            return new ClassPathResource("published-form.json").getInputStream().readAllBytes();
        }
        catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}