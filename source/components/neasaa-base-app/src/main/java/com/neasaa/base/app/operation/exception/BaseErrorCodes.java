package com.neasaa.base.app.operation.exception;

public interface BaseErrorCodes {
	//TODO Decide what error codes we need to define
	int SUCCESS_CODE = 0;
	int INITIALIZATION_ERROR_CODE = 1;
	int USER_UNAUTHORIZED = 401;
	int VALIDATION_ERROR_CODE =  400;
	int OPERATION_NOT_ALLOWED = 403;
	int OPERATION_NOT_FOUND = 404;
	int INTERNAL_ERROR_CODE = 500;
	
	// HTTP 400 = Bad Request, Input validation
	int HTTP_400 = 400;
		
}
