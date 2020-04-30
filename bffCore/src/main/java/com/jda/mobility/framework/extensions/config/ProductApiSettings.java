package com.jda.mobility.framework.extensions.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jda.mobility.framework.extensions.common.SessionDetails;
import com.jda.mobility.framework.extensions.entity.ProductMaster;
import com.jda.mobility.framework.extensions.repository.ProductMasterRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.util.UriComponentsBuilder;

import static com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.WMS;

@Getter
@Setter
@ConfigurationProperties(prefix = "product-apis")
public class ProductApiSettings {

    @Getter(AccessLevel.NONE)
    private final ProductMasterRepository productRepo;
    @Getter(AccessLevel.NONE)
    private final Map<String, String> baseUrls = new ConcurrentHashMap<>(1);
    @Getter(AccessLevel.NONE)
    private final SessionDetails sessionDetails;

    private String baseUrl;
    private String login;
    private String roles;
    private String permissions;
    private String warehouses;
    private String currentUserRoles;
    private String currentUserPermissions;
    private String validateUsers;

    public ProductApiSettings(ProductMasterRepository productRepo,
                              SessionDetails sessionDetails) {
        this.productRepo = productRepo;
        this.sessionDetails = sessionDetails;
    }

    public UriComponentsBuilder baseUrl(String product) {
        return UriComponentsBuilder.fromHttpUrl(cachedBaseUrl(product));
    }

    public UriComponentsBuilder baseUrl() {
        return baseUrl(getProductName());
    }

    public UriComponentsBuilder loginUrl(String product) {
        return baseUrl(product).path(getLogin());
    }

    public UriComponentsBuilder rolesUrl(String product) {
        return baseUrl(product).path(getRoles());
    }

    public UriComponentsBuilder rolesUrl() {
        return rolesUrl(getProductName());
    }

    public UriComponentsBuilder permissionsUrl(String product) {
        return baseUrl(product).path(getPermissions());
    }

    public UriComponentsBuilder permissionsUrl() {
        return permissionsUrl(getProductName());
    }

    public UriComponentsBuilder warehousesUrl(String product) {
        return baseUrl(product).path(getWarehouses());
    }

    public UriComponentsBuilder warehousesUrl() {
        return warehousesUrl(getProductName());
    }

    public UriComponentsBuilder currentUserRolesUrl(String product) {
        return baseUrl(product).path(getCurrentUserRoles());
    }

    public UriComponentsBuilder currentUserRolesUrl() {
        return currentUserRolesUrl(getProductName());
    }

    public UriComponentsBuilder currentUserPermissionsUrl(String product) {
        return baseUrl(product).path(getCurrentUserPermissions());
    }

    public UriComponentsBuilder currentUserPermissionsUrl() {
        return currentUserPermissionsUrl(getProductName());
    }

    public UriComponentsBuilder validateUsersUrl(String product) {
        return baseUrl(product).path(getValidateUsers());
    }

    public UriComponentsBuilder validateUsersUrl() {
        return validateUsersUrl(getProductName());
    }

    private String getProductName() {
        try {
            return sessionDetails.getTenant();
        }
        catch (BeanInstantiationException e) {
            // The api was requested outside the scope of a session.
            // Since sessionDetails is a request scope bean, an exception
            // was thrown. We'll return the default product name.
            return WMS;
        }
    }

    private String cachedBaseUrl(String productName) {
        if (WMS.equals(productName) ||  StringUtils.isBlank(productName)) {
            if (StringUtils.isNotBlank(baseUrl)) {
                return baseUrl;
            }
        }

        return baseUrls.computeIfAbsent(StringUtils.defaultString(productName, WMS), (name) -> {
            ProductMaster product = productRepo.findByName(name);
            if (product == null) {
                return "";
            }
            return UriComponentsBuilder.newInstance().scheme(product.getScheme())
                    .host(product.getContextPath())
                    .port(product.getPort())
                    .build()
                    .toUriString();
        });
    }
}
