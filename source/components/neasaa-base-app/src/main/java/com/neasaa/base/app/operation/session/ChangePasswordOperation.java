package com.neasaa.base.app.operation.session;

import static com.neasaa.base.app.utils.ValidationUtils.checkValuePresent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.BeanNames;
import com.neasaa.base.app.operation.OperationNames;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.UnauthorizedException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.base.app.operation.model.EmptyOperationResponse;
import com.neasaa.base.app.operation.session.model.ChangePasswordRequest;
import com.neasaa.base.app.service.AppSessionUser;
import com.neasaa.base.app.service.AuthenticationService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component ("ChangePasswordOperation")
@Scope("prototype")
public class ChangePasswordOperation extends AbstractOperation<ChangePasswordRequest, EmptyOperationResponse>{

	@Autowired
	@Qualifier(BeanNames.AUTHENTICATION_SERVICE_BEAN)
	private AuthenticationService authenticationService;
	
	@Override
	public String getOperationName() {
		return OperationNames.CHANGE_PASSWORD;
	}

	@Override
	public void doValidate(ChangePasswordRequest opRequest) throws OperationException {
		// Both password required
		if (opRequest == null) {
			throw new ValidationException("Invalid request provided.");
		}
		checkValuePresent(opRequest.getCurrentPassword(), "Current password");
		checkValuePresent(opRequest.getNewPassword(), "New password");
		if(opRequest.getCurrentPassword().equalsIgnoreCase(opRequest.getNewPassword())) {
			throw new ValidationException ("Current and new password can not be same.");
		}
	}

	@Override
	public EmptyOperationResponse doExecute(ChangePasswordRequest opRequest) throws OperationException {
		AppSessionUser appSessionUser = getContext().getAppSessionUser();
		if ( appSessionUser == null) {
			throw new UnauthorizedException("Login to change the password");
		}
		log.info("Changing password for user " + appSessionUser.getLogonName());
		authenticationService.changePassword(appSessionUser.getLogonName(), opRequest.getCurrentPassword(), opRequest.getNewPassword());
		
		return new EmptyOperationResponse("Password change successfully !!!");
	}

}
