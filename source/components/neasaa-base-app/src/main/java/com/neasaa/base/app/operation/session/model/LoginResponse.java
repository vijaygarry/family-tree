package com.neasaa.base.app.operation.session.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.neasaa.base.app.operation.model.OperationResponse;
import com.neasaa.base.app.service.AppSessionUser;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class LoginResponse extends OperationResponse {

	private static final long serialVersionUID = -6106304964406712762L;
	
	@JsonIgnore
	private long userId;
	private String logonName;
	private String firstName;
	private String lastName;
	private String emailId;
	@JsonIgnore
	private long sessionId;
	@JsonIgnore
	private boolean sessionActive;
	@JsonIgnore
	private boolean authenticated;
	@JsonIgnore
	private Date sessionCreationTime;
	private Date lastAccessTime;
	
	@Setter
	@JsonIgnore	
	private AppSessionUser appSessionUser;

}
