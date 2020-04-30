package com.jda.mobility.framework.extensions.security;

import com.google.common.collect.ImmutableSet;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class MethodAuthorizationMapping {

    private final Set<String> methods;
    private final Set<String> authorizations;

    public MethodAuthorizationMapping(Set<String> methods, Set<String> authorizations) {
        this.authorizations = authorizations;
        this.methods = methods.stream().map(String::toUpperCase).collect(Collectors.toSet());
    }

    public Set<String> methods() {
        return ImmutableSet.copyOf(methods);
    }

    public Set<String> authorizations() {
        return ImmutableSet.copyOf(authorizations);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodAuthorizationMapping that = (MethodAuthorizationMapping) o;
        return methods.equals(that.methods) &&
                authorizations.equals(that.authorizations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(methods, authorizations);
    }

    public enum Method {
        ALL("ALL"),
        GET("GET"),
        PUT("PUT"),
        POST("POST"),
        DELETE("DELETE"),
        HEAD("HEAD"),
        PATCH("PATCH"),
        OPTIONS("OPTIONS"),
        TRACE("TRACE");
    	
    	private String methodName;
        Method(String methodName) {
            this.methodName = methodName;
        }

        @Override
        public String toString() {
            return methodName;
        }

        
    }
}
