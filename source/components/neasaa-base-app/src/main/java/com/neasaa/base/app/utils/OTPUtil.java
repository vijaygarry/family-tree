package com.neasaa.base.app.utils;

import com.neasaa.base.app.entity.OtpVerification;
import com.neasaa.base.app.enums.OTPType;
import com.neasaa.base.app.operation.exception.InternalServerException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.base.app.utils.email.EmailMessage;
import com.neasaa.base.app.utils.email.EmailSender;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.UUID;

import static com.neasaa.base.app.enums.OTPStatus.Pending;

@Log4j2
public class OTPUtil {
    public static final long OTP_EXPIRY_DURATION = 15 * 60 * 1000; // 5 minutes in milliseconds

    public static final String FORGET_PASSWORD_OTP_SUBJECT = "Your OTP code to reset password";
    public static final String FORGET_PASSWORD_OTP_BODY_TEMPLATE = "Dear Customer,\n\nYour OTP to reset password is: %s\n\nRegards,\nRajput Chhipa Team";

    public static final String SIGNUP_OTP_SUBJECT = "Your OTP code to complete sign up";
    public static final String SIGNUP_OTP_BODY_TEMPLATE = "Dear Customer,\n\nYour OTP to complete sign up is: %s\n\nRegards,\nRajput Chhipa Team";

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
        if (otpInformation.getExpiryDate() != null && otpInformation.getExpiryDate().getTime() < currentTime) {
            log.info("OTP is expired");
            return true; // OTP is expired
        }

        if(otpInformation.getStatus() != Pending) {
            log.info ("OTP is already used, current status: {}", otpInformation.getStatus());
            return true;
        }

        return false; // OTP is still valid
    }

    public static String getLogonNameSuggestion(String emailId) {
        // This method can be used to set the logon name in the response
        String[] parts = emailId.split("@");
        return parts[0]; // Use the part before '@' as logon name
    }

    public static boolean isOTPValid (String providedOtp, OtpVerification otpInformation) throws ValidationException {
        return PasswordUtil.matchPassword(providedOtp, otpInformation.getHashOtpCode());
    }

    public static void sendOtpEmail(String emailId, String otpCode, OTPType otpType, AppProperties appProperties, EmailSender emailSender) {
        try {
            String emailSubject = null;
            String emailBody = null;
            if(otpType == OTPType.FORGOT_PASSWORD) {
                emailSubject = FORGET_PASSWORD_OTP_SUBJECT;
                emailBody = String.format(FORGET_PASSWORD_OTP_BODY_TEMPLATE, otpCode);
            } else if (otpType == OTPType.SIGN_UP) {
                emailSubject = SIGNUP_OTP_SUBJECT;
                emailBody = String.format(SIGNUP_OTP_BODY_TEMPLATE, otpCode);
            } else {
                log.error("Unsupported OTP type for email: {}", otpType);
                throw new InternalServerException("Failed to process your request, please contact administrator");
            }

            EmailMessage emailMessage = EmailMessage.builder()
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
