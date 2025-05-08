package com.neasaa.base.app.operation;

import com.neasaa.base.app.operation.dto.OperationRequest;
import com.neasaa.base.app.operation.dto.OperationResponse;
import com.neasaa.base.app.operation.exception.OperationException;

public interface Operation <Request extends OperationRequest, Response extends OperationResponse> {
	String getOperationName();
	Response execute (Request aRequest, OperationContext context) throws OperationException;

}
