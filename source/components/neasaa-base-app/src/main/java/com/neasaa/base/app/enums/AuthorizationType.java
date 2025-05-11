package com.neasaa.base.app.enums;

public enum AuthorizationType {
	/** 
	 * Check if login user has role with this operation.
	 */
	ROLE_BASE, 
	/**
	 * No authorization required.
	 */
	NO_AUTHORIZATION, 
	/**
	 * IP base authorization required. Operation allowed only from selected ip address 
	 */
	IP_BASE;
}
