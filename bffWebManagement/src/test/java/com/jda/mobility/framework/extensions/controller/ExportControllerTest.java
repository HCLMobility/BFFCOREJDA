package com.jda.mobility.framework.extensions.controller;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.jda.mobility.framework.extensions.security.UserPrincipal;
import com.jda.mobility.framework.extensions.service.impl.migration.exporter.ExportCoordinator;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ExportType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletResponse;

import static com.jda.mobility.framework.extensions.util.UnzipHelper.unzip;
import static com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ExportType.ALL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


class ExportControllerTest {

    @Mock
    private ExportCoordinator exportCoordinator;

    @Mock
    private UserPrincipal userPrincipal;

    @BeforeEach
    void beforeEach() {
        initMocks(this);
        when(userPrincipal.getUserId()).thenReturn("super");
    }

    @Test
    void exportWithException() {
        ExportController controller = new ExportController(exportCoordinator);
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(exportCoordinator.exportData(any(), any(), any()))
                .thenThrow(new IllegalArgumentException("bad stuff"));
        assertThrows(IllegalArgumentException.class, () ->
                controller.exportData(new ExportType[] { ALL }, UUID.randomUUID(), userPrincipal, response));
    }

    @Test
    void exportSuccess() throws IOException {
        ExportController controller = new ExportController(exportCoordinator);
        MockHttpServletResponse response = new MockHttpServletResponse();

        Map<String, JsonNode> exportedData =
                Map.of("name.json", JsonNodeFactory.instance.textNode("value"));

        when(exportCoordinator.exportData(any(), any(), any())).thenReturn(exportedData);

        controller.exportData(new ExportType[] { ALL }, UUID.randomUUID(), userPrincipal, response);

        byte[] zippedOutput = response.getContentAsByteArray();

        Map<String, JsonNode> nodes = unzip(zippedOutput);

        assertEquals(exportedData, nodes);
    }

}
