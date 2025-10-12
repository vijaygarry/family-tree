package com.neasaa.familytree.operation.signup;

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
import com.neasaa.base.app.utils.email.EmailSender;
import com.neasaa.familytree.dao.pg.FamilyMemberDao;
import com.neasaa.familytree.operation.signup.model.RequestSignUpOTPRequest;
import com.neasaa.familytree.operation.signup.model.RequestSignUpOTPResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.neasaa.base.app.operation.BeanNames.APP_EMAIL_SENDER;
import static com.neasaa.base.app.operation.OperationNames.SIGN_UP_REQUEST_OTP;
import static com.neasaa.base.app.utils.OTPUtil.OTP_EXPIRY_DURATION;
import static com.neasaa.base.app.utils.ValidationUtils.checkValuePresent;

@Log4j2
@Component("RequestSignUpOTPOperation")
@Scope("prototype")
public class RequestSignUpOTPOperation extends AbstractOperation<RequestSignUpOTPRequest, RequestSignUpOTPResponse> {

    @Autowired
    private AppUserDao appUserDao;

    @Autowired
    private FamilyMemberDao familyMemberDao;

    @Autowired
    private OtpVerificationDao otpVerificationDao;

    @Autowired
    private AppProperties appProperties;

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
        checkValuePresent(opRequest.getEmailId(), "Email Id");
        boolean isEmailMandatory = true;
        EmailValidator.validateEmail(opRequest.getEmailId(), isEmailMandatory);
    }

    @Override
    public RequestSignUpOTPResponse doExecute(RequestSignUpOTPRequest opRequest) throws OperationException {
        String emailId = opRequest.getEmailId().toLowerCase().trim();
        // Check if the email is already registered
        if(appUserDao.isEmailRegistered(emailId)) {
            throw new ValidationException("Email Id is already registered. Please use a different email.");
        }

        // Make sure member exists in family member table with this email
        if(!familyMemberDao.isMemberExistsForEmail(emailId)) {
            throw new ValidationException("Email Id " + emailId + " is not allowed to signup, please contact administrator.");
        }

        // Fetch the existing OTP information if any
        OtpVerification otpInformation = otpVerificationDao.getOtpInformation(emailId, OTPType.SIGN_UP);
        log.info("OTP Verification info fetched from DB: {}", otpInformation);
        if(otpInformation != null) {
            if(OTPUtil.isOtpExpired(otpInformation)) {
                // If OTP exists but expired, move the existing OTP to history table and generate a new OTP
                otpInformation.setStatus(OTPStatus.Expired);
                otpVerificationDao.moveOtpToHistory(otpInformation);
                log.info("Record moved to history table for email: {} and OTP Type: {}", emailId, OTPType.SIGN_UP);
            } else {
                // Current OTP is still valid, we can resend the same OTP
                RequestSignUpOTPResponse response = new RequestSignUpOTPResponse();
                response.setEmailId(emailId);
                response.setRequestId(otpInformation.getRequestId());
                return response;
            }
        }


        String newOtp = OTPUtil.generateOTP();
        String requestId = OTPUtil.generateRequestId();
        Date currentDate = new Date();

        OtpVerification otpVerificationInfo = OtpVerification.builder()
                .emailId(emailId)
                .otpType(OTPType.SIGN_UP)
                .requestId(requestId)
                .hashOtpCode(PasswordUtil.hashPassword(newOtp)) // Store hashed OTP
                .status(OTPStatus.Pending)
                .expiryDate(new Date(currentDate.getTime() + OTP_EXPIRY_DURATION)) // OTP valid for 15 minutes
                .attempts(0)
                .createdDate(currentDate)
                .lastUpdatedDate(currentDate)
                .build();

        otpVerificationDao.insertOtpVerification(otpVerificationInfo);
        OTPUtil.sendOtpEmail(emailId, newOtp, OTPType.SIGN_UP, appProperties, emailSender);
        RequestSignUpOTPResponse response = new RequestSignUpOTPResponse();
        response.setEmailId(emailId);
        response.setRequestId(requestId); // Simulated request ID for OTP
        return response;
    }

}
