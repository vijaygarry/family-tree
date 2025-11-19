package com.neasaa.base.app.operation.session;

import static com.neasaa.base.app.constant.AppConstants.EMAIL_OTP_CHANNEL;
import static com.neasaa.base.app.constant.AppConstants.MOBILE_OTP_CHANNEL;
import static com.neasaa.base.app.operation.BeanNames.APP_EMAIL_SENDER;
import static com.neasaa.base.app.operation.OperationNames.FORGOT_PASSWORD_REQUEST_OTP;
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
import com.neasaa.base.app.operation.session.model.RequestForgotPasswordOTPRequest;
import com.neasaa.base.app.operation.session.model.RequestForgotPasswordOTPResponse;
import com.neasaa.base.app.utils.AppProperties;
import com.neasaa.base.app.utils.EmailValidator;
import com.neasaa.base.app.utils.OTPUtil;
import com.neasaa.base.app.utils.PasswordUtil;
import com.neasaa.base.app.utils.PhoneUtil;
import com.neasaa.base.app.utils.email.EmailSender;
import java.util.Date;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Log4j2
@Component("RequestForgotPasswordOTPOperation")
@Scope("prototype")
public class RequestForgotPasswordOTPOperation
    extends AbstractOperation<RequestForgotPasswordOTPRequest, RequestForgotPasswordOTPResponse> {

  @Autowired private AppUserDao appUserDao;

  @Autowired private OtpVerificationDao otpVerificationDao;

  @Autowired private AppProperties appProperties;

  @Autowired
  @Qualifier(APP_EMAIL_SENDER)
  private EmailSender emailSender;

  @Override
  public String getOperationName() {
    return FORGOT_PASSWORD_REQUEST_OTP;
  }

  @Override
  public void doValidate(RequestForgotPasswordOTPRequest opRequest) throws OperationException {
    if (opRequest == null) {
      throw new ValidationException("Invalid request provided.");
    }
    checkValuePresent(opRequest.getLoginName(), "mobile number/email ID");
  }

  @Override
  public RequestForgotPasswordOTPResponse doExecute(RequestForgotPasswordOTPRequest opRequest)
      throws OperationException {

    String logonName = opRequest.getLoginName().toLowerCase().trim();
    String otpChannel = null;
    AppUser appUser = null;
    if(EmailValidator.isEmailId(logonName)) {
      otpChannel = EMAIL_OTP_CHANNEL;
      log.info("Looking for user with emailId {}", logonName);
      appUser = this.appUserDao.getUserByEmailId(logonName);
      // Check logon name is not registered
      if (appUser == null) {
        throw new ValidationException("Email ID is not registered. Please check email ID");
      }
    } else {
      // Remove all non-digit characters
      logonName = PhoneUtil.normalizePhoneNumber(logonName);
      otpChannel = MOBILE_OTP_CHANNEL;
      log.info("Looking for user with mobile {}", logonName);
      appUser = this.appUserDao.getUserByPhone(logonName);
      // Check logon name is not registered
      if (appUser == null) {
        throw new ValidationException("Mobile number is not registered. Please check the number");
      }
    }

    // Fetch the existing OTP information if any
    OtpVerification otpInformation =
        otpVerificationDao.getOtpInformation(logonName, OTPType.FORGOT_PASSWORD);
    log.info("OTP Verification info fetched from DB: {}", otpInformation);
    if (otpInformation != null) {
      if (OTPUtil.isOtpExpired(otpInformation)) {
        // If OTP exists but expired, move the existing OTP to history table and generate a new OTP
        otpInformation.setStatus(OTPStatus.Expired);
        otpVerificationDao.moveOtpToHistory(otpInformation);
        log.info(
            "Record moved to history table for logonName: {} and OTP Type: {}", logonName,
            OTPType.SIGN_UP);
      } else {
        // Current OTP is still valid, we can resend the same OTP
        RequestForgotPasswordOTPResponse response = new RequestForgotPasswordOTPResponse();
        if(MOBILE_OTP_CHANNEL.equalsIgnoreCase(otpChannel)) {
          response.setLoginName(PhoneUtil.formatPhoneNumber(logonName));
        } else {
          response.setLoginName(logonName);
        }
        response.setOtpChannel(otpChannel);
        response.setRequestId(otpInformation.getRequestId()); // Simulated request ID for OTP
        return response;
      }
    }

    String newOtp = OTPUtil.generateOTP();
    String requestId = OTPUtil.generateRequestId();
    Date currentDate = new Date();
    Date expiryDate;
    if(MOBILE_OTP_CHANNEL.equalsIgnoreCase(otpChannel)) {
        expiryDate = new Date(currentDate.getTime() + OTPUtil.MOBILE_OTP_EXPIRY_DURATION); // OTP valid for 1 day
    } else {
        expiryDate = new Date(currentDate.getTime() + OTPUtil.EMAIL_OTP_EXPIRY_DURATION); // OTP valid for 15 minutes
    }

    OtpVerification otpVerificationInfo =
        OtpVerification.builder()
            .emailId(logonName)
            .otpType(OTPType.FORGOT_PASSWORD)
            .requestId(requestId)
            .hashOtpCode(
                PasswordUtil.hashPassword(
                    newOtp)) // In real application, hash the OTP before storing
            .status(OTPStatus.Pending)
            .expiryDate(expiryDate)
            .attempts(0)
            .createdDate(currentDate)
            .lastUpdatedDate(currentDate)
            .build();

    otpVerificationDao.insertOtpVerification(otpVerificationInfo);
    if(MOBILE_OTP_CHANNEL.equalsIgnoreCase(otpChannel)) {
      OTPUtil.sendOtpSMS(logonName, newOtp, OTPType.FORGOT_PASSWORD, appUser.getFirstName(), appUser.getLastName(),
              appProperties, emailSender);
    } else {
      OTPUtil.sendOtpEmail(logonName, newOtp, OTPType.FORGOT_PASSWORD, appProperties, emailSender);
    }

    RequestForgotPasswordOTPResponse response = new RequestForgotPasswordOTPResponse();
    if(MOBILE_OTP_CHANNEL.equalsIgnoreCase(otpChannel)) {
      response.setLoginName(PhoneUtil.formatPhoneNumber(logonName));
    } else {
      response.setLoginName(logonName);
    }
    response.setOtpChannel(otpChannel);
    response.setRequestId(requestId); // Simulated request ID for OTP
    return response;
  }
}
