package com.neasaa.base.app.service;

import java.util.Date;

import com.neasaa.base.app.entity.AppUser;
import com.neasaa.base.app.enums.UserStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticatedUser {
	private int userId;
	private String logonName;
	private String firstName;
	private String lastName;
	private String emailId;
	private String authenticationType;
	private Date lastLoginTime;
	private Date lastPasswordChangeTime;
	private UserStatusEnum status;
	
	
	
	public static AuthenticatedUser buildAuthenticatedUser (AppUser appUser) {
		if(appUser == null) {
			return null;
		}
		return new AuthenticatedUser(
				appUser.getUserId(), appUser.getLogonName(), 
				appUser.getFirstName(), appUser.getLastName(), 
				appUser.getEmailId(), appUser.getAuthenticationType(), 
				appUser.getLastLoginTime(), appUser.getLastPasswordChangeTime(), 
				UserStatusEnum.getUserStatusByCode(appUser.getStatus()));
	}
}
