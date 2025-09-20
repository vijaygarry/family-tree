/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.familytree.entity;

import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.Date;
import java.util.Objects;

import com.neasaa.base.app.entity.BaseEntity;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
@ToString
public class AddressEntity extends BaseEntity {

	@Serial
    private static final long serialVersionUID = 1748576601270L;
	private int addressId;
	private String addressLine1;
	private String addressLine2;
	private String addressLine3;
	private String city;
	private String district;
	private String state;
	private String postalCode;
	private String country;
	private int createdBy;
	private Date createdDate;
	private int lastUpdatedBy;
	private Date lastUpdatedDate;


	public boolean equals(AddressEntity otherAddress) {
		if (otherAddress == null ) {
			log.info("Other address is null, so address is not equal");
			return false;
		}
		if (this == otherAddress) {
			return true;
		}
		return Objects.equals(addressLine1, otherAddress.addressLine1) && Objects.equals(addressLine2, otherAddress.addressLine2)
				&& Objects.equals(addressLine3, otherAddress.addressLine3) && Objects.equals(city, otherAddress.city)
				&& Objects.equals(district, otherAddress.district) && Objects.equals(state, otherAddress.state)
				&& Objects.equals(postalCode, otherAddress.postalCode) && Objects.equals(country, otherAddress.country);
	}
}
