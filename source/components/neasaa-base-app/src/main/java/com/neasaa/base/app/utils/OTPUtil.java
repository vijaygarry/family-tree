package com.neasaa.base.app.utils;

import static com.neasaa.base.app.enums.OTPStatus.Pending;

import com.neasaa.base.app.entity.OtpVerification;
import com.neasaa.base.app.enums.OTPType;
import com.neasaa.base.app.operation.exception.InternalServerException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.base.app.utils.email.EmailMessage;
import com.neasaa.base.app.utils.email.EmailSender;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class OTPUtil {
  public static final long EMAIL_OTP_EXPIRY_DURATION = 15 * 60 * 1000; // 15 minutes in milliseconds
  public static final long MOBILE_OTP_EXPIRY_DURATION = 72 * 60 * 60 * 1000; // 72 hours in milliseconds

  public static final String FORGET_PASSWORD_OTP_SUBJECT = "Your OTP code to reset password";
  public static final String FORGET_PASSWORD_OTP_BODY_TEMPLATE =
      "Dear %s,\n\nYour OTP to reset password is: %s\n\nRegards,\nRajput Chhipa Team";

  public static final String SIGNUP_OTP_SUBJECT = "Your OTP code to complete sign up";
  public static final String SIGNUP_OTP_BODY_TEMPLATE =
      "Dear %s,\n\nYour OTP to complete sign up is: %s\n\nRegards,\nRajput Chhipa Team";

  public static String generateOTP() {
    StringBuilder otp = new StringBuilder();
    for (int i = 0; i < 6; i++) {
      int digit = (int) (Math.random() * 10);
      otp.append(digit);
    }
    return otp.toString();
  }

  public static String generateRequestId() {
    return UUID.randomUUID().toString();
  }

  public static boolean isOtpExpired(OtpVerification otpInformation) {
    long currentTime = System.currentTimeMillis();
    log.info("Current time: {}, OTP expiry time: {}", currentTime, otpInformation.getExpiryDate());
    if (otpInformation.getExpiryDate() != null
        && otpInformation.getExpiryDate().getTime() < currentTime) {
      log.info("OTP is expired");
      return true; // OTP is expired
    }

    if (otpInformation.getStatus() != Pending) {
      log.info("OTP is already used, current status: {}", otpInformation.getStatus());
      return true;
    }

    return false; // OTP is still valid
  }

  public static String getLogonNameSuggestion(String emailId) {
    // This method can be used to set the logon name in the response
    String[] parts = emailId.split("@");
    return parts[0]; // Use the part before '@' as logon name
  }

  public static boolean isOTPValid(String providedOtp, OtpVerification otpInformation)
      throws ValidationException {
    return PasswordUtil.matchPassword(providedOtp, otpInformation.getHashOtpCode());
  }

  public static void sendOtpSMS(
          String phone,
          String otpCode,
          OTPType otpType,
          String firstName,
          String lastName,
          AppProperties appProperties,
          EmailSender emailSender) {
    String formattedPhone = PhoneUtil.formatPhoneNumber(phone);
    String emailSubject = null;
    String emailBody = null;
    String userMessage = null;
    if (otpType == OTPType.FORGOT_PASSWORD) {
      emailSubject = "Forget password request for mobile " + formattedPhone;
      userMessage = String.format("""
                    Dear %s %s,
                    
                    Your OTP to reset password is: %s
                    
                    Regards,
                    Rajput Chhipa Team""",
              firstName, lastName, otpCode);
    } else if (otpType == OTPType.SIGN_UP) {
      emailSubject = "Sign-Up request for mobile " + formattedPhone;
      userMessage = String.format("""
                    Dear %s %s,
                    
                    Your OTP to complete sign up is: %s
                    
                    Regards,
                    Rajput Chhipa Team""",
              firstName, lastName, otpCode);
    } else {
      log.error("Unsupported OTP type: {}", otpType);
      throw new InternalServerException(
              "Failed to process your request, please contact administrator");
    }
    String whatsApplink = buildWhatsAppUrl(phone, userMessage);
    emailBody = String.format("""
                    Send below message to mobile: %s.
                    WhatsApp link: %s
                    
                    %s""",
            formattedPhone, whatsApplink, userMessage);

    String emailListForSMSOtp = appProperties.getEmailListForSMSOtp();
    List<String> emailList = Arrays.asList(emailListForSMSOtp.trim().split(";"));
    EmailMessage emailMessage =
            EmailMessage.builder()
                    .from(appProperties.getEmailSenderEmailId())
                    .fromDisplayName(appProperties.getEmailSenderDisplayName())
                    .to(emailList)
                    .subject(emailSubject)
                    .body(emailBody)
                    .type(EmailMessage.EmailType.TEXT)
                    .build();
    log.info("Sending SMS OTP email to: {}, Subject: {}", emailList, emailSubject);
    emailSender.sendEmail(emailMessage);
  }

  /**
   * Builds a WhatsApp click-to-chat URL.
   *
   * @param mobileNumber Phone number in international format (e.g., 15714843777 for +1 571-484-3777)
   * @param message      Message to send
   * @return WhatsApp URL
   */
  public static String buildWhatsAppUrl(String mobileNumber, String message) {
    if (mobileNumber == null || mobileNumber.isEmpty()) {
      throw new IllegalArgumentException("Mobile number cannot be null or empty");
    }

    if (message == null) {
      message = "";
    }

      // Encode message (handles spaces, new lines, special chars)
      String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);

      // WhatsApp URL format
      return "https://wa.me/" + mobileNumber + "?text=" + encodedMessage;

  }

  public static void sendOtpEmail(
      String emailId,
      String otpCode,
      OTPType otpType,
      String firstName,
      String lastName,
      AppProperties appProperties,
      EmailSender emailSender) {
    try {
      String emailSubject = null;
      String emailBody = null;
      if (otpType == OTPType.FORGOT_PASSWORD) {
        emailSubject = FORGET_PASSWORD_OTP_SUBJECT;
        emailBody = String.format(FORGET_PASSWORD_OTP_BODY_TEMPLATE, (firstName + " " + lastName), otpCode);
      } else if (otpType == OTPType.SIGN_UP) {
        emailSubject = SIGNUP_OTP_SUBJECT;
        emailBody = String.format(SIGNUP_OTP_BODY_TEMPLATE, (firstName + " " + lastName), otpCode);
      } else {
        log.error("Unsupported OTP type for email: {}", otpType);
        throw new InternalServerException(
            "Failed to process your request, please contact administrator");
      }
      EmailMessage emailMessage =
          EmailMessage.builder()
              // TODO: Include replyTo address
              .from(appProperties.getEmailSenderEmailId())
              .fromDisplayName(appProperties.getEmailSenderDisplayName())
              .to(List.of(emailId))
              .subject(emailSubject)
              .body(emailBody)
              .type(EmailMessage.EmailType.TEXT)
              .build();
      log.info("Sending OTP email to: {}, Subject: {}", emailId, emailSubject);
      emailSender.sendEmail(emailMessage);
    } catch (Exception e) {
      log.error("Failed to send OTP email. Please try again later.", e);
      throw new InternalServerException("Failed to send OTP email. Please try again later.", e);
    }
    log.info("OTP email sent successfully to {}", emailId);
  }
}
