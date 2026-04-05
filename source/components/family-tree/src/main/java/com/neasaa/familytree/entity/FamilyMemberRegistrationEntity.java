/*
 * Copyright (c) 2018- 2021
 */

package com.neasaa.familytree.entity;

import com.neasaa.base.app.entity.BaseEntity;
import java.io.Serial;
import java.time.LocalDate;
import java.util.Date;

import com.neasaa.familytree.enums.Gender;
import com.neasaa.familytree.enums.MaritalStatus;
import com.neasaa.familytree.enums.Month;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilyMemberRegistrationEntity extends BaseEntity {

  @Serial private static final long serialVersionUID = 1L;

  private int memberRequestId;
  private int familyRequestId;
  private short samajId;
  private boolean headOfFamily;
  private String firstName;
  private String firstNameInHindi;
  private Gender gender;
  private Short birthDay;
  private Month birthMonth;
  private short birthYear;
  private MaritalStatus maritalStatus;
  private LocalDate weddingDate;
  private String phone;
  private String email;
  private boolean addressSameAsFamily;
  private String educationDetails;
  private String occupation;
  private Date createdDate;
  private Integer memberId;
  private String relationshipType;
  private int relatedMemberId;
  private FamilyMemberRegistrationEntity relatedMember;
}