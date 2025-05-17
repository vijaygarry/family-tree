package com.neasaa.base.app.operation.session.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.neasaa.base.app.operation.OperationContext.ClientInformation;
import com.neasaa.base.app.operation.model.OperationRequest;
import com.neasaa.base.app.utils.ValidationUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest extends OperationRequest {

	private static final long serialVersionUID = -2726566735228218285L;
	
	private String loginName;
	@JsonIgnore
	private String password;
	private ClientInformation clientInformation;
	
	@Override
	public String getAuditString() {
		//This should be implemented in operation executor.
		ValidationUtils.addToDoLog("GetAuditString not yet implemented.", "LoginRequest");
		return "loginName:" + loginName;
	}

}
