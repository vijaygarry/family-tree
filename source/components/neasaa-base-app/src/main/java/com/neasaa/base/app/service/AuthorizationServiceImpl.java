package com.neasaa.base.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.neasaa.base.app.cache.CacheManager;
import com.neasaa.base.app.entity.AppRole;
import com.neasaa.base.app.entity.OperationEntity;
import com.neasaa.base.app.operation.BeanNames;
import com.neasaa.base.app.operation.exception.AccessDeniedException;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.utils.ExceptionUtils;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service (BeanNames.AUTHORIZATION_SERVICE_BEAN)
public class AuthorizationServiceImpl implements AuthorizationService {
	
	
	@Autowired
	@Qualifier(BeanNames.SESSION_SERVICE_BEAN)
	private SessionService sessionService;
	
	@Override
	public boolean isOperationAllowedForUser(OperationEntity aOperationEntity, AppSessionUser appSessionUser)
			throws OperationException {
		
		switch (aOperationEntity.getAuthorizationType()) {
		case NO_AUTHORIZATION:
			return true;
		case ROLE_BASE:
			sessionService.checkSessionValidity(appSessionUser);
			
			//AppUserDto userDto = getSessionUser(aAppSession);
			boolean operationAllowed =  checkOperationAllowedForUserAndChannel(appSessionUser, aOperationEntity);
			
			if(!operationAllowed) {
				throw new AccessDeniedException("Operation not allowed. Please contact administrator.");
			}
			return true;
		default:
			String msg = "Authtype " + aOperationEntity.getAuthorizationType() + " not supported by operation. Check " + aOperationEntity.getOperationId() + " configuration.";
			log.info(msg);
			throw ExceptionUtils.getInternalException("Failed to process your request, contact administrator", new Exception(msg));
		}
	}

	private boolean checkOperationAllowedForUserAndChannel(AppSessionUser appSessionUser, OperationEntity aOperationEntity) {
		List<String> userRoleNames = appSessionUser.getRoleIds();
		for(String roleName : userRoleNames) {
			AppRole role = getRoleById( roleName );
			if(role == null) {
				continue;
			}
			return role.hasOperation(aOperationEntity.getOperationId());
		}
		return false;
	}
	
//	private static AppUserDto getSessionUser (AppSession aAppSession) throws OperationException {
//		AppUserDto userDto = aAppSession.getSessionUser();
//		if(userDto == null) {
//			throw new OperationException(ErrorCodes.USER_UNAUTHORIZED, "Please login to perform this operation.");
//		}
//		return userDto;
//	}
	
	@Override
	public OperationEntity getOperationByName(String aOperationName) {
		return CacheManager.getCacheManager().getOperationByName(aOperationName);
	}

	@Override
	public AppRole getRoleById(String aRoleId) {
		return CacheManager.getCacheManager().getAppRoleById( aRoleId );
	}

}
