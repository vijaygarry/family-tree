/*
 * Copyright (c) 2018- 2021
 */

package com.neasaa.base.app.entity;

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
public class AppUser extends BaseEntity {

  @Serial private static final long serialVersionUID = 1745893230059L;
  private int userId;
  private String logonName;
  private String hashPassword;
  private String firstName;
  private String lastName;
  private String emailId;
  private String phone;
  private String authenticationType;
  private String singleSignonId;
  private int invalidLoginAttempts;
  private Date lastLoginTime;
  private Date lastPasswordChangeTime;
  private String status;
  private int createdBy;
  private Date createdDate;
  private int lastUpdatedBy;
  private Date lastUpdatedDate;
}
