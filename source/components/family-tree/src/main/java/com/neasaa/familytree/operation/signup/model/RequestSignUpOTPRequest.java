package com.neasaa.familytree.operation.signup.model;

import com.neasaa.base.app.operation.model.OperationRequest;
import com.neasaa.familytree.utils.DataFormatter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestSignUpOTPRequest extends OperationRequest {
  private String otpChannel;
  private String mobileNumber;
  private String emailId;

  public void trimFields() {
    if(otpChannel != null) {
      otpChannel = otpChannel.trim();
    }
    if(mobileNumber != null) {
      mobileNumber = DataFormatter.formatPhoneNumberForDBStorage(mobileNumber);
    }
    if(emailId != null) {
      emailId = emailId.trim().toLowerCase();
    }
  }
}
