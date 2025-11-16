package com.neasaa.base.app.utils;

import com.neasaa.base.app.operation.exception.ValidationException;
import java.util.regex.Pattern;

public class EmailValidator {
  // RFC 5322 simplified pattern (not too strict, not too loose)
  private static final Pattern EMAIL_PATTERN =
      Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);

  public static void validateEmail(String email, boolean isMandatory) {
    if (email == null || email.isBlank()) {
      if (isMandatory) {
        throw new ValidationException("Email cannot be empty");
      } else {
        return; // Optional and not provided, so valid
      }
    }

    // 1. Max length check (commonly 254 chars for full email)
    if (email.length() > 254) {
      throw new ValidationException("Email must not exceed 254 characters");
    }

    // 2. Must contain exactly one '@'
    int atIndex = email.indexOf('@');
    if (atIndex == -1) {
      throw new ValidationException("Email must contain '@'");
    }
    if (atIndex != email.lastIndexOf('@')) {
      throw new ValidationException("Email must not contain more than one '@'");
    }

    String localPart = email.substring(0, atIndex);
    String domainPart = email.substring(atIndex + 1);

    // 3. Local part checks
    if (localPart.isEmpty()) {
      throw new ValidationException("Local part (before '@') cannot be empty");
    }
    if (localPart.length() > 64) {
      throw new ValidationException("Local part (before '@') must not exceed 64 characters");
    }
    if (!localPart.matches("^[A-Za-z0-9._%+-]+$")) {
      throw new ValidationException("Local part contains invalid symbols");
    }

    // 4. Domain part checks
    if (domainPart.isEmpty()) {
      throw new ValidationException("Domain part (after '@') cannot be empty");
    }
    if (domainPart.length() > 64) {
      throw new ValidationException("Domain part (after '@') must not exceed 64 characters");
    }
    if (!domainPart.matches("^[A-Za-z0-9.-]+$")) {
      throw new ValidationException("Domain part contains invalid symbols");
    }
    if (domainPart.startsWith("-") || domainPart.endsWith("-")) {
      throw new ValidationException("Domain must not start or end with a hyphen");
    }
    if (!domainPart.contains(".")) {
      throw new ValidationException("Domain must contain at least one '.'");
    }
  }

  public static boolean isEmailId (String stringVal) {
    if(stringVal == null || stringVal.isEmpty()) {
      return false;
    }
    return EMAIL_PATTERN.matcher(stringVal).matches();
  }

}
