package com.neasaa.base.app.service;

import java.util.Date;
import java.util.List;

import com.neasaa.base.app.entity.AppSession;
import com.neasaa.base.app.entity.AppUser;
import com.neasaa.base.app.enums.ChannelEnum;
import com.neasaa.base.app.enums.UserStatusEnum;

import lombok.Builder;
import lombok.Getter;

/**
 * Session user is created and stored in HTTP Session wrapper to share the session information during the session.
 * Login Operation returns new AppSessionUser which then used by session controller to put in HTTP Session object.
 */
@Getter
@Builder
public class AppSessionUser {
	
	private int userId;
	private String logonName;
	private String firstName;
	private String lastName;
	private String emailId;
	private long sessionId;
	private boolean sessionActive;
	private boolean authenticated;
	private String authenticationType;
	private Date sessionCreationTime;
	private Date lastPasswordChangeTime;
	private Date lastAccessTime;
	private UserStatusEnum status;
	private final ChannelEnum channel;
	private List<String> roleIds;
	
	public static AppSessionUser buildAuthenticatedUser (AppUser appUser, List<String> roleIdsForUser, AppSession appSession) {
		if(appUser == null) {
			return null;
		}
		return AppSessionUser.builder()
				.userId(appUser.getUserId())
				.logonName(appUser.getLogonName()) 
				.firstName(appUser.getFirstName())
				.lastName(appUser.getLastName())
				.emailId(appUser.getEmailId())
				.sessionId(appSession.getSessionId())
				.sessionActive(appSession.isActive())
				.channel(appSession.getChannel())
				.authenticated(true)
				.authenticationType(appUser.getAuthenticationType())
				.sessionCreationTime(appSession.getSessionCreationTime())
				.lastPasswordChangeTime(appUser.getLastPasswordChangeTime())
				.lastAccessTime(appUser.getLastLoginTime())
				.status(UserStatusEnum.getUserStatusByCode(appUser.getStatus()))
				.roleIds(roleIdsForUser)
				.build();
	}
	
	public void invalidate() {
		this.authenticated = false;
		this.sessionActive = false;
	}
}
