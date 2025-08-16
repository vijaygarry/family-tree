package com.neasaa.base.app.operation.session;

import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.base.app.operation.model.EmptyOperationResponse;
import com.neasaa.base.app.operation.session.model.SignUpRequest;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.neasaa.base.app.operation.OperationNames.SIGN_UP;
import static com.neasaa.base.app.utils.ValidationUtils.checkValuePresent;

@Component("SignUpOperation")
@Scope("prototype")
public class SignUpOperation extends AbstractOperation<SignUpRequest, EmptyOperationResponse> {

    @Override
    public String getOperationName() {
        return SIGN_UP;
    }

    @Override
    public void doValidate(SignUpRequest opRequest) throws OperationException {
        if (opRequest == null) {
            throw new ValidationException("Invalid request provided.");
        }
        checkValuePresent(opRequest.getEmailId(), "Email Id");
        checkValuePresent(opRequest.getOtp(), "One time password (OTP)");
        checkValuePresent(opRequest.getRequestId(), "Request Id");
        checkValuePresent(opRequest.getPassword(), "Password");
    }

    @Override
    public EmptyOperationResponse doExecute(SignUpRequest opRequest) throws OperationException {
        if (!opRequest.getRequestId().equalsIgnoreCase("1234-abcd-5678-efgh")) {
            throw new ValidationException("Invalid request ID provided.");
        }
        if (!opRequest.getOtp().equalsIgnoreCase("123456")) {
            throw new ValidationException("Invalid OTP provided.");
        }
        if (opRequest.getPassword() == null || opRequest.getPassword().length() < 6) {
            throw new ValidationException("Password must be at least 6 characters long.");
        }
        //Check if the email is already registered

        // Check if family member is already registered
        // Get first name, last name from member details
        // Update logon name in family member table

        // Create user in user table with password and other details

        return new EmptyOperationResponse("Sign Up completed successfully !!!");
    }


}
