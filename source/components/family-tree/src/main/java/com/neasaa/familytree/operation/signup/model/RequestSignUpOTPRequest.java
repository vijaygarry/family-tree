package com.neasaa.familytree.operation.signup.model;

import com.neasaa.base.app.operation.model.OperationRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestSignUpOTPRequest extends OperationRequest {
  private String emailId;
}
