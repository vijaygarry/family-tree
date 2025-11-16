package com.neasaa.base.app.utils;

import com.neasaa.base.app.operation.exception.ValidationException;

public class PhoneUtil {

    /**
     * Normalize phone number by removing all non-digit characters and format to country code and 10-digit phone number.
     * If contry code is missing, assumes India country code (91).
     *
     * Possible inputs
     * 5714843763, 571 484 3763, +1-571-484-3763, 001-571-484-3763, +91-9123456789, 091-9123456789
     * +1 (571) 484-3763, 0091 (912) 345-6789, +971 56 788 2525
     *
     * @param phoneNumber
     * @return - Formatted phone number for storing in db as phone number for user lookup.
     */
    public static String normalizePhoneNumber (String phoneNumber) {
        // if phone number is not null,format as following:
        // country code without + followed by 10-digit number
        // 919123456789
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return null;
        }

        // Remove all non-digit characters
        String normalizeNumber = phoneNumber.replaceAll("[^\\d]", "");
        if(normalizeNumber.startsWith("0")) {
            // Remove leading zeros
            normalizeNumber = normalizeNumber.replaceFirst("^0+", "");
        }
        if(normalizeNumber.length() < 10) {
            throw new ValidationException("Invalid phone number provided. Should be 10 normalizeNumber number.");
        }
        if (normalizeNumber.length() == 10) {
            return "91" + normalizeNumber;
        } else {
            return normalizeNumber;
        }
    }

    public static String formatPhoneNumber(String phoneNumber) {
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

}
