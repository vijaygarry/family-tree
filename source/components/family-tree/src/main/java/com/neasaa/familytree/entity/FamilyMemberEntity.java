/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.familytree.entity;

import com.neasaa.familytree.enums.Month;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.time.LocalDate;
import java.util.Date;

import com.neasaa.base.app.entity.BaseEntity;
import com.neasaa.familytree.enums.MaritalStatus;
import com.neasaa.familytree.enums.Gender;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilyMemberEntity extends BaseEntity {

	@Serial
    private static final long serialVersionUID = 1748919840065L;

	private int memberId;
	private int familyId;
	private String logonName;
	private boolean headOfFamily;
	private String firstName;
	private String firstNameInHindi;
	private String lastName;
	private String maidenLastName;
	private String nickName;
	private String nickNameInHindi;
	private Gender gender;
	private Short birthDay;
	private Month birthMonth;
	private Short birthYear;
	private MaritalStatus maritalStatus;
	private LocalDate weddingDate;
	private LocalDate dateOfDeath;
	private String phone;
	private boolean isPhoneWhatsappRegistered;
	private String email;
	private boolean addressSameAsFamily;
	private int memberAddressId;

	private String educationDetails;
	private String occupation;
	private String hobby;
	private String memberSearchText;
	private String profileImage;
	private String profileImageThumbnail;
	private Date imageLastUpdated;
	private int createdBy;
	private Date createdDate;
	private int lastUpdatedBy;
	private Date lastUpdatedDate;

	public boolean isAlive () {
		return this.dateOfDeath == null;
	}

	public FamilyMemberEntity copyOf() {
		return FamilyMemberEntity.builder()
				.memberId(this.memberId)
				.familyId(this.familyId)
				.logonName(this.logonName)
				.headOfFamily(this.headOfFamily)
				.firstName(this.firstName)
				.firstNameInHindi(this.firstNameInHindi)
				.lastName(this.lastName)
				.maidenLastName(this.maidenLastName)
				.nickName(this.nickName)
				.nickNameInHindi(this.nickNameInHindi)
				.gender(this.gender)
				.birthDay(this.birthDay)
				.birthMonth(this.birthMonth)
				.birthYear(this.birthYear)
				.maritalStatus(this.maritalStatus)
				.weddingDate(this.weddingDate)
				.dateOfDeath(this.dateOfDeath)
				.phone(this.phone)
				.isPhoneWhatsappRegistered(this.isPhoneWhatsappRegistered)
				.email(this.email)
				.addressSameAsFamily(this.addressSameAsFamily)
				.memberAddressId(this.memberAddressId)
				.educationDetails(this.educationDetails)
				.occupation(this.occupation)
				.hobby(this.hobby)
				.memberSearchText(this.memberSearchText)
				.profileImage(this.profileImage)
				.profileImageThumbnail(this.profileImageThumbnail)
				.imageLastUpdated(this.imageLastUpdated)
				.createdBy(this.createdBy)
				.createdDate(this.createdDate)
				.lastUpdatedBy(this.lastUpdatedBy)
				.lastUpdatedDate(this.lastUpdatedDate)
				.build();
	}
}
