package com.jda.mobility.framework.extensions.service.impl.migration;

import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;

/**
 * Uniquely identifies an API without using a {@code UUID}.
 * <p>
 * API {@code UUID}s are auto-generated and are not consistent across different
 * systems. We need to look at these properties when looking for matching
 * APIs
 */
public class ApiKey {

    public final String registryName;
    public final String endpoint;
    public final String method;
    public final String type;

    public ApiKey(JsonNode invocation) {
        registryName = invocation.path("regName").asText();
        endpoint = invocation.path("requestEndpoint").asText();
        method = invocation.path("requestMethod").asText();
        type = invocation.path("apiType").asText();
    }

    public ApiKey(String registryName, String endpoint, String method, String type) {
        this.registryName = registryName;
        this.endpoint = endpoint;
        this.method = method;
        this.type = type;
    }

    /**
     * For an {@link ApiKey} to be considered valid, all its properties must be
     * non-blank values.
     */
    public boolean isValid() {
        return StringUtils.isNotBlank(registryName)
                && StringUtils.isNotBlank(endpoint)
                && StringUtils.isNotBlank(method)
                && StringUtils.isNotBlank(type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ApiKey apiMeta = (ApiKey) o;
        return Objects.equals(registryName, apiMeta.registryName) && Objects.equals(endpoint,
                apiMeta.endpoint) && Objects.equals(method, apiMeta.method) && Objects.equals(type, apiMeta.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registryName, endpoint, method, type);
    }
}
