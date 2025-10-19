package com.neasaa.familytree.operation.family.model;

import static com.neasaa.familytree.utils.DataFormatter.getISOFormatDate;

import com.neasaa.familytree.entity.FamilyMemberEntity;
import com.neasaa.familytree.enums.Gender;
import com.neasaa.familytree.enums.MaritalStatus;
import java.util.Date;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberProfileDto {
  private int memberId;
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
  private AddressDto familyAddress;
  private String phone;
  private boolean isPhoneWhatsappRegistered;
  private String email;
  private String linkedinUrl;
  private Gender gender;
  private Short birthDay;
  private String birthMonth;
  private short birthYear;
  private String dateOfDeath;
  private MaritalStatus maritalStatus;
  private String weddingDate;
  private String educationDetails;
  private String occupation;
  private String workingAt;
  private String hobby;
  private String profileImage;
  private String profileImageThumbnail;
  private Date lastUpdatedDate;

  private boolean canUpdateMember;

  public static MemberProfileDto fromFamilyMemberDBEntity(
      FamilyMemberEntity familyMember,
      AddressDto memberAddress,
      AddressDto familyAddress,
      boolean canUpdateMember) {
    Short birthDay;
    if (familyMember.getBirthDay() != null && familyMember.getBirthDay() > 0) {
      ;
      birthDay = familyMember.getBirthDay();
    } else {
      birthDay = null;
    }

    return MemberProfileDto.builder()
        .memberId(familyMember.getMemberId())
        .familyId(familyMember.getFamilyId())
        .headOfFamily(familyMember.isHeadOfFamily())
        .firstName(familyMember.getFirstName())
        .firstNameInHindi(familyMember.getFirstNameInHindi())
        .lastName(familyMember.getLastName())
        .maidenLastName(familyMember.getMaidenLastName())
        .nickName(familyMember.getNickName())
        .nickNameInHindi(familyMember.getNickNameInHindi())
        .addressSameAsFamily(familyMember.isAddressSameAsFamily())
        .memberAddress(memberAddress)
        .familyAddress(familyAddress)
        .phone(familyMember.getPhone())
        .isPhoneWhatsappRegistered(familyMember.isPhoneWhatsappRegistered())
        .email(familyMember.getEmail())
        .gender(familyMember.getGender())
        .birthDay(birthDay)
        .birthMonth(familyMember.getBirthMonth().getShortMonthName())
        .birthYear(familyMember.getBirthYear())
        .dateOfDeath(getISOFormatDate(familyMember.getDateOfDeath()))
        .maritalStatus(familyMember.getMaritalStatus())
        .weddingDate(getISOFormatDate(familyMember.getWeddingDate()))
        .educationDetails(familyMember.getEducationDetails())
        .occupation(familyMember.getOccupation())
        .hobby(familyMember.getHobby())
        .profileImage(familyMember.getProfileImage())
        .profileImageThumbnail(familyMember.getProfileImageThumbnail())
        .canUpdateMember(canUpdateMember)
        .build();
  }
}
