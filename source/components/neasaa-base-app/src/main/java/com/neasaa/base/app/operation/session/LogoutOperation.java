package com.neasaa.base.app.operation.session;

import static com.neasaa.base.app.utils.ValidationUtils.checkObjectPresent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.neasaa.base.app.enums.SessionExitCode;
import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.BeanNames;
import com.neasaa.base.app.operation.OperationNames;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.base.app.operation.model.EmptyOperationResponse;
import com.neasaa.base.app.operation.session.model.LogoutRequest;
import com.neasaa.base.app.service.AppSessionUser;
import com.neasaa.base.app.service.SessionService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component ("LogoutOperation")
@Scope("prototype")
public class LogoutOperation extends AbstractOperation<LogoutRequest, EmptyOperationResponse>{

	@Autowired
	@Qualifier(BeanNames.SESSION_SERVICE_BEAN)
	private SessionService sessionService;

	@Override
	public String getOperationName() {
		return OperationNames.LOGOUT;
	}

	@Override
	public void doValidate(LogoutRequest opRequest) throws OperationException {
		if (opRequest == null) {
			throw new ValidationException("Invalid request provided.");
		}
		checkObjectPresent(opRequest.getSessionExitCode(), "Logout exit code");
	}

	@Override
	public EmptyOperationResponse doExecute(LogoutRequest opRequest) throws OperationException {
		SessionExitCode sessionExitCode = opRequest.getSessionExitCode();
		EmptyOperationResponse response = null;
		AppSessionUser appSessionUser = getContext().getAppSessionUser();
		if ( appSessionUser == null) {
			response = new EmptyOperationResponse("No session exists");
			return response;
		}
		log.info("Logout request for " + appSessionUser.getLogonName() + " session " + appSessionUser.getSessionId() + " exit code " + sessionExitCode);
//		if(!appSessionUser.isAuthenticated()) {
//			response = new EmptyOperationResponse("No session exists");
//			return response;
//		}
		
		boolean result = sessionService.logoutSession(appSessionUser, sessionExitCode);
		
		if(result) {
			response = new EmptyOperationResponse("Logout successful!!!");
		} else {
			response = new EmptyOperationResponse("Logout failed");
		}
		// As logout is called, invalidate session.
		appSessionUser.invalidate();
		return response;
	}

}
