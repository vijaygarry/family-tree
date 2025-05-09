package com.neasaa.base.app.service;

import com.neasaa.base.app.entity.AppRole;
import com.neasaa.base.app.entity.AppSession;
import com.neasaa.base.app.entity.OperationEntity;
import com.neasaa.base.app.operation.exception.OperationException;

public interface AuthorizationService {

	boolean isOperationAllowedForUser (OperationEntity aOperationEntity, AppSession aAppSession) throws OperationException;
	
	/**
	 * Return the operation entity specific to specified operation name.
	 * @param aOperationName
	 * @return
	 */
	OperationEntity getOperationByName (String aOperationName);
	
	/**
	 * Return the role entity for given role id.
	 * @param aRoleId
	 * @return
	 */
	AppRole getRoleById (String aRoleId);
}
