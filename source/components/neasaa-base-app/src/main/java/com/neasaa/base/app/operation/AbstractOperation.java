package com.neasaa.base.app.operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
	
	@Getter
	private OperationContext context;
	private Request request;
	private Response response;
	
	@Autowired 
	@Qualifier(BeanNames.AUTHORIZATION_SERVICE_BEAN)
	private AuthorizationService authorizationService;
	
//	@Autowired
//	@Qualifier(BeanNames.SESSION_SERVICE_BEAN)
//	private SessionService sessionService;
	
	
	@Override
	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Response execute(Request opRequest, OperationContext context) throws OperationException {
		this.context = context;
		if(context == null) {
			log.info("Operation context is not set, set context on operation before calling execute method.");
			throw new ValidationException ("Operation context is not set");
		}
		OperationEntity operationEntity = null;
		boolean operationSuccess = false;
		try {
			
			this.request = opRequest;
			
			AppSessionUser appSessionUser = this.context.getAppSessionUser();
			String operationName = this.getOperationName();
			
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
			operationSuccess = true;
			return response;
		}
		catch ( OperationException e ) {
			log.debug( "Failed to execute operation with error " + e.getMessage());
			throw e;
		}
		catch ( Throwable th ) {
			log.info( "Internal unhandle exception in executing the operation. Error:" + th.getMessage(), th);
			throw new InternalServerException(th.getMessage(), th);
		} finally {
			try {
				postExecute();
			} catch (Throwable th) {
				log.error( "Internal unhandle exception in doing post process. Error:" + th.getMessage(), th);
			}
			updateSessionLastAccessTime ();
			
			try {
				auditTransaction (operationSuccess);
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
	private void updateSessionLastAccessTime () {
		try {
//			if(this.appSession != null && this.appSession.isAuthenticated()) {
//				this.sessionService.updateLastAccessTime( this.appSession );
//			}
		} catch (Throwable th) {
			log.error( "Internal unhandle exception in doing post process. Error:" + th.getMessage(), th);
		}
	}
	
	private void auditTransaction (boolean operationSuccess) {
		
	}
}
