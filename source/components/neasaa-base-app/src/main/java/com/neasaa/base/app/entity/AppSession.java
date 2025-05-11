/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.base.app.entity;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AppSession extends BaseEntity {

	public static final long serialVersionUID = 1745893230022L;
	
	private long sessionId;
	private int userId;
	private String channelId;
	private String active;
	private Date sessionCreationTime;
	private Date logoutTime;
	private Date lastAccessTime;
	private short exitCode;
	private String appHostName;
	private String clientIpAddress;
	private String userAgent;


}
