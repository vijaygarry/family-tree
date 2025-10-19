package com.neasaa.familytree.utils;

import static com.neasaa.base.app.utils.ValidationUtils.checkObjectPresent;
import static com.neasaa.base.app.utils.ValidationUtils.checkValuePresent;
import static com.neasaa.base.app.utils.ValidationUtils.checkValueRange;

import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.familytree.enums.Month;
import com.neasaa.familytree.operation.family.model.AddressDto;
import java.time.Year;
import java.util.regex.Pattern;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class FamilytreeValidationUtils {

  private static final String PHONE_REGEX = "^(\\+)?[0-9 \\-]{10,15}$";
  private static final String FAMILY_NAME_REGEX = "^[a-zA-Z .-]+$";

  // Address line regex allows alphabets, numbers, space, comma, dot, hyphen and hindi characters
  private static final String ADDRESS_LINE_REGEX = "^[a-zA-Z0-9 ,.-\\u0900-\\u097F]+$";
  private static final String CITY_REGEX = "^[a-zA-Z .-\\u0900-\\u097F]+$";
  private static final String STATE_REGEX = "^[a-zA-Z .-\\u0900-\\u097F]+$";
  private static final String COUNTRY_REGEX = "^[a-zA-Z .-\\u0900-\\u097F]+$";
  private static final String POSTAL_CODE_REGEX =
      "^[a-zA-Z0-9]{3,8}$"; // Allowing 3 to 8 digit postal codes

  public static void validateFamilyName(String familyName) {
    checkValuePresent(familyName, "family name");
    validateStringLength(familyName, "family name", 100);
    if (!Pattern.matches(FAMILY_NAME_REGEX, familyName)) {
      throw new ValidationException(
          "Invalid character in family name, should contain only alphabets, space");
    }
  }

  public static void validateAddress(AddressDto address) {
    checkValuePresent(address.getAddressLine1(), "address line-1");
    checkValuePresent(address.getCity(), "city");
    checkValuePresent(address.getState(), "state");
    checkValuePresent(address.getCountry(), "country");
    checkValuePresent(address.getPostalCode(), "postal code");

    validateStringLength(address.getAddressLine1(), "address line-1", 100);
    validateStringLength(address.getAddressLine2(), "address line-2", 100);
    validateStringLength(address.getAddressLine3(), "address line-3", 100);
    validateStringLength(address.getCity(), "city", 75);
    validateStringLength(address.getDistrict(), "district", 75);
    validateStringLength(address.getState(), "state", 50);
    validateStringLength(address.getCountry(), "country", 70);
    validateStringLength(address.getPostalCode(), "postal code", 8);

    validateStringWithRegex(address.getAddressLine1(), "address line-1", ADDRESS_LINE_REGEX);
    validateStringWithRegex(address.getAddressLine2(), "address line-2", ADDRESS_LINE_REGEX);
    validateStringWithRegex(address.getAddressLine3(), "address line-3", ADDRESS_LINE_REGEX);
    validateStringWithRegex(address.getCity(), "city", CITY_REGEX);
    validateStringWithRegex(address.getDistrict(), "district", CITY_REGEX);
    validateStringWithRegex(address.getState(), "state", STATE_REGEX);
    validateStringWithRegex(address.getCountry(), "country", COUNTRY_REGEX);
    validateStringWithRegex(address.getPostalCode(), "postal code", POSTAL_CODE_REGEX);
  }

  public static void validateStringLength(String value, String fieldName, int maxLength) {
    if (value != null && value.length() > maxLength) {
      throw new ValidationException(
          fieldName + " should not be more than " + maxLength + " characters");
    }
  }

  public static void validateStringWithRegex(String value, String fieldName, String regex) {
    if (value != null && !value.isEmpty() && !Pattern.matches(regex, value)) {
      throw new ValidationException("Invalid " + fieldName + " provided");
    }
  }

  public static void validatePhoneNumber(String phoneNumber) {
    if (phoneNumber == null || phoneNumber.isEmpty()) {
      return;
    }
    String phone = phoneNumber.trim();
    // replace all spaces and hyphens
    phone = phone.replaceAll("[ \\-]", "");

    if (!Pattern.matches(PHONE_REGEX, phone)) {
      throw new ValidationException("Invalid phone number provided");
    }
  }

  public static void validateBirthDate(Short day, String month, Short year) {

    log.info("Input birth date: {}/{}/{}", day, month, year);
    if (day != null) {
      checkValueRange(day.intValue(), 1, 31, "birth day");
    }

    checkObjectPresent(month, "birth month");
    // Check if month is valid
    Month monthEnum = Month.fromName(month);
    if (monthEnum == null) {
      throw new ValidationException("Invalid value for field birth month");
    }

    checkObjectPresent(year, "birth year");
    int currentYear = Year.now().getValue();
    checkValueRange(year.intValue(), 1900, currentYear, "birth year");
    if (day != null) {
      // Check if day is valid for the given month and year
      boolean isLeapYear = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
      int maxDays = monthEnum.getMaxDaysInMonth(isLeapYear);
      if (day > maxDays) {
        throw new ValidationException("Invalid value for field birth day");
      }
    }
  }
}
