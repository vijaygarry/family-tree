package com.neasaa.base.app.operation.session;

import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.OperationNames;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.model.EmptyOperationRequest;
import com.neasaa.base.app.operation.model.EmptyOperationResponse;

public class LogoutOperation extends AbstractOperation<EmptyOperationRequest, EmptyOperationResponse>{

	@Override
	public EmptyOperationResponse doExecute(EmptyOperationRequest opRequest) throws OperationException {
		return null;
	}

	@Override
	public String getOperationName() {
		return OperationNames.LOGOUT;
	}

	@Override
	public void doValidate(EmptyOperationRequest opRequest) throws OperationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postExecute() {
		// TODO Auto-generated method stub
		
	}

}
