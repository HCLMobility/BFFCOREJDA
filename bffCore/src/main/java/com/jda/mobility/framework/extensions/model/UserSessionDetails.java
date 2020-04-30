/**
 * 
 */
package com.jda.mobility.framework.extensions.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;

/**
 * The class to carry user session details.
 *
 */
@Builder @Value
public class UserSessionDetails implements Serializable, Comparable<UserSessionDetails> {

	private static final long serialVersionUID = -8202297395380628802L;
	private String sessionId;
	private String userId;
	private String deviceId;
	private String warehouseId;
	private UUID flowId;
	private UUID formId;
	private UUID productConfigId;
	private String locale;
	private String version;
	private String refererUrl;
	private UUID menuId;
	private String menuType;
	private boolean tabbedForm;
	private boolean modalForm;
	private Instant lastAccessTime;
	
	@Override
	public int compareTo(UserSessionDetails o) {
		return this.getLastAccessTime().compareTo(o.getLastAccessTime());
	}

}
