package com.neasaa.familytree.utils;

import com.neasaa.familytree.entity.Address;
import com.neasaa.familytree.enums.IndianState;

public class DataFormatter {
	
	public static String formatPhoneNumber (String phoneNumber) {
		//TODO: if phone number is not null,format as following:
		// +91-912 345 6789
		// +1-123 456 7890
		if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
			return "";
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
		System.out.println(formatPhoneNumber("9123456789"));         // +91-912 345 6789
		System.out.println(formatPhoneNumber("+1-1234567890"));      // +1-123 456 7890
		System.out.println(formatPhoneNumber("001234567890"));       // +0-012 345 67890
		System.out.println(formatPhoneNumber("98765 43210"));        // +91-987 654 3210
	}

	public static boolean isIndianAddress (Address address) {
		if(address == null) {
			return true;
		}
		if(Constants.INDIA_COUNTRY.equalsIgnoreCase(address.getCountry())) {
			return true;
		}
		return false;
	}
	/**
	 * The region where the family resides.
    	- For families in **India**:  
      		`Region = City + State` (e.g., *Amravati, MH*)
    	- For families **abroad**:  
      		`Region = Country` (e.g., *USA*)
	 * @param address
	 * @return
	 */
	public static String getRegion (Address address) {
		if(address == null) {
			return null;
		}
		if(isIndianAddress(address)) {
			return address.getCity() + ", " + IndianState.getShortStateName(address.getState());
		}
		
		return address.getCountry();
	}
	
	/**
	 * Automatically derived by the app as:  
  	`[Head of Family Name] + [Region]`  
	 **Example**: *Bhagwatnarayan Garothaya – Amravati, MH*
	 * If Head of family does not exists, then use only family name.
	 * @return
	 */
	public static String getFamilyDisplayName (String headOfFamilyMemberName, String familyName, String region) {
		if(headOfFamilyMemberName == null) {
			return String.format("%s - %s", familyName, region);
		}
		return String.format("%s %s - %s", headOfFamilyMemberName, familyName, region);
	}
}
