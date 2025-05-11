package com.neasaa.base.app.enums;

import lombok.Getter;

public enum UserStatusEnum {
	ACTIVE("A"),
	INACTIVE("I"),
	LOCKED ("L");
	
	@Getter
	private String statusCode;
	
	private UserStatusEnum (String statusCode) {
		this.statusCode = statusCode;
	}
	
	public static UserStatusEnum getUserStatusByCode (String statusCode) {
		for(UserStatusEnum status : UserStatusEnum.values()) {
			if(status.name().equalsIgnoreCase(statusCode)) {
				return status;
			}
		}
		return null;
	}
	
}
