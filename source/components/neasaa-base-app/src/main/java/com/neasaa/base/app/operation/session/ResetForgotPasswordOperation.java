package com.neasaa.base.app.operation.session;

import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.base.app.operation.model.EmptyOperationResponse;
import com.neasaa.base.app.operation.session.model.ResetForgotPasswordRequest;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.neasaa.base.app.operation.OperationNames.RESET_FORGOT_PASSWORD;
import static com.neasaa.base.app.utils.ValidationUtils.checkValuePresent;

@Component("ResetForgotPasswordOperation")
@Scope("prototype")
public class ResetForgotPasswordOperation extends AbstractOperation<ResetForgotPasswordRequest, EmptyOperationResponse> {

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
    }

    @Override
    public EmptyOperationResponse doExecute(ResetForgotPasswordRequest opRequest) throws OperationException {
        if (!opRequest.getRequestId().equalsIgnoreCase("1234-abcd-5678-efgh")) {
            throw new ValidationException("Invalid request ID provided.");
        }
        if (!opRequest.getOtp().equalsIgnoreCase("123456")) {
            throw new ValidationException("Invalid OTP provided.");
        }
        if (opRequest.getNewPassword() == null || opRequest.getNewPassword().length() < 6) {
            throw new ValidationException("New password must be at least 6 characters long.");
        }
        return new EmptyOperationResponse("Password reset successfully !!!");
    }
}