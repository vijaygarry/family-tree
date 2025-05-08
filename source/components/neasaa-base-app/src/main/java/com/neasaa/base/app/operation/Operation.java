package com.neasaa.base.app.operation;

import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.model.OperationRequest;
import com.neasaa.base.app.operation.model.OperationResponse;

public interface Operation <Request extends OperationRequest, Response extends OperationResponse> {
	String getOperationName();
	Response execute (Request aRequest, OperationContext context) throws OperationException;

}
