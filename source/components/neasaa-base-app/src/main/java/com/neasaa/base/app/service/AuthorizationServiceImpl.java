package com.neasaa.base.app.service;

import org.springframework.stereotype.Service;

import com.neasaa.base.app.entity.AppRole;
import com.neasaa.base.app.entity.OperationEntity;
import com.neasaa.base.app.operation.BeanNames;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.session.model.UserSessionDetails;

@Service (BeanNames.AUTHORIZATION_SERVICE_BEAN)
public class AuthorizationServiceImpl implements AuthorizationService {

	@Override
	public boolean isOperationAllowedForUser(OperationEntity aOperationEntity, UserSessionDetails userSessionDetails)
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
