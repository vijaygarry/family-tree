/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.web.infra.entity;

import lombok.Setter;
import lombok.Getter;
import java.util.Date;

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
