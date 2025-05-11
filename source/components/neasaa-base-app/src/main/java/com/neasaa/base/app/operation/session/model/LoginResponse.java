package com.neasaa.base.app.operation.session.model;

import java.util.Date;

import com.neasaa.base.app.operation.model.OperationResponse;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse extends OperationResponse {

	private static final long serialVersionUID = -6106304964406712762L;
	
	private String logonName;
	private String firstName;
	private String lastName;
	private String emailId;
	
	private long sessionId;
	private boolean sessionActive;
	private Date lastAccessTime;
	
	@Override
	public String getAuditString() {
		
		return null;
	}

}
