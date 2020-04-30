/**
 * 
 */
package com.jda.mobility.framework.extensions.common;

import java.util.Calendar;
import java.util.TimeZone;

import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.jda.mobility.framework.extensions.entity.RevisionInfo;

/**
 * @author HCL Technologies Ltd.
 *
 */
public class RevisionInfoListener implements RevisionListener {

	@Override
	public void newRevision(Object revInfoEntity) { 
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		RevisionInfo revInfo = (RevisionInfo) revInfoEntity;
		revInfo.setUserName(authentication.getName());
		revInfo.setDate(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime());
	}
}
