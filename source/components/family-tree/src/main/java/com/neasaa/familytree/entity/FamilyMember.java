/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.familytree.entity;

import lombok.Setter;
import lombok.Getter;
import java.util.Date;

import com.neasaa.base.app.entity.BaseEntity;

@Getter
@Setter
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
	private String sex;
	private short birthDay;
	private short birthMonth;
	private short birthYear;
	private String maritalStatus;
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
