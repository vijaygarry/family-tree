package com.neasaa.base.app.constant;

public class AppConstants {

  public static final String DEFAULT_ROLE_ON_SIGNUP = "FAMILY_ADMIN_ROLE";

  // This is used as placeholder for user id during signup
  public static final int SYSTEM_USER_ID = 0;

  // DB Operation constants
  // Operation type in history table when main table record is updated
  public static final String UPDATE_OPERATION = "UPDATE";

  // Operation type in history table when main table record is deleted
  public static final String DELETE_OPERATION = "DELETE";

  public static final String EMAIL_OTP_CHANNEL = "email";
  public static final String MOBILE_OTP_CHANNEL = "mobile";
}
