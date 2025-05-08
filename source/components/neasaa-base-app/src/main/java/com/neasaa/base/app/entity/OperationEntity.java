/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.base.app.entity;

import lombok.Setter;
import lombok.Getter;
import java.util.Date;

@Getter
@Setter
public class OperationEntity extends BaseEntity {

	public static final long serialVersionUID = 1745893230070L;
	private String operationId;
	private String description;
	private String beanName;
	private boolean authorizationRequired;
	private boolean auditRequired;
	private String authorizationType;
	private boolean active;
	private int createdBy;
	private Date createdDate;
	private int lastupdatedBy;
	private Date lastupdatedDate;


}
