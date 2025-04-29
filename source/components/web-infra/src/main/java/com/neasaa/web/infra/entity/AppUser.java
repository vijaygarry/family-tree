/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.web.infra.entity;

import lombok.Setter;
import lombok.Getter;
import java.util.Date;

@Getter
@Setter
public class AppUser extends BaseEntity {

	public static final long serialVersionUID = 1745893230059L;
	private int userId;
	private String logonName;
	private String hashPassword;
	private String firstName;
	private String lastName;
	private String emailId;
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
