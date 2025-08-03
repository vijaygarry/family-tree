package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationRequest;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.Date;

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
	private Date dateOfDeath;
	private String maritalStatus;
	private String weddingDate;
	private String educationDetails;
	private String occupation;
	private String workingAt;
	private String hobby;
	private String profileImage;
	private String profileImageThumbnail;
	private RelationshipDto relashinship;
}
