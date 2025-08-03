package com.neasaa.familytree.operation.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Builder
@Getter
@Setter
@ToString
public class ExcelFamilyMemberDetails {
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
    private String weddingDate;
    private String educationDetails;
    private String occupation;
    private String workingAt;
    private String hobby;
    private String profileImage;
//    private String fatherName;
//    private String motherName;
    private String spouseName;
    private List<String> childrenNamesList;
    private boolean belongsToOtherFamily;
    private InputRelationship relashinship;
    private int excelRowNumber;

    public AddFamilyMemberRequest toAddFamilyMemberRequest() {
        return AddFamilyMemberRequest.builder()
                .familyId(familyId)
                .headOfFamily(headOfFamily)
                .firstName(firstName)
                .firstNameInHindi(firstNameInHindi)
                .lastName(lastName)
                .maidenLastName(maidenLastName)
                .nickName(nickName)
                .nickNameInHindi(nickNameInHindi)
                .addressSameAsFamily(addressSameAsFamily)
                .memberAddress(memberAddress)
                .phone(phone)
                .isPhoneWhatsappRegistered(isPhoneWhatsappRegistered)
                .email(email)
                .linkedinUrl(linkedinUrl)
                .gender(gender)
                .birthDay(birthDay)
                .birthMonth(birthMonth)
                .birthYear(birthYear)
                .dateOfDeath(dateOfDeath)
                .maritalStatus(maritalStatus)
                .educationDetails(educationDetails)
                .occupation(occupation)
                .workingAt(workingAt)
                .hobby(hobby)
                .profileImage(profileImage)
                .relashinship(relashinship)
                .build();
    }
}
