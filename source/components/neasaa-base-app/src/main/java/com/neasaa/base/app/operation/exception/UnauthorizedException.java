package com.neasaa.base.app.operation.exception;

/**
 * This exception is thrown in following scenario 
 * - Session expired - 401 
 * - No session (unauthenticated) - 401 
 * - Invalid token/cookie - 401
 */
public class UnauthorizedException extends OperationException {
	
	private static final long serialVersionUID = 3129969428264319416L;

	public UnauthorizedException(String aMessage) {
		super(aMessage);
	}

	@Override
	public int getHttpResponseCode() {
		return BaseErrorCodes.UNAUTHORIZED_ACCESS;
	}
}
