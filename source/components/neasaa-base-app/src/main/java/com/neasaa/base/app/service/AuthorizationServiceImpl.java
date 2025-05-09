package com.neasaa.base.app.service;

import com.neasaa.base.app.entity.AppRole;
import com.neasaa.base.app.entity.AppSession;
import com.neasaa.base.app.entity.OperationEntity;
import com.neasaa.base.app.operation.exception.OperationException;

public class AuthorizationServiceImpl implements AuthorizationService {

	@Override
	public boolean isOperationAllowedForUser(OperationEntity aOperationEntity, AppSession aAppSession)
			throws OperationException {
		return false;
	}

	@Override
	public OperationEntity getOperationByName(String aOperationName) {
		return null;
	}

	@Override
	public AppRole getRoleById(String aRoleId) {
		// TODO Auto-generated method stub
		return null;
	}

}
