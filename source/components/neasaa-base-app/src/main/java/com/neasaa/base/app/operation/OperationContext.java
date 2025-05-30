package com.neasaa.base.app.operation;

import java.util.Date;

import com.neasaa.base.app.enums.ChannelEnum;
import com.neasaa.base.app.service.AppSessionUser;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OperationContext {
	
	//We will be using this user id if user is not logged in to application.
	private final int NOT_LOGGED_IN_USER_ID = -1;
	// 
	private final AppSessionUser appSessionUser;
	private final Date startTime;
	/**
	 * Processing end time for this request
	 */
	private Date endTime;
	
	private String appHostName;
	private AuditInfo auditInfo;
	
	public OperationContext (AppSessionUser appSessionUser , String appHostName) {
		this.startTime = new Date();
		this.appSessionUser = appSessionUser;
		this.appHostName = appHostName;
		
		this.auditInfo = AuditInfo.builder()
				.createdBy(appSessionUser == null ? NOT_LOGGED_IN_USER_ID : appSessionUser.getUserId())
				.createdDate(startTime)
				.lastUpdatedBy(appSessionUser == null ? NOT_LOGGED_IN_USER_ID : appSessionUser.getUserId())
				.lastUpdatedDate(startTime)
				.build();
	}
	
	public long getResponseTime () {
		if(this.startTime != null && this.endTime != null) {
			return (this.endTime.getTime() - this.startTime.getTime());
		}
		System.out.println( "Start time:" + this.startTime + ", End Time:" + this.endTime + ". Returning -1 as response time." );
		return -1;
	}

	public void markComplete() {
		this.endTime = new Date();
	}
	
	@Builder
	@Getter
	public static class ClientInformation {
		private final String clientUserIpAddr;
		private final String userAgentString;
		private final ChannelEnum channel; //Derived from user agent
		private final String browserName; // e.g. Chrome, Firefox, Safari, Edge, Opera
		private final String browserVersion; //e.g. 117.0.5938.92, 118.0
		private final String operatingSystem; //e.g. Windows NT 10.0, Mac OS X 10_15_7, Android 13, iOS 16
		private final String deviceType;// e.g. Sometimes included: Mobile, Tablet, Desktop
	}
}
