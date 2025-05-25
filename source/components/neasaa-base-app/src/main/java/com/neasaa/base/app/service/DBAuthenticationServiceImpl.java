package com.neasaa.base.app.service;

import java.sql.SQLException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.neasaa.base.app.dao.pg.AppUserDao;
import com.neasaa.base.app.entity.AppUser;
import com.neasaa.base.app.enums.UserStatusEnum;
import com.neasaa.base.app.operation.BeanNames;
import com.neasaa.base.app.operation.exception.InternalServerException;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.UnauthorizedException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.base.app.utils.PasswordUtil;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service (BeanNames.AUTHENTICATION_SERVICE_BEAN)
public class DBAuthenticationServiceImpl implements AuthenticationService {
	
	// User can try login with invalid password for 5 time. 
	// On 5th invalid attempt, user status will be locked. 
	private static final int MAX_INVALID_LOGIN_ATTEMPTS_ALLOWED = 5;
	
	@Autowired
	private AppUserDao appUserDao;
	
	
	@Override
	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public AppUser authenticateUser(String logonName, String plainTextPwd, Map<String, String> aOtherParams)
			throws OperationException {
		try {
			AppUser appUser = this.appUserDao.getUserByLogonName(logonName);
			if (appUser == null) {
				log.info("User {} not found.", logonName);
				throw new UnauthorizedException("Invalid user or password.");
			}
			log.info("User " + logonName + " found in DB with status as " + appUser.getStatus());
			UserStatusEnum userStatus = UserStatusEnum.getUserStatusByCode(appUser.getStatus());
			if (userStatus != UserStatusEnum.ACTIVE) {
				log.info("User {} is not active", logonName);
				throw new UnauthorizedException("User is not active, please contact administrator.");
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
				throw new UnauthorizedException("Invalid user or password.");
			}

			this.appUserDao.updateLastSuccessLoginTime(logonName, appUser.getUserId());
			appUser = this.appUserDao.getUserByLogonName(logonName);
			return appUser;
		} catch (SQLException se) {
			throw new InternalServerException("Internal exception while authenticating the user", se);
		}
	}

	@Override
	public void changePassword(String logonName, String currentPassword, String newPassword) throws OperationException {
		try {
			AppUser appUser = this.appUserDao.getUserByLogonName(logonName);
			if (appUser == null) {
				log.info("User {} not found.", logonName);
				// This should not happen, that's why internal exception
				throw new InternalServerException("User not found.");
			}
			
			boolean pwdValid = PasswordUtil.matchPassword(currentPassword, appUser.getHashPassword());
			if (!pwdValid) {
				log.info("User password does not match.");
				throw new ValidationException("Current password does not match.");
			}
			String hashPassword = PasswordUtil.hashPassword(newPassword);
			int recordsUpdated = this.appUserDao.updateUserPassword(logonName, hashPassword);
			if(recordsUpdated != 1) {
				throw new InternalServerException("Failed to update password");
			}
			
		} catch (SQLException se) {
			throw new InternalServerException("Internal exception please contact administrator", se);
		}
	}

}
