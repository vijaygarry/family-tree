package com.neasaa.familytree.utils;

import static com.neasaa.base.app.utils.ValidationUtils.checkValuePresent;

import com.neasaa.familytree.operation.family.model.AddressDto;

public class FamilytreeValidationUtils {
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
}
