package com.neasaa.base.app.service;

import java.util.Map;

import com.neasaa.base.app.entity.AppUser;
import com.neasaa.base.app.operation.exception.OperationException;

public interface AuthenticationService {
	
	/**
	 * This method authenticate the user with specified details. If authentication is successful, returns the authenticated user
	 * Throws exception if user authentication fails.
	 * 
	 * @param aLoginDetails
	 * @param aOtherParams
	 * @return
	 * @throws Exception
	 */
	AppUser authenticateUser (String logonName, String plainTextPwd, Map<String, String> aOtherParams) throws OperationException;
	
	
	/**
	 * Verify the existing password and if valid, change it to new password.
	 * 
	 * @param logonName
	 * @param currentPassword
	 * @param newPassword
	 * @throws OperationException - Throws exception if failed to update the password.
	 */
	void changePassword (String logonName, String currentPassword, String newPassword) throws OperationException;
}
