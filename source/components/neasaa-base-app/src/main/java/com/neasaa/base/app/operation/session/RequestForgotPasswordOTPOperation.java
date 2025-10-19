package com.neasaa.base.app.operation.session;

import static com.neasaa.base.app.operation.BeanNames.APP_EMAIL_SENDER;
import static com.neasaa.base.app.operation.OperationNames.FORGOT_PASSWORD_REQUEST_OTP;
import static com.neasaa.base.app.utils.OTPUtil.OTP_EXPIRY_DURATION;
import static com.neasaa.base.app.utils.ValidationUtils.checkValuePresent;

import com.neasaa.base.app.dao.pg.AppUserDao;
import com.neasaa.base.app.dao.pg.OtpVerificationDao;
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
    checkValuePresent(opRequest.getEmailId(), "Email Id");
    boolean isMandatory = true;
    EmailValidator.validateEmail(opRequest.getEmailId(), isMandatory);
  }

  @Override
  public RequestForgotPasswordOTPResponse doExecute(RequestForgotPasswordOTPRequest opRequest)
      throws OperationException {
    String emailId = opRequest.getEmailId().toLowerCase().trim();
    // Check if the email is not already registered
    if (!appUserDao.isEmailRegistered(emailId)) {
      throw new ValidationException("Email ID is not registered. Please check email ID");
    }

    // Fetch the existing OTP information if any
    OtpVerification otpInformation =
        otpVerificationDao.getOtpInformation(emailId, OTPType.FORGOT_PASSWORD);
    log.info("OTP Verification info fetched from DB: {}", otpInformation);
    if (otpInformation != null) {
      if (OTPUtil.isOtpExpired(otpInformation)) {
        // If OTP exists but expired, move the existing OTP to history table and generate a new OTP
        otpInformation.setStatus(OTPStatus.Expired);
        otpVerificationDao.moveOtpToHistory(otpInformation);
        log.info(
            "Record moved to history table for email: {} and OTP Type: {}",
            emailId,
            OTPType.SIGN_UP);
      } else {
        // Current OTP is still valid, we can resend the same OTP
        RequestForgotPasswordOTPResponse response = new RequestForgotPasswordOTPResponse();
        response.setEmailId(opRequest.getEmailId());
        response.setRequestId(otpInformation.getRequestId()); // Simulated request ID for OTP
        return response;
      }
    }

    String newOtp = OTPUtil.generateOTP();
    String requestId = OTPUtil.generateRequestId();
    Date currentDate = new Date();

    OtpVerification otpVerificationInfo =
        OtpVerification.builder()
            .emailId(emailId)
            .otpType(OTPType.FORGOT_PASSWORD)
            .requestId(requestId)
            .hashOtpCode(
                PasswordUtil.hashPassword(
                    newOtp)) // In real application, hash the OTP before storing
            .status(OTPStatus.Pending)
            .expiryDate(
                new Date(currentDate.getTime() + OTP_EXPIRY_DURATION)) // OTP valid for 15 minutes
            .attempts(0)
            .createdDate(currentDate)
            .lastUpdatedDate(currentDate)
            .build();

    otpVerificationDao.insertOtpVerification(otpVerificationInfo);
    OTPUtil.sendOtpEmail(emailId, newOtp, OTPType.FORGOT_PASSWORD, appProperties, emailSender);
    RequestForgotPasswordOTPResponse response = new RequestForgotPasswordOTPResponse();
    response.setEmailId(emailId);
    response.setRequestId(requestId); // Simulated request ID for OTP
    return response;
  }
}
