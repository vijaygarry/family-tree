package com.neasaa.base.app.operation.session;

import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.OperationNames;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.model.EmptyOperationResponse;
import com.neasaa.base.app.operation.session.model.LoginRequest;

public class LoginOperation extends AbstractOperation<LoginRequest, EmptyOperationResponse>{

	@Override
	public String getOperationName() {
		return OperationNames.CREATE_SESSION;
	}
	
	@Override
	public void doValidate(LoginRequest opRequest) throws OperationException {
		
	}
	
	@Override
	public EmptyOperationResponse doExecute(LoginRequest opRequest) throws OperationException {
		return new EmptyOperationResponse("Login successfully");
	}

	@Override
	public void postExecute() {
		
	}

	

	

}
