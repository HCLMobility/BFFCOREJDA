package com.jda.mobility.framework.extensions.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The class for the AppProperties
 * HCL Technologies Ltd.
 */
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String scheme;
    private final Auth auth = new Auth();

    public static class Auth {
        private String clientId;
        private String tokenSecret;
        private long tokenExpirationMsec;

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getTokenSecret() {
            return tokenSecret;
        }

        public void setTokenSecret(String tokenSecret) {
            this.tokenSecret = tokenSecret;
        }

        public long getTokenExpirationMsec() {
            return tokenExpirationMsec;
        }

        public void setTokenExpirationMsec(long tokenExpirationMsec) {
            this.tokenExpirationMsec = tokenExpirationMsec;
        }
    }

    public Auth getAuth() {
        return auth;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public boolean isOidcEnabled() {
        return OPENID.equalsIgnoreCase(scheme);
    }

    public boolean isBasicAuthEnabled() {
        return !isOidcEnabled();
    }

    private static final String OPENID = "OPENID";

}