package com.neasaa.base.app.operation.session.model;

import com.neasaa.base.app.operation.model.OperationResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestForgotPasswordOTPResponse extends OperationResponse {
    private String emailId;
    private String requestId;
}
