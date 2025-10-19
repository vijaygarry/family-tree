package com.neasaa.base.app.enums;

public enum OTPType {
  SIGN_UP,
  FORGOT_PASSWORD,
  DEVICE_AUTHENTICATION;

  public static OTPType getOTPType(String type) {
    for (OTPType otpType : OTPType.values()) {
      if (otpType.name().equalsIgnoreCase(type)) {
        return otpType;
      }
    }
    return null;
  }
}
