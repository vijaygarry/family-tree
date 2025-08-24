package com.neasaa.base.app.utils;

import com.neasaa.base.app.entity.OtpVerification;
import com.neasaa.base.app.operation.exception.ValidationException;
import lombok.extern.log4j.Log4j2;

import java.util.UUID;

import static com.neasaa.base.app.enums.OTPStatus.Pending;

@Log4j2
public class OTPUtil {
    public static final long OTP_EXPIRY_DURATION = 15 * 60 * 1000; // 5 minutes in milliseconds

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
        return PasswordUtil.matchPassword(otpInformation.getHashOtpCode(), providedOtp);
    }

}
