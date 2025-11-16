package com.neasaa.base.app.service;

import com.neasaa.base.app.dao.pg.AppUserDao;
import com.neasaa.base.app.entity.AppUser;
import com.neasaa.base.app.enums.UserStatusEnum;
import com.neasaa.base.app.operation.BeanNames;
import com.neasaa.base.app.operation.exception.InternalServerException;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.UnauthorizedException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.base.app.utils.EmailValidator;
import com.neasaa.base.app.utils.PasswordUtil;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import com.neasaa.base.app.utils.PhoneUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service(BeanNames.AUTHENTICATION_SERVICE_BEAN)
public class DBAuthenticationServiceImpl implements AuthenticationService {

  // User can try login with invalid password for 5 time.
  // On 5th invalid attempt, user status will be locked.
  private static final int MAX_INVALID_LOGIN_ATTEMPTS_ALLOWED = 5;

  @Autowired private AppUserDao appUserDao;

  @Override
  @Transactional(
      value = "transactionManager",
      propagation = Propagation.REQUIRES_NEW,
      rollbackFor = Exception.class)
  public AppUser authenticateUser(
      String logonName, String plainTextPwd, Map<String, String> aOtherParams)
      throws OperationException {
    try {

      AppUser appUser = getUserByLogonName(logonName);
      if (appUser == null) {
        log.info("User {} not found.", logonName);
        throw new UnauthorizedException("Invalid user or password.");
      }
      String appUserLogonName = appUser.getLogonName();
      log.info("User {} found in DB with status as {}", appUserLogonName, appUser.getStatus());
      UserStatusEnum userStatus = UserStatusEnum.getUserStatusByCode(appUser.getStatus());
      if (userStatus != UserStatusEnum.ACTIVE) {
        log.info("User {} is not active", appUserLogonName);
        throw new UnauthorizedException("User is not active, please contact administrator.");
      }

      boolean pwdValid = PasswordUtil.matchPassword(plainTextPwd, appUser.getHashPassword());
      if (!pwdValid) {
        int invalidAttempts = appUser.getInvalidLoginAttempts() + 1;
        if (invalidAttempts == MAX_INVALID_LOGIN_ATTEMPTS_ALLOWED) {
          log.info("Max invalid login attempts reached, locking the user.");
          userStatus = UserStatusEnum.LOCKED;
        }
        this.appUserDao.updateInvalidLoginAttempt(
                appUserLogonName, invalidAttempts, userStatus.getStatusCode());
        log.info("User password does not match.");
        throw new UnauthorizedException("Invalid user or password.");
      }

      this.appUserDao.updateLastSuccessLoginTime(appUserLogonName, appUser.getUserId());
      return this.appUserDao.getUserByLogonName(appUserLogonName);
    } catch (SQLException se) {
      throw new InternalServerException("Internal exception while authenticating the user", se);
    }
  }

  private AppUser getUserByLogonName (String logonName) {

    if(EmailValidator.isEmailId(logonName)) {
      log.info("Looking for user with emailId {}", logonName);
      return this.appUserDao.getUserByEmailId(logonName);
    }

    // Remove all non-digit characters
    try {
      String mobileNumber = PhoneUtil.normalizePhoneNumber(logonName);
      log.info("Looking for user with mobile {}", mobileNumber);
      return this.appUserDao.getUserByPhone(mobileNumber);
    } catch(ValidationException e) {
        // Ignore exception, as logon name may not be phone number
    }

    log.info("Looking for user with logon name {}", logonName);
    return this.appUserDao.getUserByLogonName(logonName);
  }

  @Override
  public void changePassword(
      String logonName,
      String currentPassword,
      String newPassword,
      int updatedBy,
      Date lastUpdatedDate)
      throws OperationException {
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
    int recordsUpdated =
        this.appUserDao.updateUserPassword(logonName, hashPassword, updatedBy, lastUpdatedDate);
    if (recordsUpdated != 1) {
      throw new InternalServerException("Failed to update password");
    }
  }
}
