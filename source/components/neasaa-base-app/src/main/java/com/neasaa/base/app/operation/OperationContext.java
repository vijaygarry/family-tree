package com.neasaa.base.app.operation;

import java.util.Date;

import com.neasaa.base.app.entity.AppSession;

import lombok.Getter;

@Getter
public class OperationContext {

	private final AppSession appSession;
	private final Date startTime;
	/**
	 * Processing end time for this request
	 */
	private Date endTime;
	private final String clientUserIpAddr;
	
	public OperationContext (AppSession aAppSession , String aClientUserIpAddr) {
		this.startTime = new Date();
		this.appSession = aAppSession;
		this.clientUserIpAddr = aClientUserIpAddr;
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
}
