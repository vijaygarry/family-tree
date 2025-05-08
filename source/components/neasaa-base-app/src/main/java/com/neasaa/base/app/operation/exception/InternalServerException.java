package com.neasaa.base.app.operation.exception;

public class InternalServerException extends OperationException {

	private static final long serialVersionUID = 8875501915639256061L;

	public InternalServerException(String aMessage) {
		super(aMessage);
	}

	public InternalServerException(String aMessage, Throwable th) {
		super(aMessage, th);
	}

	@Override
	public int getHttpResponseCode() {
		return 0;
	}

}
