/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.familytree.entity;

import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

import com.neasaa.base.app.entity.BaseEntity;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address extends BaseEntity {

	public static final long serialVersionUID = 1748576601270L;
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


}
