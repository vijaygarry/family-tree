package com.neasaa.familytree.operation.signup;

import static com.neasaa.base.app.constant.AppConstants.DEFAULT_ROLE_ON_SIGNUP;
import static com.neasaa.base.app.constant.AppConstants.EMAIL_OTP_CHANNEL;
import static com.neasaa.base.app.constant.AppConstants.MOBILE_OTP_CHANNEL;
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
import com.neasaa.base.app.operation.exception.InternalServerException;
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

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Log4j2
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
    checkValuePresent(opRequest.getOtpChannel(), "OTP channel");

    checkValuePresent(opRequest.getOtp(), "One time password (OTP)");
    checkValuePresent(opRequest.getRequestId(), "Request Id");
    checkValuePresent(opRequest.getPassword(), "Password");


  }

  @Override
  public EmptyOperationResponse doExecute(SignUpRequest opRequest) throws OperationException {
    String emailId = opRequest.getEmailId();

    if(EMAIL_OTP_CHANNEL.equalsIgnoreCase(opRequest.getOtpChannel())) {
      checkValuePresent(emailId, "Email Id");
      boolean isEmailMandatory = true;
      EmailValidator.validateEmail(emailId, isEmailMandatory);
      // Check if the email is already registered
      if (appUserDao.isEmailRegistered(emailId)) {
        throw new ValidationException(
                "Email Id is already registered. Please use 'Forgot Password' option to reset the password.");
      }
    } else if(MOBILE_OTP_CHANNEL.equalsIgnoreCase(opRequest.getOtpChannel())) {
      checkValuePresent(opRequest.getMobileNumber(), "mobile number");
      //Get phone number removing all chars other than digit. This will also prefix india country code if digit is 10 or less than 10.
      String mobileNumber = opRequest.getMobileNumber();
      log.info("Registering mobile number {}", mobileNumber);
      // Minimum 101 digits, country code can be 1 or more digit and 10-digits phone number
      if(mobileNumber.length() < 11) {
        throw new ValidationException("Please enter valid mobile number.");
      }
      // Check if the mobile number is already registered
      if (appUserDao.isPhoneRegistered(mobileNumber)) {
        throw new ValidationException(
                "Mobile number is already registered. Please use a different mobile number.");
      }
    } else {
      throw new ValidationException("Please select one of the registration channel.");
    }

    // Make sure member exists in family member table with this email/mobile
    FamilyMemberEntity familyMemberEntity = getFamilyEntityByIdentifier(opRequest);

    // Get OTP for email/phone, and OTP Type = SIGNUP
    OtpVerification otpInformation = getOtpInformationById(opRequest);

//    // We should remove this validation
//    if (!opRequest.getRequestId().equalsIgnoreCase(otpInformation.getRequestId())) {
//      throw new ValidationException("Invalid request ID provided.");
//    }

    // Check if OTP expired
    if (OTPUtil.isOtpExpired(otpInformation)) {
      // If OTP exists but expired, move the existing OTP to history table and generate a new OTP
      // TODO: This should be done in a transaction to persit the information
      otpInformation.setStatus(OTPStatus.Expired);
      otpVerificationDao.moveOtpToHistory(otpInformation);
      throw new ValidationException("OTP expired, Please request a new OTP.");
    }

    // This will throw exception if OTP does not match
    if (!OTPUtil.isOTPValid(opRequest.getOtp(), otpInformation)) {
      // update number of attempts. This function uses new transaction to make sure DB is updated even when we are throwing exception
      otpVerificationDao.updateOtpValidationAttempts(otpInformation);
      throw new ValidationException("Invalid OTP provided, please check the OTP and try again.");
    }

    if (opRequest.getPassword() == null || opRequest.getPassword().length() < 6) {
      throw new ValidationException("Password must be at least 6 characters long.");
    }
    String logonName = getLogonName(familyMemberEntity);
    log.info("Fetching user by logon name: {}", logonName);
    AppUser appUser = appUserDao.getUserByLogonName(logonName);
    Date currentDate = new Date();
    boolean emailVerified;
    boolean phoneVerified;


    int userId;
    if (appUser == null) {
      log.info("User not found with logon name {}, registering new user.", logonName);
      userId = registerNewUser(opRequest, familyMemberEntity, logonName, currentDate);
      emailVerified = EMAIL_OTP_CHANNEL.equalsIgnoreCase(opRequest.getOtpChannel());
      phoneVerified = MOBILE_OTP_CHANNEL.equalsIgnoreCase(opRequest.getOtpChannel());
    } else {
      log.info("User details: logon name: {}, Email: {}, Phone: {}", appUser.getLogonName(), appUser.getEmailId(), appUser.getPhone());
      if(EMAIL_OTP_CHANNEL.equalsIgnoreCase(opRequest.getOtpChannel())) {
          log.info("Existing phone for this user: {}", appUser.getPhone());
        appUser.setEmailId(opRequest.getEmailId());
        emailVerified = true;
        phoneVerified = familyMemberEntity.isPhoneVerified();
      } else {
        log.info("Existing email for this user: {}", appUser.getEmailId());
        appUser.setPhone(opRequest.getMobileNumber());
        emailVerified = familyMemberEntity.isEmailVerified();
        phoneVerified = true;
      }
      userId = appUser.getUserId();
      appUserDao.updateUserEmailAndPhone(appUser, userId, currentDate);
      appUserDao.updateUserPassword(appUser.getLogonName(), PasswordUtil.hashPassword(opRequest.getPassword()), userId, currentDate);
    }

    // Update logon name in family member table
    familyMemberDao.updateMemberLogonName(
            logonName, emailVerified, phoneVerified, userId, currentDate, familyMemberEntity.getMemberId());

    // Move OTP to history - update status and other fields
    otpInformation.setStatus(OTPStatus.Verified);
    otpInformation.setVerifiedAt(currentDate);
    otpInformation.setAttempts(otpInformation.getAttempts() + 1);
    otpInformation.setLastAttemptDate(currentDate);
    otpInformation.setLastUpdatedDate(currentDate);
    otpVerificationDao.moveOtpToHistory(otpInformation);

    return new EmptyOperationResponse("Sign Up completed successfully !!!");
  }

  private FamilyMemberEntity getFamilyEntityByIdentifier (SignUpRequest opRequest) {
    if(EMAIL_OTP_CHANNEL.equalsIgnoreCase(opRequest.getOtpChannel())) {
      FamilyMemberEntity familyMemberEntity = familyMemberDao.getMemberByEmail(opRequest.getEmailId());
      if (familyMemberEntity == null) {
        throw new ValidationException(
                "Email Id " + opRequest.getEmailId() + " is not allowed to signup, please contact administrator.");
      }
      return familyMemberEntity;
    }
    if(MOBILE_OTP_CHANNEL.equalsIgnoreCase(opRequest.getOtpChannel())) {
      FamilyMemberEntity familyMemberEntity = familyMemberDao.getMemberByPhone(opRequest.getMobileNumber());
      if (familyMemberEntity == null) {
        throw new ValidationException(
                "Mobile number " + opRequest.getMobileNumber() + " is not allowed to signup, please contact administrator.");
      }
      return familyMemberEntity;
    }

    throw new InternalServerException(
            "Internal exception, please try again later");
  }

  private OtpVerification getOtpInformationById (SignUpRequest opRequest) {
    if(EMAIL_OTP_CHANNEL.equalsIgnoreCase(opRequest.getOtpChannel())) {
      OtpVerification otpInformation = otpVerificationDao.getOtpInformation(opRequest.getEmailId(), OTPType.SIGN_UP);
      if (otpInformation == null) {
        throw new ValidationException(
              "Invalid OTP for email ID " + opRequest.getEmailId() + ". Please request a new OTP.");
      }
      //TODO: Check if maximum attempts exceeded?
      //TODO: Check status should be pending
      return otpInformation;
    }

    if(MOBILE_OTP_CHANNEL.equalsIgnoreCase(opRequest.getOtpChannel())) {
      OtpVerification otpInformation = otpVerificationDao.getOtpInformation(opRequest.getMobileNumber(), OTPType.SIGN_UP);
      if (otpInformation == null) {
        throw new ValidationException(
                "Invalid OTP for mobile " + opRequest.getMobileNumber() + ". Please request a new OTP.");
      }
      return otpInformation;
    }
    throw new InternalServerException(
            "Internal exception, please try again later");
  }

  private String getLogonName (FamilyMemberEntity familyMemberEntity) {
    return "member-" + familyMemberEntity.getMemberId();
  }

  private int registerNewUser (SignUpRequest opRequest, FamilyMemberEntity familyMemberEntity, String logonName, Date currentDate) {

    AppUser user =
            AppUser.builder()
                    .logonName(logonName)
                    .hashPassword(PasswordUtil.hashPassword(opRequest.getPassword()))
                    .firstName(familyMemberEntity.getFirstName())
                    .lastName(familyMemberEntity.getLastName())
                    .emailId(opRequest.getEmailId())
                    .phone(opRequest.getMobileNumber())
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
    return newUerId;
  }
}
