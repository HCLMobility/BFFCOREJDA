/**
 * 
 */
package com.jda.mobility.framework.extensions.common;

import java.io.Serializable;
import java.util.Objects;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

/**
 * The class SessionDetails.java
 * @author HCL Technologies Ltd.
 */
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS) 
public class SessionDetails implements Serializable{
	
	/** The field serialVersionUID of type long */
	private static final long serialVersionUID = -5154951408556077985L;

	/** The field sessionId of type String */
	private String sessionId;

	/** The field locale of type String */
	private String locale;

	/** The field tenant of type String */
	private String tenant;
	
	/** The field version of type String */
	private String version;
	
	/** The field channel of type String */
	private String channel;
	
	/** The field principalName of type String */
	private String principalName;
	
	/** The field prdAuthCookie of type String */
	private String prdAuthCookie;
	
	private String deviceName;

	/**
	 * @return the locale of type String
	 */
	public String getLocale() {
		return locale;
	}

	/**
	 * @return the sessionId of type String
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * @param sessionId of type String
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	/**
	 * @param locale of type String
	 */
	public void setLocale(String locale) {
		this.locale = locale;
	}

	/**
	 * @return the tenant of type String
	 */
	public String getTenant() {
		return tenant;
	}

	/**
	 * @param tenant of type String
	 */
	public void setTenant(String tenant) {
		this.tenant = tenant;
	}

	/**
	 * @return the version of type String
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version of type String
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the channel of type String
	 */
	public String getChannel() {
		return channel;
	}

	/**
	 * @param channel of type String
	 */
	public void setChannel(String channel) {
		this.channel = channel;
	}

	/**
	 * @return the principalName of type String
	 */
	public String getPrincipalName() {
		return principalName;
	}

	/**
	 * @param principalName of type String
	 */
	public void setPrincipalName(String principalName) {
		this.principalName = principalName;
	}

	public String getPrdAuthCookie() {
		return prdAuthCookie;
	}

	public void setPrdAuthCookie(String prdAuthCookie) {
		this.prdAuthCookie = prdAuthCookie;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	@Override
	public int hashCode() {
		return Objects.hash(channel, locale, prdAuthCookie, principalName, sessionId, tenant, version,deviceName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SessionDetails other = (SessionDetails) obj;
		return Objects.equals(channel, other.channel) && Objects.equals(locale, other.locale)
				&& Objects.equals(prdAuthCookie, other.prdAuthCookie)
				&& Objects.equals(principalName, other.principalName) && Objects.equals(sessionId, other.sessionId)
				&& Objects.equals(tenant, other.tenant) && Objects.equals(version, other.version)
				&& Objects.equals(deviceName, other.deviceName);
	}
	
}