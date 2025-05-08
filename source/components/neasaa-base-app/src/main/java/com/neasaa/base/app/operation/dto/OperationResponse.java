package com.neasaa.base.app.operation.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class OperationResponse implements Serializable {

	public static final int SUCCESS_CODE = 0;
	public static final int USER_CANCEL_OPERATION = 1;
	
	private static final long serialVersionUID = 4309029842159428542L;

	
	/**
	 * Integer return code for operation result.
	 */
	private int responseCode;
	
	/**
	 * Operation message on completion of operation. This may be success response, information, etc.
	 * Operation message can be return in case of success as well as error.
	 */
	private String operationMessage;
	
	public OperationResponse (String message) {
		this.operationMessage = message;
	}
	
	public OperationResponse (int responseCode) {
		this.responseCode = responseCode;
	}
	
	public abstract String getAuditString();
}
