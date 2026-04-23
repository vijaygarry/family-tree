package com.neasaa.familytree.operation.signup;

import static com.neasaa.base.app.operation.BeanNames.APP_EMAIL_SENDER;
import static com.neasaa.base.app.operation.OperationNames.SIGN_UP_REQUEST_OTP;
import static com.neasaa.base.app.utils.ValidationUtils.checkValuePresent;
import static com.neasaa.base.app.constant.AppConstants.EMAIL_OTP_CHANNEL;
import static com.neasaa.base.app.constant.AppConstants.MOBILE_OTP_CHANNEL;

import com.neasaa.base.app.dao.pg.AppUserDao;
import com.neasaa.base.app.dao.pg.OtpVerificationDao;
import com.neasaa.base.app.entity.OtpVerification;
import com.neasaa.base.app.enums.OTPStatus;
import com.neasaa.base.app.enums.OTPType;
import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.base.app.utils.AppProperties;
import com.neasaa.base.app.utils.EmailValidator;
import com.neasaa.base.app.utils.OTPUtil;
import com.neasaa.base.app.utils.PasswordUtil;
import com.neasaa.base.app.utils.PhoneUtil;
import com.neasaa.base.app.utils.email.EmailSender;
import com.neasaa.familytree.dao.pg.FamilyMemberDao;
import com.neasaa.familytree.entity.FamilyMemberEntity;
import com.neasaa.familytree.operation.signup.model.RequestSignUpOTPRequest;
import com.neasaa.familytree.operation.signup.model.RequestSignUpOTPResponse;
import java.util.Date;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Log4j2
@Component("RequestSignUpOTPOperation")
@Scope("prototype")
public class RequestSignUpOTPOperation
    extends AbstractOperation<RequestSignUpOTPRequest, RequestSignUpOTPResponse> {

  @Autowired private AppUserDao appUserDao;

  @Autowired private FamilyMemberDao familyMemberDao;

  @Autowired private OtpVerificationDao otpVerificationDao;

  @Autowired private AppProperties appProperties;

  @Autowired
  @Qualifier(APP_EMAIL_SENDER)
  private EmailSender emailSender;

  @Override
  public String getOperationName() {
    return SIGN_UP_REQUEST_OTP;
  }

  @Override
  public void doValidate(RequestSignUpOTPRequest opRequest) throws OperationException {
    if (opRequest == null) {
      throw new ValidationException("Invalid request provided.");
    }
    checkValuePresent(opRequest.getOtpChannel(), "OTP channel");
  }

  @Override
  public RequestSignUpOTPResponse doExecute(RequestSignUpOTPRequest opRequest)
      throws OperationException {

    if(EMAIL_OTP_CHANNEL.equalsIgnoreCase(opRequest.getOtpChannel())) {
      return handleEmailOTP(opRequest);
    } else if(MOBILE_OTP_CHANNEL.equalsIgnoreCase(opRequest.getOtpChannel())) {
      return handleSMSOTP(opRequest);
    } else {
      throw new ValidationException("Please select one of the registration channel.");
    }
  }

  private RequestSignUpOTPResponse handleSMSOTP (RequestSignUpOTPRequest opRequest) {
    checkValuePresent(opRequest.getMobileNumber(), "mobile number");
    //Get phone number removing all chars other than digit. This will also prefix india country code if digit is 10 or less than 10.
    String mobileNumber = PhoneUtil.normalizePhoneNumber(opRequest.getMobileNumber());
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

    // Make sure member exists in family member table with this email
    FamilyMemberEntity memberEntity = familyMemberDao.getMemberByPhone(mobileNumber);
    if (memberEntity == null) {
      throw new ValidationException(
              "No family member associated with phone number " + mobileNumber + ", please register family before you signup. " +
                      "For more details contact administrator.");
    }

    // Fetch the existing OTP information if any
    OtpVerification otpInformation = getValidOTPInformation(mobileNumber);
    if (otpInformation != null) {
      // Current OTP is still valid, we can reuse the same OTP
      RequestSignUpOTPResponse response = new RequestSignUpOTPResponse();
      response.setOtpChannel(opRequest.getOtpChannel());
      response.setMobileNumber(PhoneUtil.formatPhoneNumber(mobileNumber));
      response.setRequestId(otpInformation.getRequestId());
      return response;
    }

    String newOtp = OTPUtil.generateOTP();
    String requestId = OTPUtil.generateRequestId();

    insertNewOTPVerificationInfo(newOtp, requestId, mobileNumber, OTPUtil.MOBILE_OTP_EXPIRY_DURATION);

    OTPUtil.sendOtpSMS(mobileNumber, newOtp, OTPType.SIGN_UP, memberEntity.getFirstName(), memberEntity.getLastName(),
            appProperties, emailSender);
    RequestSignUpOTPResponse response = new RequestSignUpOTPResponse();
    response.setOtpChannel(opRequest.getOtpChannel());
    response.setMobileNumber(PhoneUtil.formatPhoneNumber(mobileNumber));
    response.setRequestId(requestId);
    return response;

  }
  private RequestSignUpOTPResponse handleEmailOTP (RequestSignUpOTPRequest opRequest) {
    checkValuePresent(opRequest.getEmailId(), "Email Id");
    boolean isEmailMandatory = true;
    EmailValidator.validateEmail(opRequest.getEmailId(), isEmailMandatory);

    String emailId = opRequest.getEmailId();
    // Check if the email is already registered
    if (appUserDao.isEmailRegistered(emailId)) {
      throw new ValidationException(
              "Email Id is already registered. Please use a different email.");
    }

    // Make sure member exists in family member table with this email
    FamilyMemberEntity memberEntity = familyMemberDao.getMemberByEmail(emailId);
    if (memberEntity == null) {
      throw new ValidationException(
              "No family member associated with email " + emailId + ", please register family before you signup. " +
                      "For more details contact administrator.");
    }

    // Fetch the existing OTP information if any
    OtpVerification otpInformation = getValidOTPInformation(emailId);
    if (otpInformation != null) {
      // Current OTP is still valid, we can reuse the same OTP
      RequestSignUpOTPResponse response = new RequestSignUpOTPResponse();
      response.setOtpChannel(opRequest.getOtpChannel());
      response.setEmailId(emailId);
      response.setRequestId(otpInformation.getRequestId());
      return response;
    }

    String newOtp = OTPUtil.generateOTP();
    String requestId = OTPUtil.generateRequestId();
    insertNewOTPVerificationInfo(newOtp, requestId, emailId, OTPUtil.EMAIL_OTP_EXPIRY_DURATION);

    OTPUtil.sendOtpEmail(emailId, newOtp, OTPType.SIGN_UP, memberEntity.getFirstName(), memberEntity.getLastName(), appProperties, emailSender);
    RequestSignUpOTPResponse response = new RequestSignUpOTPResponse();
    response.setOtpChannel(opRequest.getOtpChannel());
    response.setEmailId(emailId);
    response.setRequestId(requestId);
    return response;
  }

  private OtpVerification getValidOTPInformation (String emailOrMobileNumber) {
    // Fetch the existing OTP information if any
    OtpVerification otpInformation = otpVerificationDao.getOtpInformation(emailOrMobileNumber, OTPType.SIGN_UP);
    log.info("OTP Verification info fetched from DB: {}", otpInformation);
    if (otpInformation == null) {
      return null;
    }
    //TODO: Check maximum attempts exceeded?

    if (!OTPUtil.isOtpExpired(otpInformation)) {
      return otpInformation;
    }


    // OTP exists but expired, move the existing OTP to history table and generate a new OTP
    otpInformation.setStatus(OTPStatus.Expired);
    otpVerificationDao.moveOtpToHistory(otpInformation);
    log.info(
            "Record moved to history table for {} and OTP Type: {}",
            emailOrMobileNumber,
            OTPType.SIGN_UP);
    return null;
  }

  private void insertNewOTPVerificationInfo (String newOtp, String requestId, String emailOrMobileNumber, long validationPeriodInMillis) {
    Date currentDate = new Date();

    OtpVerification otpVerificationInfo =
            OtpVerification.builder()
                    .emailId(emailOrMobileNumber)
                    .otpType(OTPType.SIGN_UP)
                    .requestId(requestId)
                    .hashOtpCode(PasswordUtil.hashPassword(newOtp)) // Store hashed OTP
                    .status(OTPStatus.Pending)
                    .expiryDate(
                            new Date(currentDate.getTime() + validationPeriodInMillis)) // OTP validity
                    .attempts(0)
                    .createdDate(currentDate)
                    .lastUpdatedDate(currentDate)
                    .build();

    otpVerificationDao.insertOtpVerification(otpVerificationInfo);
  }
}
