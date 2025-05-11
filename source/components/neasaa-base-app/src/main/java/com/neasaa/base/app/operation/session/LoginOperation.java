package com.neasaa.base.app.operation.session;

import static com.neasaa.base.app.utils.ValidationUtils.checkValuePresent;

import java.util.Date;

import com.neasaa.base.app.entity.AppSession;
import com.neasaa.base.app.enums.ChannelEnum;
import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.OperationNames;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.base.app.operation.model.EmptyOperationResponse;
import com.neasaa.base.app.operation.session.model.LoginRequest;
import com.neasaa.base.app.operation.session.model.LoginResponse;
import com.neasaa.base.app.service.AuthenticatedUser;
import com.neasaa.base.app.service.AuthenticationService;
import com.neasaa.base.app.service.SessionService;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class LoginOperation extends AbstractOperation<LoginRequest, LoginResponse>{
	
	private ChannelEnum selectedChannel;
	private final AuthenticationService authenticationService;
	private final SessionService sessionService;
	
	private LoginOperation (AuthenticationService authenticationService, SessionService sessionService) {
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
		checkValuePresent(opRequest.getChannel(), "channel");
		selectedChannel = ChannelEnum.getChannelFromString(opRequest.getChannel());
		if(selectedChannel == null) {
			throw new ValidationException("Please provide valid channel name.");
		}
	}
	
	@Override
	public LoginResponse doExecute(LoginRequest opRequest) throws OperationException {
		AuthenticatedUser authenticatedUser = authenticationService.authenticateUser(opRequest.getLoginName(), opRequest.getPassword(), null);
		log.info( "User {} authenticated successfully! ", opRequest.getLoginName());
		
		AppSession session = sessionService.createSession(authenticatedUser, false, selectedChannel, getOperationName(), getOperationName(), getOperationName(), getOperationName());

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
	
	private AppSession createSession (LoginRequest opRequest) {
		AppSession appSession = this.getSessionService().createSession( authenticateUser, true, this.selectedChannel,
				GeneralUtilities.getLocalHostName(), operationInput.getClientIpAddress(), operationInput.getClientOsName(), operationInput.getClientBrowserName() );
		sessionDetails.setSessionDetails(appSession);
	}
	@Override
	public void postExecute() {
		
	}

	

	

}
