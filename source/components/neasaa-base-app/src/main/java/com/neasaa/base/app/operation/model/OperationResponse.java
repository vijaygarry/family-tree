package com.neasaa.base.app.operation.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class OperationResponse implements Serializable {

	private static final long serialVersionUID = 4309029842159428542L;
	
	/**
	 * Operation message on completion of operation. This may be success response, information, etc.
	 * Operation message can be return in case of success as well as error.
	 */
	private String operationMessage;
	
	public OperationResponse (String message) {
		this.operationMessage = message;
	}
	
	public OperationResponse () {
		
	}
	
	public abstract String getAuditString();
}
