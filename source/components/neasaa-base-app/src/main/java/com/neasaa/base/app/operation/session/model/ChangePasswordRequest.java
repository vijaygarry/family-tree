package com.neasaa.base.app.operation.session.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neasaa.base.app.operation.model.OperationRequest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest extends OperationRequest {

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String currentPassword;
	
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String newPassword;


}
