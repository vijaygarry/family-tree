package com.neasaa.base.app.operation.session;

import static com.neasaa.base.app.utils.ValidationUtils.checkObjectPresent;
import static com.neasaa.base.app.utils.ValidationUtils.checkValuePresent;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.neasaa.base.app.dao.pg.UserRoleMapDao;
import com.neasaa.base.app.entity.AppSession;
import com.neasaa.base.app.entity.AppUser;
import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.BeanNames;
import com.neasaa.base.app.operation.OperationContext;
import com.neasaa.base.app.operation.OperationContext.ClientInformation;
import com.neasaa.base.app.operation.OperationNames;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.base.app.operation.session.model.LoginRequest;
import com.neasaa.base.app.operation.session.model.LoginResponse;
import com.neasaa.base.app.service.AppSessionUser;
import com.neasaa.base.app.service.AuthenticationService;
import com.neasaa.base.app.service.SessionService;
import com.neasaa.base.app.utils.ExceptionUtils;
import com.neasaa.base.app.utils.ValidationUtils;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component ("LoginOperation")
@Scope("prototype")
public class LoginOperation extends AbstractOperation<LoginRequest, LoginResponse>{
	private static final boolean AUTHENTICATED_SESSION = true;
	private static final boolean ACTIVE_SESSION = true;
	
	@Autowired
	@Qualifier(BeanNames.AUTHENTICATION_SERVICE_BEAN)
	private AuthenticationService authenticationService;
	
	@Autowired
	@Qualifier(BeanNames.SESSION_SERVICE_BEAN)
	private SessionService sessionService;
	
	@Autowired
	private UserRoleMapDao userRoleMapDao;
		
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
		String plainTextPassword = opRequest.getPassword();
		opRequest.setPassword(null);
		AppUser authenticatedUser = authenticationService.authenticateUser(opRequest.getLoginName(), plainTextPassword, null);
		log.info( "User {} authenticated successfully! ", opRequest.getLoginName());
		OperationContext context = getContext();
		ClientInformation clientInformation = opRequest.getClientInformation();
		ValidationUtils.addToDoLog("Add hostAppName, OS Name and BrowserName during session creation", LoginOperation.class.getName());
		AppSession appSession = sessionService.createSession(authenticatedUser, AUTHENTICATED_SESSION, ACTIVE_SESSION, clientInformation.getChannel(), 
				context.getAppHostName(), clientInformation.getClientUserIpAddr(), 
				clientInformation.getDeviceType() + ":" + clientInformation.getOperatingSystem(), 
				clientInformation.getBrowserName() + ":" + clientInformation.getBrowserVersion());
		
		List<String> roleIdsForUser;
		try {
			roleIdsForUser = userRoleMapDao.getRoleIdsByUserId(authenticatedUser.getUserId());
		} catch (SQLException e) {
			throw ExceptionUtils.getInternalException("Internal error while processing your request", e);
		}
		AppSessionUser appSessionUser = AppSessionUser.buildAuthenticatedUser(authenticatedUser, roleIdsForUser, appSession);		
		
		LoginResponse loginResponse = 
				LoginResponse.builder()
				.logonName(authenticatedUser.getLogonName())
				.firstName(authenticatedUser.getFirstName())
				.lastName(authenticatedUser.getLastName())
				.emailId(authenticatedUser.getEmailId())
				.sessionId(appSession.getSessionId())
				.sessionActive(true)
				.lastAccessTime(null)
				.appSessionUser(appSessionUser)
				.build();
		return loginResponse;
	}

}
