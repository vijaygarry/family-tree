package com.neasaa.base.app.operation.session.model;

import com.neasaa.base.app.operation.model.OperationRequest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest extends OperationRequest {

	private static final long serialVersionUID = -2726566735228218285L;
	
	private String loginName;
	//@JsonIgnore
	private String password;
	private String channel;
	private String clientIpAddress;
	private String clientOsName;
	private String clientBrowserName;
	
	@Override
	public String getAuditString() {
		//TODO: Add logic to convert whole object to json object
		return "loginName:" + loginName;
	}

}
