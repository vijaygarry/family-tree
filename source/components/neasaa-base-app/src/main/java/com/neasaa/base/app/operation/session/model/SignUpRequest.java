package com.neasaa.base.app.operation.session.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neasaa.base.app.operation.model.OperationRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest extends OperationRequest {
    private String emailId;
    private String logonName;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String otp;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;


    private String requestId;
}