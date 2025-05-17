package com.neasaa.base.app.utils;

import com.neasaa.base.app.operation.exception.InternalServerException;

public class ExceptionUtils {

	public static final InternalServerException getInternalException (String message) {
		return new InternalServerException(message);
	}
	
	public static final InternalServerException getInternalException (String message, Throwable exception) {
		return new InternalServerException(message, exception);
	}
}
