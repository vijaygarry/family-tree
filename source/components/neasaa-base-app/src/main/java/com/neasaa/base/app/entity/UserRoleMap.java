/*
 * Copyright (c) 2018- 2021
 */

package com.neasaa.base.app.entity;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleMap extends BaseEntity {

  public static final long serialVersionUID = 1745893230090L;
  private int userId;
  private String roleId;
  private int createdBy;
  private Date createdDate;
  private int lastupdatedBy;
  private Date lastupdatedDate;
}
