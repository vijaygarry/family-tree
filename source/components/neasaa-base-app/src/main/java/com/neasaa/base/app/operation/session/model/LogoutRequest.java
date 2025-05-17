package com.neasaa.base.app.operation.session.model;

import com.neasaa.base.app.enums.SessionExitCode;
import com.neasaa.base.app.operation.model.OperationRequest;

import lombok.Getter;
import lombok.Setter;

public class LogoutRequest extends OperationRequest {
	
	private static final long serialVersionUID = 195876741083906785L;
	
	@Setter
	@Getter
	private SessionExitCode sessionExitCode;

	@Override
	public String getAuditString() {
		return null;
	}
}
