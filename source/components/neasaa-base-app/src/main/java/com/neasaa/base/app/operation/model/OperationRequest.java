package com.neasaa.base.app.operation.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class OperationRequest implements Serializable {

	private static final long serialVersionUID = -3732306981452292314L;
	
	@JsonIgnore
	public abstract String getAuditString();

}
