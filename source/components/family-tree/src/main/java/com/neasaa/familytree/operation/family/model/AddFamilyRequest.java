package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationRequest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddFamilyRequest extends OperationRequest {

	private static final long serialVersionUID = -6449791531999816806L;

	private String familyName;
	private String familyNameInHindi;
	private String gotra;
	private String phone;
	private boolean isPhoneWhatsappRegistered;
	private String email;
	private AddressDto address;
	
}
