package com.neasaa.base.app.operation.exception;

/**
 * This exception is thrown in following scenario 
 * - User is authenticated, but not allowed to perform specific operation - 403 
 */
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
