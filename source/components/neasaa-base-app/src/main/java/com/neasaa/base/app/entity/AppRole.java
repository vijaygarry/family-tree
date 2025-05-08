/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.base.app.entity;

import lombok.Setter;
import lombok.Getter;
import java.util.Date;

@Getter
@Setter
public class AppRole extends BaseEntity {

	public static final long serialVersionUID = 1745893230079L;
	private String roleId;
	private String roledesc;
	private boolean enable;
	private int createdBy;
	private Date createdDate;
	private int lastupdatedBy;
	private Date lastupdatedDate;


}
