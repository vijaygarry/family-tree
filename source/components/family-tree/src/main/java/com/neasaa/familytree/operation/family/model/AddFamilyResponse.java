package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationResponse;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AddFamilyResponse extends OperationResponse {

	private static final long serialVersionUID = 6634043188426161095L;
	
	private String familyName;
	private int familyId;
}
