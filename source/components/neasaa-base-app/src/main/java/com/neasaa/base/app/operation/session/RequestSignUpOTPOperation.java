package com.neasaa.base.app.operation.session;

import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.base.app.operation.session.model.RequestSignUpOTPRequest;
import com.neasaa.base.app.operation.session.model.RequestSignUpOTPResponse;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.neasaa.base.app.operation.OperationNames.SIGN_UP_REQUEST_OTP;
import static com.neasaa.base.app.utils.ValidationUtils.checkValuePresent;

@Component("RequestSignUpOTPOperation")
@Scope("prototype")
public class RequestSignUpOTPOperation extends AbstractOperation<RequestSignUpOTPRequest, RequestSignUpOTPResponse> {

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
    }

    @Override
    public RequestSignUpOTPResponse doExecute(RequestSignUpOTPRequest opRequest) throws OperationException {
        // Check if the email is already registered
        // Make sure member exists in family member table with this email
        RequestSignUpOTPResponse response = new RequestSignUpOTPResponse();
        response.setEmailId(opRequest.getEmailId());
        response.setRequestId("1234-abcd-5678-efgh"); // Simulated request ID for OTP
        response.setLogonName(getLogonNameFromEmail(opRequest.getEmailId()));
        return response;
    }

    public String getLogonNameFromEmail (String emailId) {
        // This method can be used to set the logon name in the response
        // For simplicity, we will just return the email ID without domain part
        if (emailId == null || !emailId.contains("@")) {
            throw new ValidationException("Invalid email format provided.");
        }
        String[] parts = emailId.split("@");
        if (parts.length < 2) {
            throw new ValidationException("Invalid email format provided.");
        }
        return parts[0]; // Use the part before '@' as logon name
    }
}
