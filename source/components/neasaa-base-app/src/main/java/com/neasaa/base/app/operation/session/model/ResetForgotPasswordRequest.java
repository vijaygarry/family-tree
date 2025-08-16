package com.neasaa.base.app.operation.session.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neasaa.base.app.operation.model.OperationRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetForgotPasswordRequest extends OperationRequest {
    private String emailId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String otp;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String newPassword;

    private String requestId;
}
