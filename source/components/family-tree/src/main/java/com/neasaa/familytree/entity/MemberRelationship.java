/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.familytree.entity;

import lombok.Setter;
import lombok.Getter;
import java.util.Date;

import com.neasaa.base.app.entity.BaseEntity;

@Getter
@Setter
public class MemberRelationship extends BaseEntity {

	public static final long serialVersionUID = 1748576601294L;
	private int memberId;
	private String relationshipType;
	private int relatedMemberId;
	private int createdBy;
	private Date createdDate;
	private int lastUpdatedBy;
	private Date lastUpdatedDate;


}
