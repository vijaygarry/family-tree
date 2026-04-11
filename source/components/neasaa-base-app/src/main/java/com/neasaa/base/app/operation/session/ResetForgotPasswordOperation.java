package com.neasaa.base.app.operation.session;

import static com.neasaa.base.app.constant.AppConstants.EMAIL_OTP_CHANNEL;
import static com.neasaa.base.app.constant.AppConstants.MOBILE_OTP_CHANNEL;
import static com.neasaa.base.app.operation.OperationNames.RESET_FORGOT_PASSWORD;
import static com.neasaa.base.app.utils.ValidationUtils.checkValuePresent;

import com.neasaa.base.app.dao.pg.AppUserDao;
import com.neasaa.base.app.dao.pg.OtpVerificationDao;
import com.neasaa.base.app.entity.AppUser;
import com.neasaa.base.app.entity.OtpVerification;
import com.neasaa.base.app.enums.OTPStatus;
import com.neasaa.base.app.enums.OTPType;
import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.base.app.operation.model.EmptyOperationResponse;
import com.neasaa.base.app.operation.session.model.ResetForgotPasswordRequest;
import com.neasaa.base.app.utils.EmailValidator;
import com.neasaa.base.app.utils.OTPUtil;
import com.neasaa.base.app.utils.PasswordUtil;
import java.util.Date;

import com.neasaa.base.app.utils.PhoneUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("ResetForgotPasswordOperation")
@Scope("prototype")
public class ResetForgotPasswordOperation
    extends AbstractOperation<ResetForgotPasswordRequest, EmptyOperationResponse> {

  @Autowired private AppUserDao appUserDao;

  @Autowired private OtpVerificationDao otpVerificationDao;

  @Override
  public String getOperationName() {
    return RESET_FORGOT_PASSWORD;
  }

  @Override
  public void doValidate(ResetForgotPasswordRequest opRequest) throws OperationException {
    if (opRequest == null) {
      throw new ValidationException("Invalid request provided.");
    }
    checkValuePresent(opRequest.getLoginName(), "Email Id/Mobile Number");
    checkValuePresent(opRequest.getOtpChannel(), "otp channel");
    checkValuePresent(opRequest.getOtp(), "One time password (OTP)");
    checkValuePresent(opRequest.getRequestId(), "Request Id");
    checkValuePresent(opRequest.getNewPassword(), "New password");
  }

  @Override
  public EmptyOperationResponse doExecute(ResetForgotPasswordRequest opRequest)
      throws OperationException {
    String optChannel = opRequest.getOtpChannel().toLowerCase().trim();
    AppUser appUSer;
    String normalizedLoginName;
    if(EMAIL_OTP_CHANNEL.equalsIgnoreCase(optChannel)) {
      boolean isMandatory = true;
      EmailValidator.validateEmail(opRequest.getLoginName(), isMandatory);
      // Check if the email is not registered
      appUSer = appUserDao.getUserByEmailId(opRequest.getLoginName());
      if (appUSer == null) {
        throw new ValidationException("Email ID is not registered. Please check email ID");
      }
      normalizedLoginName = opRequest.getLoginName();
    } else if(MOBILE_OTP_CHANNEL.equalsIgnoreCase(optChannel)) {
      // Check if the phone is not registered
      String phoneNumber =
          PhoneUtil.normalizePhoneNumber(opRequest.getLoginName());
      appUSer = appUserDao.getUserByPhone(phoneNumber);
      if (appUSer == null) {
        throw new ValidationException("Mobile number is not registered. Please check mobile number");
      }
      normalizedLoginName = phoneNumber;
    } else {
      throw new ValidationException("Unsupported OTP channel: " + optChannel);
    }

    String password = opRequest.getNewPassword();
    String otp = opRequest.getOtp();

    // Get OTP for email, and OTP Type = FORGOT_PASSWORD
    OtpVerification otpInformation =
        otpVerificationDao.getOtpInformation(normalizedLoginName, OTPType.FORGOT_PASSWORD);
    if (otpInformation == null) {
      throw new ValidationException(
          "Invalid OTP for " + opRequest.getLoginName() + ". Please request a new OTP.");
    }

    // We should remove this validation
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

    if (!OTPUtil.isOTPValid(otp, otpInformation)) {
      // update number of attempts. This function uses new transaction to make sure DB is updated even when we are throwing exception
      otpVerificationDao.updateOtpValidationAttempts(otpInformation);
      throw new ValidationException("Invalid OTP provided, please check the OTP and try again.");
    }

    if (opRequest.getNewPassword().length() < 6) {
      throw new ValidationException("Password must be at least 6 characters long.");
    }

    Date currentDate = new Date();
    appUserDao.updateUserPassword(
        appUSer.getLogonName(),
        PasswordUtil.hashPassword(password),
        appUSer.getUserId(),
        currentDate);

    // Move OTP to history - update status and other fields
    otpInformation.setStatus(OTPStatus.Verified);
    otpInformation.setVerifiedAt(currentDate);
    otpInformation.setAttempts(otpInformation.getAttempts() + 1);
    otpInformation.setLastAttemptDate(currentDate);
    otpInformation.setLastUpdatedDate(currentDate);
    otpVerificationDao.moveOtpToHistory(otpInformation);

    return new EmptyOperationResponse("Forgot password completed successfully !!!");
  }
}
