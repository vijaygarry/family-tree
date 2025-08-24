package com.neasaa.base.app.enums;

public enum OTPStatus {
    Pending, Verified, Expired;

    public static OTPStatus getOTPStatus (String status) {
        for (OTPStatus otpStatus : OTPStatus.values()) {
            if (otpStatus.name().equalsIgnoreCase(status)) {
                return otpStatus;
            }
        }
        return null;
    }
}
