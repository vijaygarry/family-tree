/*
 * Copyright (c) 2018- 2021
 */

package com.neasaa.base.app.entity;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppConfig extends BaseEntity {

  public static final long serialVersionUID = 1745893230065L;
  private String configName;
  private String paramName;
  private String paramValue;
  private boolean enable;
  private short listOrderSeq;
  private int createdBy;
  private Date createdDate;
  private int lastUpdatedBy;
  private Date lastUpdatedDate;
}
