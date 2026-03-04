/*
 * Copyright (c) 2018- 2021
 */

package com.neasaa.familytree.entity;

import com.neasaa.base.app.entity.BaseEntity;
import com.neasaa.familytree.enums.Gender;
import com.neasaa.familytree.enums.MaritalStatus;
import com.neasaa.familytree.enums.Month;
import java.io.Serial;
import java.time.LocalDate;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
public class FamilyMemberEntity extends BaseEntity {

  @Serial private static final long serialVersionUID = 1748919840065L;

  private int memberId;
  private int familyId;
  private int samajId;
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
  private boolean isPhoneVerified;
  private boolean isPhoneWhatsappRegistered;
  private String email;
  private boolean isEmailVerified;
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

  public boolean isAlive() {
    return this.dateOfDeath == null;
  }

  /**
   * Updates the member search text used for searching members.
   * Format for member search string: First Name, Last Name, First Name in Hindi, Maiden Last Name, Nick Name,
   * Phone, Email, Region
   * @param region - Member region.
   */
  public void updateSearchText(String region) {
    StringBuilder searchTextBuilder = new StringBuilder();
    if (this.firstName != null) {
      searchTextBuilder.append(this.firstName).append(" ");
    }
    if (this.lastName != null) {
      searchTextBuilder.append(this.lastName).append(" ");
    }
    if (this.firstNameInHindi != null) {
      searchTextBuilder.append(this.firstNameInHindi).append(" ");
    }
    if(this.maidenLastName != null) {
      searchTextBuilder.append(this.maidenLastName).append(" ");
    }
    if (this.nickName != null) {
      searchTextBuilder.append(this.nickName).append(" ");
    }
    if(this.phone != null) {
      searchTextBuilder.append(this.phone).append(" ");
    }
    if(this.email != null) {
      searchTextBuilder.append(this.email).append(" ");
    }
    log.info("Region for member {} is {}", this.memberId, region);
    if(region != null) {
      searchTextBuilder.append(region).append(" ");
    }
    this.memberSearchText = searchTextBuilder.toString();
  }

  public FamilyMemberEntity copyOf() {
    return FamilyMemberEntity.builder()
        .memberId(this.memberId)
        .familyId(this.familyId)
        .samajId(this.samajId)
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
        .isPhoneVerified(this.isPhoneVerified)
        .isPhoneWhatsappRegistered(this.isPhoneWhatsappRegistered)
        .email(this.email)
        .isEmailVerified(this.isEmailVerified)
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
