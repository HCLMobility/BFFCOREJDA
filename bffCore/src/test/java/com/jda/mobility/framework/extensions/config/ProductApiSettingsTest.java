package com.jda.mobility.framework.extensions.config;


import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.jda.mobility.framework.extensions.common.SessionDetails;
import com.jda.mobility.framework.extensions.entity.ProductMaster;
import com.jda.mobility.framework.extensions.repository.ProductMasterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.mockito.Mock;
import org.springframework.beans.BeanInstantiationException;

import static com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.WMS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class ProductApiSettingsTest {

    @Mock
    private ProductMasterRepository repo;

    @Mock
    private SessionDetails session;

    private ProductApiSettings apis;

    @BeforeEach
    void setup() {
        initMocks(this);
        apis = new ProductApiSettings(repo, session);
    }

    @TestFactory
    Stream<DynamicTest> misconfiguredBaseUrls() {
        return List.of(
                "", "localhost", "httpp://localhost", "htp:/localhost", "http", "http//foo" )
                .stream()
                .map(url -> dynamicTest("misconfigured -> " + url, () -> {
                    apis.setBaseUrl(url);
                    assertThrows(IllegalArgumentException.class, () -> apis.baseUrl("WMS"));
                }));
    }

    @TestFactory
    Stream<DynamicTest> multipleProducts() {
        return List.of("WM", "TM", "LM", "SCPO")
                .stream()
                .peek(product -> when(repo.findByName(product)).thenAnswer(invocation -> {
                    ProductMaster pm = new ProductMaster();
                    pm.setScheme("https");
                    pm.setPort(null);
                    pm.setContextPath("localhost/" + product);
                    return pm;
                }))
                .map(product -> dynamicTest(product,
                        () -> assertEquals("https://localhost/" + product, apis.baseUrl(product).toUriString())));
    }

    @Test
    void baseUrlDefaultsToWms() {
        when(session.getTenant()).thenThrow(
                new BeanInstantiationException(SessionDetails.class, "request scoped stuff"));

        when(repo.findByName(WMS)).thenAnswer(invocation -> {
            ProductMaster pm = new ProductMaster();
            pm.setScheme("http");
            pm.setPort("4500");
            pm.setContextPath("127.0.0.1");
            return pm;
        });

        assertEquals("http://127.0.0.1:4500", apis.baseUrl().toUriString());
    }

    @Test
    void baseUrlWithBlankProduct() {
        String baseUrl = "https://example.com";
        apis.setBaseUrl(baseUrl);
        assertEquals(baseUrl, apis.baseUrl("").toUriString());
        assertEquals(baseUrl, apis.baseUrl(null).toUriString());
    }

    @Test
    void unsetApisUsingDefaultProduct() {
        String baseUrl = "http://default";
        apis.setBaseUrl(baseUrl);
        assertEquals(baseUrl, apis.warehousesUrl().toUriString());
        assertEquals(baseUrl, apis.rolesUrl().toUriString());
        assertEquals(baseUrl, apis.permissionsUrl().toUriString());
        assertEquals(baseUrl, apis.currentUserPermissionsUrl().toUriString());
        assertEquals(baseUrl, apis.currentUserRolesUrl().toUriString());
        assertEquals(baseUrl, apis.validateUsersUrl().toUriString());
    }

    @Test
    void unsetApisUsingSpecificProduct() {
        String product = "TEST";
        when(repo.findByName(product)).thenAnswer(invocation -> {
            ProductMaster pm = new ProductMaster();
            pm.setScheme("https");
            pm.setPort(null);
            pm.setContextPath("jdawaapi.jdadelivers.com");
            return pm;
        });
        when(session.getTenant()).thenReturn(product);

        String expected = "https://jdawaapi.jdadelivers.com";
        assertEquals(expected, apis.warehousesUrl(product).toUriString());
        assertEquals(expected, apis.rolesUrl(product).toUriString());
        assertEquals(expected, apis.permissionsUrl(product).toUriString());
        assertEquals(expected, apis.currentUserPermissionsUrl().toUriString());
        assertEquals(expected, apis.currentUserRolesUrl(product).toUriString());
        assertEquals(expected, apis.validateUsersUrl(product).toUriString());
    }

    @Test
    void apisWithPaths() {
        apis.setBaseUrl("https://by.azure.net");
        apis.setCurrentUserPermissions("/cup/{u}");
        apis.setCurrentUserRoles("/cur/{0}?arg={1}");
        apis.setRoles("/roles");
        apis.setValidateUsers("/validate");
        apis.setWarehouses("/warehouses");
        apis.setPermissions("/permissions");
        apis.setLogin("/login");

        assertEquals("https://by.azure.net/login", apis.loginUrl(WMS).toUriString());
        assertEquals("https://by.azure.net/warehouses", apis.warehousesUrl(WMS).toUriString());
        assertEquals("https://by.azure.net/roles", apis.rolesUrl(WMS).toUriString());
        assertEquals("https://by.azure.net/permissions", apis.permissionsUrl(WMS).toUriString());
        assertEquals("https://by.azure.net/cup/me", apis.currentUserPermissionsUrl(WMS)
                .buildAndExpand(Map.of("u", "me")).toUriString());
        assertEquals("https://by.azure.net/cur/me?arg=you", apis.currentUserRolesUrl(WMS)
                .buildAndExpand("me", "you").toUriString());
        assertEquals("https://by.azure.net/validate", apis.validateUsersUrl(WMS).toUriString());
    }


}