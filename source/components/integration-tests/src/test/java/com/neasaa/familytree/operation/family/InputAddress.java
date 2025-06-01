package com.neasaa.familytree.operation.family;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class InputAddress {
	private String addressLine1;
	private String addressLine2;
	private String addressLine3;
	private String city;
	private String district;
	private String state;
	private String postalCode;
	private String country;
	
	public static InputAddress getValidTestAddress () {
		return InputAddress.builder()
			.addressLine1("Budhwara Chowk, Matakhidki Road")
			.addressLine2("Address line 2")
			.addressLine3("Address line 3")
			.city("Amravati")
			.district("Amravati")
			.state("Maharastra")
			.country("India")
			.postalCode("444601")
			.build();
			
	}
}
