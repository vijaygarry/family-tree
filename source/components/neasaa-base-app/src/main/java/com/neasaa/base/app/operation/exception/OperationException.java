package com.neasaa.base.app.operation.exception;

public abstract class OperationException extends RuntimeException {

	private static final long serialVersionUID = 6125407835719603881L;
	
	public OperationException ( String aMessage ) {
		this(aMessage, null );
	}

	public OperationException (String aMessage, Throwable aCause ) {
		super( aMessage, aCause );
	}
	
	public abstract int getHttpResponseCode();
}
