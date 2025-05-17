package com.neasaa.base.app.operation.session.model;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSessionDetails {
	private long userId;
	private String logonName;
	private String firstName;
	private String lastName;
	private String emailId;
	private long sessionId;
	private boolean sessionActive;
	private boolean authenticated;
	private Date sessionCreationTime;
	private Date lastAccessTime;
	
	public static UserSessionDetails getUserSessionDetails (LoginResponse loginResponse) {
		return UserSessionDetails.builder()
				.userId(loginResponse.getUserId())
				.logonName(loginResponse.getLogonName())
				.firstName(loginResponse.getFirstName())
				.lastName(loginResponse.getLastName())
				.emailId(loginResponse.getEmailId())
				.sessionId(loginResponse.getSessionId())
				.sessionActive(loginResponse.isSessionActive())
				.authenticated(loginResponse.isAuthenticated())
				.sessionCreationTime(loginResponse.getSessionCreationTime())
				.lastAccessTime(loginResponse.getLastAccessTime())
				.build();
	}
	
	public void invalidate() {
		this.authenticated = false;
	}
}
