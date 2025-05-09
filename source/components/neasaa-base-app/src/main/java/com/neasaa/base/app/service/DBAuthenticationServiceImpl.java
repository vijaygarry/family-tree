package com.neasaa.base.app.service;

import java.util.Map;

import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.session.model.LoginRequest;

public class DBAuthenticationServiceImpl implements AuthenticationService {

	@Override
	public AuthenticatedUser authenticateUser(LoginRequest aLoginInput, Map<String, String> aOtherParams)
			throws Exception {
		return null;
	}

	@Override
	public void changePassword(String aLogonName, String aOldPassword, String aNewPassword) throws OperationException {
		
	}

}
