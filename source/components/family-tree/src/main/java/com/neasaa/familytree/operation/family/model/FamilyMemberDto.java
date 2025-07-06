package com.neasaa.familytree.operation.family.model;

import com.neasaa.familytree.entity.FamilyMember;
import com.neasaa.familytree.enums.Gender;
import com.neasaa.familytree.enums.MaritalStatus;
import com.neasaa.familytree.enums.Month;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FamilyMemberDto {
    private int memberId;
    private int familyId;
    private boolean headOfFamily;
    private String firstName;
    private String firstNameInHindi;
    private String lastName;
    private String maidenLastName;
    private String nickName;
    private String nickNameInHindi;
    private Gender gender;
    private short birthDay;
    private Month birthMonth;
    private short birthYear;
    private Date dateOfDeath;
    private String phone;
    private boolean isPhoneWhatsappRegistered;
    private String email;
    private String linkedinUrl;
    private boolean addressSameAsFamily;
    private AddressDto memberAddress;
    private MaritalStatus maritalStatus;
    private String educationDetails;
    private String occupation;
    private String workingAt;
    private String hobby;
    private String profileImage;
    private String profileImageThumbnail;

    private FamilyMemberDto spouse;
    private List<FamilyMemberDto> children;

    public static FamilyMemberDto getFamilyMemberDtoFromDBEntity(FamilyMember familyMember) {
        return FamilyMemberDto.builder()
                .memberId(familyMember.getMemberId())
                .familyId(familyMember.getFamilyId())
                .headOfFamily(familyMember.isHeadOfFamily())
                .firstName(familyMember.getFirstName())
                .firstNameInHindi(familyMember.getFirstNameInHindi())
                .lastName(familyMember.getLastName())
                .maidenLastName(familyMember.getMaidenLastName())
                .nickName(familyMember.getNickName())
                .nickNameInHindi(familyMember.getNickNameInHindi())
                .gender(familyMember.getGender())
                .birthDay(familyMember.getBirthDay())
                .birthMonth(familyMember.getBirthMonth())
                .birthYear(familyMember.getBirthYear())
                .dateOfDeath(familyMember.getDateOfDeath())
                .phone(familyMember.getPhone())
                .isPhoneWhatsappRegistered(familyMember.isPhoneWhatsappRegistered())
                .email(familyMember.getEmail())
                .linkedinUrl(familyMember.getLinkedinUrl())
                .addressSameAsFamily(familyMember.isAddressSameAsFamily())
//                .memberAddress(AddressDto.getAddressDtoFromEntity(familyMember.getMemberAddress()))
                .maritalStatus(familyMember.getMaritalStatus())
                .educationDetails(familyMember.getEducationDetails())
                .occupation(familyMember.getOccupation())
                .workingAt(familyMember.getWorkingAt())
                .hobby(familyMember.getHobby())
                .profileImage(familyMember.getProfileImage())
                .profileImageThumbnail(familyMember.getProfileImageThumbnail())
                .build();
    }

    public void addChild(FamilyMemberDto child) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(child);
    }

}
