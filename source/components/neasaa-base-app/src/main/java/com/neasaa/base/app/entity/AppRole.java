/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.base.app.entity;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

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
	private List<String> operationIds;

	public boolean hasOperation (String operationId) {
		if(operationIds == null) {
			return false;
		}
		return operationIds.contains(operationId);
	}
}
