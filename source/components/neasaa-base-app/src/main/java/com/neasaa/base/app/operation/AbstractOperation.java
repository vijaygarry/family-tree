package com.neasaa.base.app.operation;

import com.neasaa.base.app.operation.dto.OperationRequest;
import com.neasaa.base.app.operation.dto.OperationResponse;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class AbstractOperation<Request extends OperationRequest, Response extends OperationResponse> implements Operation<Request, Response> {
	
	@Getter
	private OperationContext context;
	
	
	@Override
	public Response execute(Request opRequest, OperationContext context) throws OperationException {
		this.context = context;
		if(context == null) {
			log.info("Operation context is not set, set context on operation before calling execute method.");
			throw new ValidationException ("Operation context is not set");
		}
		validateRequest(opRequest);
		return doExecute(opRequest);
	}
	
	public abstract void validateRequest(Request opRequest) throws OperationException;
	public abstract Response doExecute(Request opRequest) throws OperationException;
}
