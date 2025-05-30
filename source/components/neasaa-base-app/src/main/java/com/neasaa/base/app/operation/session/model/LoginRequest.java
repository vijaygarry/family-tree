package com.neasaa.base.app.operation.session.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neasaa.base.app.operation.OperationContext.ClientInformation;
import com.neasaa.base.app.operation.model.OperationRequest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest extends OperationRequest {

	private static final long serialVersionUID = -2726566735228218285L;
	
	private String loginName;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;
	private ClientInformation clientInformation;
	

}
