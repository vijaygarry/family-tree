package com.neasaa.base.app.operation.session;

import static com.neasaa.base.app.utils.ValidationUtils.checkValuePresent;

import com.neasaa.base.app.enums.ChannelEnum;
import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.OperationNames;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.base.app.operation.model.EmptyOperationResponse;
import com.neasaa.base.app.operation.session.model.LoginRequest;
import com.neasaa.base.app.service.AuthenticationService;

public class LoginOperation extends AbstractOperation<LoginRequest, EmptyOperationResponse>{
	
	private ChannelEnum selectedChannel;
	private AuthenticationService authenticationService;
	
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
	public EmptyOperationResponse doExecute(LoginRequest opRequest) throws OperationException {
		
		return new EmptyOperationResponse("Login successfully");
	}

	@Override
	public void postExecute() {
		
	}

	

	

}
