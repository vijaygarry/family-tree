package com.neasaa.base.app.operation.session.model;

import com.neasaa.base.app.operation.model.OperationResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestSignUpOTPResponse extends OperationResponse {
    private String emailId;
    private String requestId;
    private String logonName;
}
