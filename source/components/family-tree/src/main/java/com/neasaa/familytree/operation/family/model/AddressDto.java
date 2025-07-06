package com.neasaa.familytree.operation.family.model;

import com.neasaa.familytree.entity.Address;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
	
	private String addressLine1;
	private String addressLine2;
	private String addressLine3;
	private String city;
	private String district;
	private String state;
	private String postalCode;
	private String country;

	public static AddressDto getAddressDtoFromEntity(Address address) {
		if (address == null) {
			return null;
		}
		return AddressDto.builder()
				.addressLine1(address.getAddressLine1())
				.addressLine2(address.getAddressLine2())
				.addressLine3(address.getAddressLine3())
				.city(address.getCity())
				.district(address.getDistrict())
				.state(address.getState())
				.postalCode(address.getPostalCode())
				.country(address.getCountry())
				.build();
	}
}
