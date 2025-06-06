/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.base.app.entity;

import java.util.Date;

import com.neasaa.base.app.enums.ChannelEnum;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AppSession extends BaseEntity {

	public static final long serialVersionUID = 1747019378044L;
	private long sessionId;
	private int userId;
	private ChannelEnum channel;
	private boolean active;
	private boolean authenticated;
	private Date sessionCreationTime;
	private Date logoutTime;
	private Date lastAccessTime;
	private short exitCode;
	private String appHostName;
	private String clientIpAddress;
	private String userAgent;


}
