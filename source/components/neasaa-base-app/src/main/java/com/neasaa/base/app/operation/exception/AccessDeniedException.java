package com.neasaa.base.app.operation.exception;

public class AccessDeniedException extends OperationException {

	private static final long serialVersionUID = 3677006020821461115L;

	public AccessDeniedException(String aMessage) {
		super(aMessage);
	}

	@Override
	public int getHttpResponseCode() {
		return BaseErrorCodes.ACCESS_DENIED;
	}

}
