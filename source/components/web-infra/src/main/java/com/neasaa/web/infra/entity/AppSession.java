/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.web.infra.entity;

import lombok.Setter;
import lombok.Getter;
import java.util.Date;

@Getter
@Setter
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
