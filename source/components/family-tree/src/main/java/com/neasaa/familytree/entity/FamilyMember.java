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
import com.neasaa.familytree.enums.MaritalStatus;
import com.neasaa.familytree.enums.Gender;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilyMember extends BaseEntity {

	public static final long serialVersionUID = 1748636415588L;
	private int memberId;
	private int familyId;
	private boolean headOfFamily;
	private String firstName;
	private String lastName;
	private String maidenLastName;
	private String nickName;
	private boolean addressSameAsFamily;
	private int memberAddressId;
	private String phone;
	private boolean isPhoneWhatsappRegistered;
	private String email;
	private String linkedinUrl;
	private Gender gender;
	private short birthDay;
	private short birthMonth;
	private short birthYear;
	private MaritalStatus maritalStatus;
	private String educationDetails;
	private String occupation;
	private String workingAt;
	private String hobby;
	private String profileImage;
	private String profileImageThumbnail;
	private Date imageLastUpdated;
	private int createdBy;
	private Date createdDate;
	private int lastUpdatedBy;
	private Date lastUpdatedDate;


}
