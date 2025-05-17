package com.neasaa.base.app.operation;

import com.neasaa.base.app.operation.exception.InternalServerException;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.model.OperationRequest;
import com.neasaa.base.app.operation.model.OperationResponse;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class OperationExecutor {

	
	
	public static <Request extends OperationRequest, Response extends OperationResponse, OP extends Operation<Request, Response>> Response executeOperation (
			Class<OP> aClazz, 
			Request request, 
			OperationContext operationContext
	) throws OperationException {
		
		
//		AppSession appSession = operationContext.getAppSession();
//		String clientUserIpAddr = operationContext.getClientUserIpAddr();
		
		
		//TODO: Set logging parameters in Log Thread Context
		Operation<Request, Response> operation = null;
		Response operationResponse = null;
		
		try {
			log.debug("Creating operation for class {} ", aClazz.getName());
			operation = OperationFactory.getOperationByType (aClazz);
			log.info( "Running operation <{}> ", operation.getOperationName());
			operationResponse =  operation.execute(request, operationContext);
			log.info( "operation <{}> completed successfully.", operation);
			log.debug( "Response from operation {} -> {} ", operation ,operationResponse);
		} catch ( OperationException oe ) {
			if(log.isDebugEnabled()) {
				log.debug( "Operation <" + operation + "> failed to execute with error message <" + oe.getMessage() + ">", oe);
			}
			throw oe;
		}
		catch ( Throwable th ) {
			log.info ( "Error running the operation <" + operation + ">", th );
			throw new InternalServerException("Error running the operation <" + operation + ">", th);
		}
		
		return operationResponse;
		
		// TODO: Create class to capture and log user agent details
	}
}
