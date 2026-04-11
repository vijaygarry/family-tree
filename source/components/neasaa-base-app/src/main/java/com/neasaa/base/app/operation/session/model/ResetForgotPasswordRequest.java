package com.neasaa.base.app.operation.session.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neasaa.base.app.operation.model.OperationRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetForgotPasswordRequest extends OperationRequest {
  private String loginName;
  private String otpChannel;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String otp;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String newPassword;

  private String requestId;

  @Override
  public void normalize() {
    if (this.loginName != null) {
      this.loginName = this.loginName.trim().toLowerCase();
    }
    if (this.otpChannel != null) {
      this.otpChannel = this.otpChannel.trim();
    }
    if (this.otp != null) {
      this.otp = this.otp.trim();
    }
    if (this.newPassword != null) {
      this.newPassword = this.newPassword.trim();
    }
    if (this.requestId != null) {
      this.requestId = this.requestId.trim();
    }
  }
}
