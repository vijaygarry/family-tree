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
public class Family extends BaseEntity {

	public static final long serialVersionUID = 1748919840051L;
	private int familyId;
	private String familyName;
	private String familyNameInHindi;
	private String gotra;
	private int addressId;
	private String region;
	private String phone;
	private boolean isPhoneWhatsappRegistered;
	private String email;
	private String familyDisplayName;
	private boolean active;
	private String familyImage;
	private Date imageLastUpdated;
	private int createdBy;
	private Date createdDate;
	private int lastUpdatedBy;
	private Date lastUpdatedDate;


}
