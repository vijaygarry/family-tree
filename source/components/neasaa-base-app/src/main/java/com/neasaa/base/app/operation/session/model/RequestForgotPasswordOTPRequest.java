package com.neasaa.base.app.operation.session.model;

import com.neasaa.base.app.operation.model.OperationRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestForgotPasswordOTPRequest extends OperationRequest {
    private String emailId;
}
