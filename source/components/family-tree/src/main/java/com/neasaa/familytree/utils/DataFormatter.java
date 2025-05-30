package com.neasaa.familytree.utils;

import com.neasaa.familytree.entity.Address;

public class DataFormatter {
	
	public static String formatPhoneNumber (String phoneNumber) {
		//TODO: if phone number is not null,format as following:
		// +91-912 345 6789
		// +1-123 456 7890
		return phoneNumber;
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
			//TODO: State should be 2 digit string
			return address.getCity() + ", " + address.getState();
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
