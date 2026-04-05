/*
 * Copyright (c) 2018- 2021
 */

package com.neasaa.familytree.entity;

import com.neasaa.base.app.entity.BaseEntity;
import com.neasaa.familytree.enums.FamilyRegistrationStatus;
import java.io.Serial;
import java.util.Date;
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
public class FamilyRegistrationRequestEntity extends BaseEntity {

  @Serial private static final long serialVersionUID = 1L;

  private int familyRequestId;
  private short samajId;
  private String familyName;
  private String familyNameInHindi;
  private String gotra;
  private String addressLine1;
  private String addressLine2;
  private String addressLine3;
  private String city;
  private String district;
  private String state;
  private String postalCode;
  private String country;
  private String phone;
  private String email;
  private Date createdDate;
  private String clientInfo;
  private FamilyRegistrationStatus status;
  private int familyAddedBy;
  private Date dateAdded;
  private Integer familyId;
}