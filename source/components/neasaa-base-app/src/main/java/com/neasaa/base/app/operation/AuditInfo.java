package com.neasaa.base.app.operation;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuditInfo {
	
	private int createdBy;
	private Date createdDate;
	private int lastUpdatedBy;
	private Date lastUpdatedDate;
}
