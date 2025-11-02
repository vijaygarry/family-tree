package com.neasaa.familytree.operation.signup;

import static com.neasaa.base.app.constant.AppConstants.DEFAULT_ROLE_ON_SIGNUP;
import static com.neasaa.base.app.constant.AppConstants.SYSTEM_USER_ID;
import static com.neasaa.base.app.enums.UserStatusEnum.ACTIVE;
import static com.neasaa.base.app.operation.OperationNames.SIGN_UP;
import static com.neasaa.base.app.utils.ValidationUtils.checkValuePresent;

import com.neasaa.base.app.dao.pg.AppUserDao;
import com.neasaa.base.app.dao.pg.OtpVerificationDao;
import com.neasaa.base.app.dao.pg.UserRoleMapDao;
import com.neasaa.base.app.entity.AppUser;
import com.neasaa.base.app.entity.OtpVerification;
import com.neasaa.base.app.entity.UserRoleMap;
import com.neasaa.base.app.enums.AuthorizationType;
import com.neasaa.base.app.enums.OTPStatus;
import com.neasaa.base.app.enums.OTPType;
import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.base.app.operation.model.EmptyOperationResponse;
import com.neasaa.base.app.utils.EmailValidator;
import com.neasaa.base.app.utils.OTPUtil;
import com.neasaa.base.app.utils.PasswordUtil;
import com.neasaa.familytree.dao.pg.FamilyMemberDao;
import com.neasaa.familytree.entity.FamilyMemberEntity;
import com.neasaa.familytree.operation.signup.model.SignUpRequest;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("SignUpOperation")
@Scope("prototype")
public class SignUpOperation extends AbstractOperation<SignUpRequest, EmptyOperationResponse> {

  @Autowired private AppUserDao appUserDao;

  @Autowired private FamilyMemberDao familyMemberDao;

  @Autowired private OtpVerificationDao otpVerificationDao;

  @Autowired private UserRoleMapDao userRoleMapDao;

  @Override
  public String getOperationName() {
    return SIGN_UP;
  }

  @Override
  public void doValidate(SignUpRequest opRequest) throws OperationException {
    if (opRequest == null) {
      throw new ValidationException("Invalid request provided.");
    }
    checkValuePresent(opRequest.getEmailId(), "Email Id");
    checkValuePresent(opRequest.getOtp(), "One time password (OTP)");
    checkValuePresent(opRequest.getRequestId(), "Request Id");
    checkValuePresent(opRequest.getPassword(), "Password");
    boolean isEmailMandatory = true;
    EmailValidator.validateEmail(opRequest.getEmailId(), isEmailMandatory);
  }

  @Override
  public EmptyOperationResponse doExecute(SignUpRequest opRequest) throws OperationException {

    String emailId = opRequest.getEmailId().toLowerCase().trim();
    String password = opRequest.getPassword().trim();
    String otp = opRequest.getOtp().trim();

    // Check if the email is already registered
    if (appUserDao.isEmailRegistered(emailId)) {
      throw new ValidationException(
          "Email Id is already registered. Please use 'Forgot Password' option to reset the password.");
    }

    AppUser userByLogonName = appUserDao.getUserByLogonName(emailId);
    if (userByLogonName != null) {
      throw new ValidationException(
          "Email Id "
              + emailId
              + " is already registered. Please use 'Forgot Password' option to reset the password.");
    }

    // Make sure member exists in family member table with this email
    FamilyMemberEntity memberByEmail = familyMemberDao.getMemberByEmail(emailId);
    if (memberByEmail == null) {
      throw new ValidationException(
          "Email Id " + emailId + " is not allowed to signup, please contact administrator.");
    }

    // Get OTP for email, and OTP Type = SIGNUP
    OtpVerification otpInformation = otpVerificationDao.getOtpInformation(emailId, OTPType.SIGN_UP);
    if (otpInformation == null) {
      throw new ValidationException(
          "Invalid OTP for email ID " + emailId + ". Please request a new OTP.");
    }

    // We should remove this validation
    if (!opRequest.getRequestId().equalsIgnoreCase(otpInformation.getRequestId())) {
      throw new ValidationException("Invalid request ID provided.");
    }

    // Check if OTP expired
    if (OTPUtil.isOtpExpired(otpInformation)) {
      // If OTP exists but expired, move the existing OTP to history table and generate a new OTP
      // TODO: This should be done in a transaction to persit the information
      otpInformation.setStatus(OTPStatus.Expired);
      otpVerificationDao.moveOtpToHistory(otpInformation);
      throw new ValidationException("OTP expired, Please request a new OTP.");
    }

    // This will throw exception if OTP does not match
    if (!OTPUtil.isOTPValid(otp, otpInformation)) {
      // TODO: Update attempts and last attempt date in OTP table
      // TODO: As we are throwing exception if OTP does not match, DB transaction will roll back, so
      // create new transaction for this update
      throw new ValidationException("Invalid OTP provided, please check the OTP and try again.");
    }

    if (opRequest.getPassword() == null || opRequest.getPassword().length() < 6) {
      throw new ValidationException("Password must be at least 6 characters long.");
    }
    Date currentDate = new Date();

    // Get first name, last name from member details
    AppUser user =
        AppUser.builder()
            .logonName(emailId)
            .hashPassword(PasswordUtil.hashPassword(password))
            .firstName(memberByEmail.getFirstName())
            .lastName(memberByEmail.getLastName())
            .emailId(emailId)
            .authenticationType(AuthorizationType.ROLE_BASE.name())
            .invalidLoginAttempts(0)
            .status(ACTIVE.getStatusCode())
            .createdBy(SYSTEM_USER_ID)
            .createdDate(currentDate)
            .lastUpdatedBy(SYSTEM_USER_ID)
            .lastUpdatedDate(currentDate)
            .build();

    // Create entry in app user table with password and other details
    int newUerId = appUserDao.registerAppUser(user);

    // Update logon name in family member table
    familyMemberDao.updateMemberLogonNameWithEmail(
        emailId, newUerId, currentDate, memberByEmail.getMemberId());

    // Add default roles to user in user role table
    UserRoleMap userRoleMap =
        UserRoleMap.builder()
            .userId(newUerId)
            .roleId(DEFAULT_ROLE_ON_SIGNUP)
            .createdBy(newUerId)
            .createdDate(currentDate)
            .lastupdatedBy(newUerId)
            .lastupdatedDate(currentDate)
            .build();

    userRoleMapDao.insertUserRoleMap(userRoleMap);

    // Move OTP to history - update status and other fields
    otpInformation.setStatus(OTPStatus.Verified);
    otpInformation.setVerifiedAt(currentDate);
    otpInformation.setAttempts(otpInformation.getAttempts() + 1);
    otpInformation.setLastAttemptDate(currentDate);
    otpInformation.setLastUpdatedDate(currentDate);
    otpVerificationDao.moveOtpToHistory(otpInformation);

    return new EmptyOperationResponse("Sign Up completed successfully !!!");
  }
}
