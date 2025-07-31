package com.neasaa.familytree.operation.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Builder
@Getter
@Setter
@ToString
public class AddFamilyMemberRequest {

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
    private boolean addressSameAsFamily;
    private InputAddress memberAddress;
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
    private String educationDetails;
    private String occupation;
    private String workingAt;
    private String hobby;
    private String profileImage;
    private InputRelationship relashinship;
}
