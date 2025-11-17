package com.neasaa.familytree.operation.signup.model;

import com.neasaa.base.app.operation.model.OperationResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestSignUpOTPResponse extends OperationResponse {
  private String otpChannel;
  private String mobileNumber;
  private String emailId;
  private String requestId;
}
