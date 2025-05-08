/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.base.app.entity;

import lombok.Setter;
import lombok.Getter;
import java.util.Date;

@Getter
@Setter
public class RoleOperationMap extends BaseEntity {

	public static final long serialVersionUID = 1745893230084L;
	private String roleId;
	private String operationId;
	private int createdBy;
	private Date createdDate;
	private int lastupdatedBy;
	private Date lastupdatedDate;


}
