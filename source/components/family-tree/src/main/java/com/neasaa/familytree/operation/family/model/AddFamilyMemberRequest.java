package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationRequest;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.time.LocalDate;

@Getter
@Setter
public class AddFamilyMemberRequest extends OperationRequest {

	@Serial
	private static final long serialVersionUID = -4292257590045882963L;
	
	private int familyId;
	private boolean headOfFamily;
	private String firstName;
	private String firstNameInHindi;
	private String lastName;
	private String maidenLastName;
	private String nickName;
	private String nickNameInHindi;
	private boolean addressSameAsFamily;
	private AddressDto memberAddress;
	private String phone;
	private boolean isPhoneWhatsappRegistered;
	private String email;
	private String linkedinUrl;
	private String gender;
	private Short birthDay;
	private String birthMonth;
	private Short birthYear;
	private String dateOfDeath;
	private String maritalStatus;
	private String weddingDate;
	private String educationDetails;
	private String occupation;
	private String workingAt;
	private String hobby;
	private String profileImage;
	private String profileImageThumbnail;
	private RelationshipDto relationship;

	public void trimFields() {
		if (firstName != null) firstName = firstName.trim();
		if (firstNameInHindi != null) firstNameInHindi = firstNameInHindi.trim();
		if (lastName != null) lastName = lastName.trim();
		if (maidenLastName != null) maidenLastName = maidenLastName.trim();
		if (nickName != null) nickName = nickName.trim();
		if (nickNameInHindi != null) nickNameInHindi = nickNameInHindi.trim();
		if( memberAddress != null) memberAddress.trimFields();
		if (phone != null) phone = phone.trim();
		if (email != null) email = email.trim();
		if (linkedinUrl != null) linkedinUrl = linkedinUrl.trim();
		if (gender != null) gender = gender.trim();
		if (birthMonth != null) birthMonth = birthMonth.trim();
		if(dateOfDeath != null) dateOfDeath = dateOfDeath.trim();
		if (maritalStatus != null) maritalStatus = maritalStatus.trim();
		if( weddingDate != null ) weddingDate = weddingDate.trim();
		if (educationDetails != null) educationDetails = educationDetails.trim();
		if (occupation != null) occupation = occupation.trim();
		if (workingAt != null) workingAt = workingAt.trim();
		if (hobby != null) hobby = hobby.trim();
		if (profileImage != null) profileImage = profileImage.trim();
		if (profileImageThumbnail != null) profileImageThumbnail = profileImageThumbnail.trim();
		if( relationship != null ) relationship.trimFields();
	}


}
