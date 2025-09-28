package com.neasaa.base.app.operation.session;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.neasaa.base.app.operation.OperationNames.RESET_FORGOT_PASSWORD;
import static com.neasaa.base.app.utils.ValidationUtils.checkValuePresent;

@Component("ResetForgotPasswordOperation")
@Scope("prototype")
public class ResetForgotPasswordOperation extends AbstractOperation<ResetForgotPasswordRequest, EmptyOperationResponse> {

    @Autowired
    private AppUserDao appUserDao;

    @Autowired
    private OtpVerificationDao otpVerificationDao;

    @Override
    public String getOperationName() {
        return RESET_FORGOT_PASSWORD;
    }

    @Override
    public void doValidate(ResetForgotPasswordRequest opRequest) throws OperationException {
        if (opRequest == null) {
            throw new ValidationException("Invalid request provided.");
        }
        checkValuePresent(opRequest.getEmailId(), "Email Id");
        checkValuePresent(opRequest.getOtp(), "One time password (OTP)");
        checkValuePresent(opRequest.getRequestId(), "Request Id");
        checkValuePresent(opRequest.getNewPassword(), "New password");
        boolean isMandatory = true;
        EmailValidator.validateEmail(opRequest.getEmailId(), isMandatory);
    }

    @Override
    public EmptyOperationResponse doExecute(ResetForgotPasswordRequest opRequest) throws OperationException {

        String emailId = opRequest.getEmailId().toLowerCase().trim();
        String password = opRequest.getNewPassword().trim();
        String otp = opRequest.getOtp().trim();

        // Check if the email is not registered
        AppUser userByEmailId = appUserDao.getUserByEmailId(emailId);
        if(userByEmailId == null) {
            throw new ValidationException("Email ID is not registered. Please check email ID");
        }

        // Get OTP for email, and OTP Type = FORGOT_PASSWORD
        OtpVerification otpInformation = otpVerificationDao.getOtpInformation(emailId, OTPType.FORGOT_PASSWORD);
        if(otpInformation == null) {
            throw new ValidationException("Invalid OTP for email ID " + emailId + ". Please request a new OTP.");
        }

        // We should remove this validation
        if (!opRequest.getRequestId().equalsIgnoreCase(otpInformation.getRequestId())) {
            throw new ValidationException("Invalid request ID provided.");
        }

        // Check if OTP expired
        if(OTPUtil.isOtpExpired(otpInformation)) {
            // If OTP exists but expired, move the existing OTP to history table and generate a new OTP
            // TODO: This should be done in a transaction to persit the information
            otpInformation.setStatus(OTPStatus.Expired);
            otpVerificationDao.moveOtpToHistory(otpInformation);
            throw new ValidationException("OTP expired, Please request a new OTP.");
        }

        if(!OTPUtil.isOTPValid(otp, otpInformation) ){
            // TODO: Update attempts and last attempt date in OTP table
            // TODO: As we are throwing exception if OTP does not match, DB transaction will roll back, so create new transaction for this update
            throw new ValidationException("Invalid OTP provided, please check the OTP and try again.");
        }

        if (opRequest.getNewPassword().length() < 6) {
            throw new ValidationException("Password must be at least 6 characters long.");
        }

        Date currentDate = new Date();
        appUserDao.updateUserPassword(
                userByEmailId.getLogonName(),
                PasswordUtil.hashPassword(password),
                userByEmailId.getUserId(),
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