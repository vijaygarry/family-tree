package com.neasaa.familytree.operation.signup.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neasaa.base.app.operation.model.OperationRequest;
import com.neasaa.base.app.utils.PhoneUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest extends OperationRequest {
  private String otpChannel;
  private String mobileNumber;
  private String emailId;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String otp;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;

  private String requestId;

  @Override
  public void normalize() {
    if(otpChannel != null) {
      otpChannel = otpChannel.trim();
    }
    if(mobileNumber != null) {
      mobileNumber = PhoneUtil.normalizePhoneNumber(mobileNumber);
    }
    if(emailId != null) {
      emailId = emailId.trim().toLowerCase();
    }
    if(otp != null) {
      otp = otp.trim();
    }
    if(password != null) {
      password = password.trim();
    }
  }

}
