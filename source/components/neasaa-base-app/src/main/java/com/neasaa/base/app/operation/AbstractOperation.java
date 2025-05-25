package com.neasaa.base.app.operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.neasaa.base.app.entity.OperationEntity;
import com.neasaa.base.app.operation.exception.AccessDeniedException;
import com.neasaa.base.app.operation.exception.InternalServerException;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.base.app.operation.model.OperationRequest;
import com.neasaa.base.app.operation.model.OperationResponse;
import com.neasaa.base.app.service.AppSessionUser;
import com.neasaa.base.app.service.AuthorizationService;
import com.neasaa.base.app.service.SessionService;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class AbstractOperation<Request extends OperationRequest, Response extends OperationResponse> implements Operation<Request, Response> {
	
	@Autowired 
	@Qualifier(BeanNames.AUTHORIZATION_SERVICE_BEAN)
	private AuthorizationService authorizationService;
	
	@Autowired
	@Qualifier(BeanNames.SESSION_SERVICE_BEAN)
	private SessionService sessionService;

	
	@Getter
	private OperationContext context;
	
	@Override
	@Transactional(transactionManager = BeanNames.TRANSACTION_MANAGER, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Response execute(Request opRequest, OperationContext context) throws OperationException {
		this.context = context;
		if(context == null) {
			log.info("Operation context is not set, set context on operation before calling execute method.");
			throw new ValidationException ("Operation context is not set");
		}
		OperationEntity operationEntity = null;
	
		Request request = opRequest;
		Response response = null;
		OperationException operationException = null;
		AppSessionUser appSessionUser = this.context.getAppSessionUser();
		String operationName = this.getOperationName();
		
		try {
			
			operationEntity = getOperationEntityByName(operationName);
			
			// getOperationEntityByName make sure operationEntity is not null.
			if(operationEntity.getAuthorizationType() ==  null) {
				String msg = "Authtype is not define for Operation name " + operationName; 
				log.info(msg);
				throw new InternalServerException(msg);
			}
			
			if(!this.authorizationService.isOperationAllowedForUser( operationEntity, appSessionUser )) {
				throw new AccessDeniedException("Operation " + operationName + " not allowed. Please contact administrator.");
			}
			
			// Static validation for input fields. This should not depends on DB connection.
			doValidate(request);
			
			response = doExecute (request);
			return response;
		}
		catch ( OperationException e ) {
			log.debug( "Failed to execute operation with error " + e.getMessage());
			operationException = e;
			throw e;
		}
		catch ( Throwable th ) {
			log.info( "Internal unhandle exception in executing the operation. Error:" + th.getMessage(), th);
			operationException = new InternalServerException(th.getMessage(), th);
			throw operationException;
		} finally {
			try {
				postExecute();
			} catch (Throwable th) {
				log.error( "Internal unhandle exception in doing post process. Error:" + th.getMessage(), th);
			}
			context.markComplete();
			
			updateSessionLastAccessTime (appSessionUser);
			
			try {
				auditTransaction (operationEntity, request, response, operationException, appSessionUser, context);
			} catch (Throwable th) {
				log.error( "Internal unhandle exception while auditing. Error:" + th.getMessage(), th);
			}
		}
	}
	
	protected OperationEntity getOperationEntityByName (String aOperationName) throws OperationException {
		OperationEntity operationEntity = this.authorizationService.getOperationByName(aOperationName);
		if (operationEntity == null) {
			String msg = "Operation name " + aOperationName + " not configured in database."; 
			log.info(msg);
			throw new InternalServerException(msg);
		}
		return operationEntity;
	}
	
	
	public abstract void doValidate(Request opRequest) throws OperationException;
	public abstract Response doExecute(Request opRequest) throws OperationException;
	
	/**
	 * This method will be called after doExecute even if doExecute returns exception
	 */
	public void postExecute() {
		
	}
	
	/**
	 * This method should not throw any exception.
	 */
	private void updateSessionLastAccessTime (AppSessionUser appSessionUser) {
		try {
			if(appSessionUser != null && appSessionUser.isAuthenticated()) {
				this.sessionService.updateLastAccessTime( appSessionUser );
			}
		} catch (Throwable th) {
			log.error( "Internal unhandle exception in doing post process. Error:" + th.getMessage(), th);
		}
	}
	
	private void auditTransaction(OperationEntity operationEntity, Request request, Response response,
			OperationException exception, AppSessionUser appSessionUser, OperationContext context) {
		
		if(operationEntity == null || !operationEntity.isAuditRequired()) {
			log.info("Audit not enabled for operation {}, skipping auditing", operationEntity);
			return;
		}
		
		if(appSessionUser == null) {
			log.info("AppSession is null, skipping auditing");
			return;
		}
		if(context == null) {
			log.info("Operation Context is null, skipping auditing");
			return;
		}
		
		String responseString = null;
		int httpResponseCode = 200;
		if(exception != null) {
			httpResponseCode = 	exception.getHttpResponseCode();
			responseString =  "{\"operationMessage\": \"" + exception.getMessage() + "\"}";
		}
		
        
		String requestString = null;
		if(request != null) {
			requestString = getJsonString(request); 
		}
		if(response != null) {
			responseString = getJsonString(response); 
		}
		
		sessionService.auditTransaction(appSessionUser.getSessionId(), operationEntity.getOperationId(),
				appSessionUser.getUserId(), context.getStartTime(), context.getResponseTime(), httpResponseCode,
				requestString, responseString);
		
	    
	}
	
	private String getJsonString (Object obj) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		try {
			return mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			log.info("Failed to build json string for auditing", e);
			return "Failed to build json string for auditing";
		} 
	}
}
