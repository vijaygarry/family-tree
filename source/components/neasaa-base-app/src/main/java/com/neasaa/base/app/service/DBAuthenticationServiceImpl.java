package com.neasaa.base.app.service;

import java.util.Map;

import com.neasaa.base.app.dao.pg.AppUserDao;
import com.neasaa.base.app.entity.AppUser;
import com.neasaa.base.app.enums.UserStatusEnum;
import com.neasaa.base.app.operation.exception.AccessDeniedException;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.session.model.LoginRequest;
import com.neasaa.base.app.utils.PasswordUtil;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class DBAuthenticationServiceImpl implements AuthenticationService {
	// User can try login with invalid password for 5 time. 
	// On 5th invalid attempt, user status will be locked. 
	private static final int MAX_INVALID_LOGIN_ATTEMPTS_ALLOWED = 5;
	private final AppUserDao appUserDao;
	
	public DBAuthenticationServiceImpl (AppUserDao appUserDao) {
		this.appUserDao = appUserDao;
	}
	
	
	@Override
	public AuthenticatedUser authenticateUser(LoginRequest aLoginInput, Map<String, String> aOtherParams)
			throws Exception {
		AppUser appUser = this.appUserDao.getUserByLogonName( aLoginInput.getLoginName() );
		if(appUser == null) {
			log.info( "User " + aLoginInput.getLoginName() + " not found." );
			throw new AccessDeniedException ( "Invalid user or password.");
		}
		UserStatusEnum userStatus = UserStatusEnum.getUserStatusByCode(appUser.getStatus());
		if(userStatus != UserStatusEnum.ACTIVE) {
			log.info( "User " + aLoginInput.getLoginName() + " is not active" );
			throw new AccessDeniedException ("Invalid user or password.");
		}
		
		boolean pwdValid = PasswordUtil.matchPassword(aLoginInput.getPassword(), appUser.getHashPassword() );
		if(!pwdValid) {
			int invalidAttempts = appUser.getInvalidLoginAttempts() + 1;
			if(invalidAttempts == MAX_INVALID_LOGIN_ATTEMPTS_ALLOWED) {
				log.info( "Max invalid login attempts reached, locking the user." );
				userStatus = UserStatusEnum.LOCKED;
			}
			this.appUserDao.updateInvalidLoginAttempt( aLoginInput.getLoginName(), invalidAttempts, userStatus.getStatusCode());
			log.info( "User password does not match." );
			throw new AccessDeniedException ("Invalid user or password.");
		}
		
		this.appUserDao.updateLastSuccessLoginTime( aLoginInput.getLoginName(), appUser.getUserId());
		appUser = this.appUserDao.getUserByLogonName( aLoginInput.getLoginName() );
		return AuthenticatedUser.buildAuthenticatedUser( appUser );
	}

	@Override
	public void changePassword(String aLogonName, String aOldPassword, String aNewPassword) throws OperationException {
		
	}

}
