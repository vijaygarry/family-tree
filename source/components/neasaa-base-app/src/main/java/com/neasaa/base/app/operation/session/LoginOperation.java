package com.neasaa.base.app.operation.session;

import static com.neasaa.base.app.utils.ValidationUtils.checkObjectPresent;
import static com.neasaa.base.app.utils.ValidationUtils.checkValuePresent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.neasaa.base.app.entity.AppSession;
import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.BeanNames;
import com.neasaa.base.app.operation.OperationContext;
import com.neasaa.base.app.operation.OperationContext.ClientInformation;
import com.neasaa.base.app.operation.OperationNames;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.base.app.operation.session.model.LoginRequest;
import com.neasaa.base.app.operation.session.model.LoginResponse;
import com.neasaa.base.app.service.AuthenticatedUser;
import com.neasaa.base.app.service.AuthenticationService;
import com.neasaa.base.app.service.SessionService;
import com.neasaa.base.app.utils.ValidationUtils;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component ("LoginOperation")
@Scope("prototype")
public class LoginOperation extends AbstractOperation<LoginRequest, LoginResponse>{
	private static final boolean AUTHENTICATED_SESSION = true;
	private static final boolean ACTIVE_SESSION = true;
	
	private final AuthenticationService authenticationService;
	private final SessionService sessionService;
	
	@Autowired
	private LoginOperation (@Qualifier(BeanNames.AUTHENTICATION_SERVICE_BEAN) AuthenticationService authenticationService, @Qualifier(BeanNames.SESSION_SERVICE_BEAN) SessionService sessionService) {
		this.authenticationService = authenticationService;
		this.sessionService = sessionService;
	}
	
	@Override
	public String getOperationName() {
		return OperationNames.LOGIN;
	}
	
	@Override
	public void doValidate(LoginRequest opRequest) throws OperationException {
		if (opRequest == null) {
			throw new ValidationException("Invalid request provided.");
		}
		checkValuePresent(opRequest.getLoginName(), "user id");
		checkValuePresent(opRequest.getPassword(), "password");
		checkObjectPresent(opRequest.getClientInformation(), "client information");
		checkObjectPresent(opRequest.getClientInformation().getChannel(), "channel");
	}
	
	@Override
	public LoginResponse doExecute(LoginRequest opRequest) throws OperationException {
		AuthenticatedUser authenticatedUser = authenticationService.authenticateUser(opRequest.getLoginName(), opRequest.getPassword(), null);
		log.info( "User {} authenticated successfully! ", opRequest.getLoginName());
		OperationContext context = getContext();
		ClientInformation clientInformation = opRequest.getClientInformation();
		ValidationUtils.addToDoLog("Add hostAppName, OS Name and BrowserName during session creation", LoginOperation.class.getName());
		AppSession session = sessionService.createSession(authenticatedUser, AUTHENTICATED_SESSION, ACTIVE_SESSION, clientInformation.getChannel(), 
				context.getAppHostName(), clientInformation.getClientUserIpAddr(), 
				clientInformation.getDeviceType() + ":" + clientInformation.getOperatingSystem(), 
				clientInformation.getBrowserName() + ":" + clientInformation.getBrowserVersion());
		
		LoginResponse loginResponse = 
				LoginResponse.builder()
				.logonName(authenticatedUser.getLogonName())
				.firstName(authenticatedUser.getFirstName())
				.lastName(authenticatedUser.getLastName())
				.emailId(authenticatedUser.getEmailId())
				.sessionId(session.getSessionId())
				.sessionActive(true)
				.lastAccessTime(null)
				.build();
		return loginResponse;
	}
	
	@Override
	public void postExecute() {
		
	}

	

	

}
