package com.neasaa.familytree.utils;

import com.neasaa.familytree.entity.AddressEntity;
import com.neasaa.familytree.entity.FamilyEntity;
import com.neasaa.familytree.entity.FamilyMemberEntity;
import com.neasaa.familytree.enums.IndianState;
import com.neasaa.familytree.enums.Month;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class DataFormatter {

  private static final String WEDDING_DATE_FORMAT = "dd-MMM-yyyy";

  /**
   * Possible inputs
   * 5714843763, 571 484 3763, +1-571-484-3763, 001-571-484-3763, +91-9123456789, 091-9123456789
   * +1 (571) 484-3763, 0091 (912) 345-6789, +971 56 788 2525
   *
   * @param phoneNumber
   * @return - Formatted phone number for storing in db as phone number for user lookup.
   */
  public static String formatPhoneNumberForDBStorage (String phoneNumber) {
    // if phone number is not null,format as following:
    // country code without + followed by 10 digit number
    // 919123456789
    if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
      return null;
    }

    // Remove all non-digit characters
    String digits = phoneNumber.replaceAll("[^\\d]", "");
    if(digits.startsWith("0")) {
      // Remove leading zeros
      digits = digits.replaceFirst("^0+", "");
    }
    if (digits.length() <= 10) {
      // Assume India number and prefix india country code
      String localNumber = String.format("%010d", Long.parseLong(digits));
      return "91" + localNumber;
    } else {
      return digits;
    }
  }

  public static String formatPhoneNumberForUX(String phoneNumber) {
    // TODO: if phone number is not null,format as following:
    // +91-912 345 6789
    // +1-123 456 7890
    if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
      return null;
    }

    // Remove all non-digit characters
    String digits = phoneNumber.replaceAll("[^\\d]", "");

    if (digits.length() <= 10) {
      // Assume India number
      String localNumber = String.format("%010d", Long.parseLong(digits));
      return formatInternational("+91", localNumber);
    } else {
      // Extract country code (assume 1–3 digits), then format remaining
      String countryCode = digits.substring(0, digits.length() - 10);
      String localNumber = digits.substring(digits.length() - 10);
      return formatInternational("+" + countryCode, localNumber);
    }
  }

  private static String formatInternational(String countryCode, String localNumber) {
    String areaCode = localNumber.substring(0, 3);
    String middle = localNumber.substring(3, 6);
    String last = localNumber.substring(6, 10);
    return String.format("%s-%s %s %s", countryCode, areaCode, middle, last);
  }

  public static void main(String[] args) {
    System.out.println(formatPhoneNumberForUX("9123456789")); // +91-912 345 6789
    System.out.println(formatPhoneNumberForUX("+1-1234567890")); // +1-123 456 7890
    System.out.println(formatPhoneNumberForUX("001234567890")); // +0-012 345 67890
    System.out.println(formatPhoneNumberForUX("98765 43210")); // +91-987 654 3210
  }

  public static boolean isIndianAddress(AddressEntity address) {
    if (address == null) {
      return true;
    }
    return Constants.INDIA_COUNTRY.equalsIgnoreCase(address.getCountry());
  }

  /**
   * The region where the family resides. - For families in **India**: `Region = City + State`
   * (e.g., *Amravati, MH*) - For families **abroad**: `Region = State + Country` (e.g., *USA*)
   *
   * @param address
   * @return
   */
  public static String getRegion(AddressEntity address) {
    if (address == null) {
      return null;
    }
    if (isIndianAddress(address)) {
      return address.getCity() + ", " + IndianState.getShortStateName(address.getState());
    }

    return address.getState() + ", " + address.getCountry();
  }

  public static String getFamilySearchString(
      FamilyEntity family, FamilyMemberEntity familyHeadOfFamily, AddressEntity address) {
    if (family == null) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    sb.append(family.getFamilyName()).append(" ");
    if (family.getFamilyNameInHindi() != null && !family.getFamilyNameInHindi().isEmpty()) {
      sb.append(family.getFamilyNameInHindi()).append(" ");
    }
    if (familyHeadOfFamily != null && familyHeadOfFamily.getFirstName() != null) {
      sb.append(familyHeadOfFamily.getFirstName()).append(" ");
    }
    if (familyHeadOfFamily != null && familyHeadOfFamily.getFirstNameInHindi() != null) {
      sb.append(familyHeadOfFamily.getFirstNameInHindi()).append(" ");
    }
    if (address != null) {
      if (address.getCity() != null) {
        sb.append(address.getCity()).append(" ");
      }
      if (address.getState() != null) {
        sb.append(address.getState()).append(" ");
      }
      if (address.getCountry() != null) {
        sb.append(address.getCountry()).append(" ");
      }
    }

    return sb.toString();
  }

  public static String getFamilyMemberSearchString(
      FamilyMemberEntity familyMemberEntity, AddressEntity address) {
    if (familyMemberEntity == null) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    sb.append(familyMemberEntity.getFirstName()).append(" ");
    if (familyMemberEntity.getFirstNameInHindi() != null
        && !familyMemberEntity.getFirstNameInHindi().isEmpty()) {
      sb.append(familyMemberEntity.getFirstNameInHindi()).append(" ");
    }

    if (familyMemberEntity.getLastName() != null && !familyMemberEntity.getLastName().isEmpty()) {
      sb.append(familyMemberEntity.getLastName()).append(" ");
    }

    if (familyMemberEntity.getMaidenLastName() != null
        && !familyMemberEntity.getMaidenLastName().isEmpty()) {
      sb.append(familyMemberEntity.getMaidenLastName()).append(" ");
    }

    if (familyMemberEntity.getNickName() != null && !familyMemberEntity.getNickName().isEmpty()) {
      sb.append(familyMemberEntity.getNickName()).append(" ");
    }

    if (familyMemberEntity.getPhone() != null && !familyMemberEntity.getPhone().isEmpty()) {
      sb.append(familyMemberEntity.getPhone()).append(" ");
    }

    if (familyMemberEntity.getEmail() != null && !familyMemberEntity.getEmail().isEmpty()) {
      sb.append(familyMemberEntity.getEmail()).append(" ");
    }

    if (address != null) {
      if (address.getCity() != null) {
        sb.append(address.getCity()).append(" ");
      }
      if (address.getState() != null) {
        sb.append(address.getState()).append(" ");
      }
      if (address.getCountry() != null) {
        sb.append(address.getCountry()).append(" ");
      }
    }

    return sb.toString();
  }

  //	/**
  //	 * Automatically derived by the app as:
  //  	`[Head of Family Name] + [Region]`
  //	 **Example**: *Bhagwatnarayan Garothaya – Amravati, MH*
  //	 * If Head of family does not exists, then use only family name.
  //	 * @return
  //	 */
  //	public static String getFamilyDisplayName (String headOfFamilyMemberName, String familyName,
  // String region) {
  //		if(headOfFamilyMemberName == null) {
  //			return String.format("%s - %s", familyName, region);
  //		}
  //		return String.format("%s %s - %s", headOfFamilyMemberName, familyName, region);
  //	}

  public static String formatBirthDate(short day, Month month, short year) {
    if (month == null) {
      return "Year " + year;
    }
    if (day <= 0) {
      return String.format("%s-%s", month.getShortMonthName(), year);
    }
    return String.format("%s-%s-%s", day, month.getShortMonthName(), year);
  }

  public static int getYearFromDate(LocalDate localDate) {
    if (localDate == null) {
      return 0;
    }
    return localDate.getYear();
  }

  public static String getFormattedMemberAge(FamilyMemberEntity familyMemberEntity) {
    if (!familyMemberEntity.isAlive()) {
      return "("
          + familyMemberEntity.getBirthYear()
          + " - "
          + getYearFromDate(familyMemberEntity.getDateOfDeath())
          + ")";
    }
    return "("
        + getMemberAgeInYears(
            familyMemberEntity.getBirthDay(),
            familyMemberEntity.getBirthMonth(),
            familyMemberEntity.getBirthYear())
        + " years)";
  }

  public static String getISOFormatDate(LocalDate localDate) {
    if (localDate == null) {
      return null;
    }
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
    return localDate.format(formatter);
  }

  public static LocalDate parseISODateToLocalDate(String inputDate) {
    if (inputDate == null || inputDate.isEmpty()) {
      return null;
    }
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
    return LocalDate.parse(inputDate, formatter);
  }

  public static int getMemberAgeInYears(short day, Month month, short year) {
    LocalDate birthDate = null;

    if (month == null) {
      // Assume January 1st of the year
      birthDate = LocalDate.of(year, 1, 1);
    } else if (day <= 0) {
      // Month is provided but day is not, assume 1st of the month
      birthDate = LocalDate.of(year, month.getMonthNumber(), 1);
    } else {
      // Full date is provided
      birthDate = LocalDate.of(year, month.getMonthNumber(), day);
    }

    // Current date
    LocalDate today = LocalDate.now();
    // Calculate age
    return Period.between(birthDate, today).getYears();
  }

  public static String capitalizeFirstLetter(String value) {
    if (value == null || value.isEmpty()) {
      return value;
    }
    if (value.length() == 1) {
      return value.toUpperCase();
    }
    String trimValue = value.trim();
    return trimValue.substring(0, 1).toUpperCase() + trimValue.substring(1);
  }
}
