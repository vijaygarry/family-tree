package com.neasaa.base.app.service;

import java.sql.SQLException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.neasaa.base.app.dao.pg.AppUserDao;
import com.neasaa.base.app.entity.AppUser;
import com.neasaa.base.app.enums.UserStatusEnum;
import com.neasaa.base.app.operation.BeanNames;
import com.neasaa.base.app.operation.exception.AccessDeniedException;
import com.neasaa.base.app.operation.exception.InternalServerException;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.utils.PasswordUtil;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service (BeanNames.AUTHENTICATION_SERVICE_BEAN)
public class DBAuthenticationServiceImpl implements AuthenticationService {
	
	// User can try login with invalid password for 5 time. 
	// On 5th invalid attempt, user status will be locked. 
	private static final int MAX_INVALID_LOGIN_ATTEMPTS_ALLOWED = 5;
	private final AppUserDao appUserDao;
	
	public DBAuthenticationServiceImpl (AppUserDao appUserDao) {
		this.appUserDao = appUserDao;
	}
	
	
	@Override
	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public AuthenticatedUser authenticateUser(String logonName, String plainTextPwd, Map<String, String> aOtherParams)
			throws OperationException {
		try {
			AppUser appUser = this.appUserDao.getUserByLogonName(logonName);
			if (appUser == null) {
				log.info("User {} not found.", logonName);
				throw new AccessDeniedException("Invalid user or password.");
			}

			UserStatusEnum userStatus = UserStatusEnum.getUserStatusByCode(appUser.getStatus());
			if (userStatus != UserStatusEnum.ACTIVE) {
				log.info("User {} is not active", logonName);
				throw new AccessDeniedException("User is not active, please contact administrator.");
			}

			boolean pwdValid = PasswordUtil.matchPassword(plainTextPwd, appUser.getHashPassword());
			if (!pwdValid) {
				int invalidAttempts = appUser.getInvalidLoginAttempts() + 1;
				if (invalidAttempts == MAX_INVALID_LOGIN_ATTEMPTS_ALLOWED) {
					log.info("Max invalid login attempts reached, locking the user.");
					userStatus = UserStatusEnum.LOCKED;
				}
				this.appUserDao.updateInvalidLoginAttempt(logonName, invalidAttempts, userStatus.getStatusCode());
				log.info("User password does not match.");
				throw new AccessDeniedException("Invalid user or password.");
			}

			this.appUserDao.updateLastSuccessLoginTime(logonName, appUser.getUserId());
			appUser = this.appUserDao.getUserByLogonName(logonName);
			return AuthenticatedUser.buildAuthenticatedUser(appUser);
		} catch (SQLException se) {
			throw new InternalServerException("Internal exception while authenticating the user", se);
		}
	}

	@Override
	public void changePassword(String aLogonName, String aOldPassword, String aNewPassword) throws OperationException {
		
	}

}
