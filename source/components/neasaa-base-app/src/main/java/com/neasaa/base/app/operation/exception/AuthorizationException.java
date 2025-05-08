package com.neasaa.base.app.operation.exception;

public class AuthorizationException extends OperationException {

	private static final long serialVersionUID = 7745983597759726251L;

	public AuthorizationException(String aMessage) {
		super(aMessage);
	}

	@Override
	public int getHttpResponseCode() {
		return 0;
	}

}
