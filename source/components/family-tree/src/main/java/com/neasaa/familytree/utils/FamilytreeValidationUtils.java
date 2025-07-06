package com.neasaa.familytree.utils;

import static com.neasaa.base.app.utils.ValidationUtils.checkValuePresent;

import java.util.regex.Pattern;

import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.familytree.operation.family.model.AddressDto;

public class FamilytreeValidationUtils {
	
	private static final String PHONE_REGEX = "^(\\+)?[0-9 \\-]{10,15}$";
	
	public static void validateAddress (AddressDto address) {
		checkValuePresent(address.getAddressLine1(), "address line-1");
		checkValuePresent(address.getCity(), "city");
		checkValuePresent(address.getState(), "state");
		checkValuePresent(address.getCountry(), "country");
		checkValuePresent(address.getPostalCode(), "postal code");
		
		//TODO: For india, check the state is valid.
		//TODO: For india, check postal code is 6 digit
		//TODO: Address line1,2 and 3 should not be more than 100 chars
	}
	
	public static void validatePhoneNumber (String phoneNumber) {
		if(phoneNumber == null || phoneNumber.isEmpty()) {
			return;
		}
		if(!Pattern.matches(PHONE_REGEX, phoneNumber)) {
			throw new ValidationException ("Invalid phone number provided");
		}
	}
}
