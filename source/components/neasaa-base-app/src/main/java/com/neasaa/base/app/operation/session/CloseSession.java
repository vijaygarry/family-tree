package com.neasaa.base.app.operation.session;

import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.OperationNames;
import com.neasaa.base.app.operation.dto.OperationRequest;
import com.neasaa.base.app.operation.dto.OperationResponse;
import com.neasaa.base.app.operation.exception.OperationException;

public class CloseSession extends AbstractOperation<OperationRequest, OperationResponse>{

	@Override
	public void validateRequest(OperationRequest opRequest) throws OperationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public OperationResponse doExecute(OperationRequest opRequest) throws OperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOperationName() {
		return OperationNames.CLOSE_SESSION;
	}

}
