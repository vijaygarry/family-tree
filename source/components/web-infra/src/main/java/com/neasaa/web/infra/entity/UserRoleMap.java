/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.web.infra.entity;

import lombok.Setter;
import lombok.Getter;
import java.util.Date;

@Getter
@Setter
public class UserRoleMap extends BaseEntity {

	public static final long serialVersionUID = 1745893230090L;
	private int userId;
	private String roleId;
	private int createdBy;
	private Date createdDate;
	private int lastupdatedBy;
	private Date lastupdatedDate;


}
