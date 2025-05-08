package com.neasaa.base.app.operation.dto;

public class EmptyOperationResponse extends OperationResponse {

	private static final long serialVersionUID = -2259580190205125416L;

	public EmptyOperationResponse(int responseCode) {
		super(responseCode);
	}

	@Override
	public String getAuditString() {
		return null;
	}

}
