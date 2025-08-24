/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.base.app.entity;

import com.neasaa.base.app.enums.OTPStatus;
import com.neasaa.base.app.enums.OTPType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

import java.io.Serial;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerification extends BaseEntity {

	@Serial
    private static final long serialVersionUID = 1755989330874L;

	private String emailId;
	private OTPType otpType;
	private String hashOtpCode;
	private String requestId;
	private OTPStatus status;
	private Date verifiedAt;
	private Date expiryDate;
	private int attempts;
	private Date lastAttemptDate;
	private Date createdDate;
	private Date lastUpdatedDate;


}
