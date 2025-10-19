/*
 * Copyright (c) 2018- 2021
 */

package com.neasaa.base.app.entity;

import java.io.Serial;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpVerificationHistory extends BaseEntity {

  @Serial private static final long serialVersionUID = 1755989330888L;

  private long seqId;
  private String emailId;
  private String otpType;
  private String hashOtpCode;
  private String requestId;
  private String status;
  private Date verifiedAt;
  private Date expiryDate;
  private int attempts;
  private Date lastAttemptDate;
  private Date createdDate;
  private Date lastUpdatedDate;
}
