package com.neasaa.base.app.operation.session;

import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.base.app.operation.session.model.RequestForgotPasswordOTPRequest;
import com.neasaa.base.app.operation.session.model.RequestForgotPasswordOTPResponse;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.neasaa.base.app.operation.OperationNames.FORGOT_PASSWORD_REQUEST_OTP;
import static com.neasaa.base.app.utils.ValidationUtils.checkValuePresent;

@Component("RequestForgotPasswordOTPOperation")
@Scope("prototype")
public class RequestForgotPasswordOTPOperation extends AbstractOperation<RequestForgotPasswordOTPRequest, RequestForgotPasswordOTPResponse> {

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
    }

    @Override
    public RequestForgotPasswordOTPResponse doExecute(RequestForgotPasswordOTPRequest opRequest) throws OperationException {
        RequestForgotPasswordOTPResponse response = new RequestForgotPasswordOTPResponse();
        response.setEmailId(opRequest.getEmailId());
        response.setRequestId("1234-abcd-5678-efgh"); // Simulated request ID for OTP
        return response;
    }
}
