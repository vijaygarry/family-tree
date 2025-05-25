/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.base.app.entity;

import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppSessionTxn extends BaseEntity {

	public static final long serialVersionUID = 1745893230036L;
	
	private long txnId;
	private long sessionId;
	private String operationId;
	private int userId;
	private Date txnStartTime;
	private long txnLatencyMillis;
	private int httpResponseCode;
	private String request;
	private String response;


}
