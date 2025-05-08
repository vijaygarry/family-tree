package com.neasaa.base.app.operation.exception;

public class ValidationException extends OperationException {

	private static final long serialVersionUID = -2680704817641748939L;

	public ValidationException(String aMessage) {
		super(aMessage);
	}

	@Override
	public int getHttpResponseCode() {
		return BaseErrorCodes.HTTP_400;
	}
	

}
