package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationResponse;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddFamilyMemberResponse extends OperationResponse {

	private static final long serialVersionUID = -392941656189650246L;

	private int memberId;
	private String firstName;
	private String lastName;
}
