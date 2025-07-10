package com.neasaa.familytree.operation.family;

import static com.neasaa.base.app.utils.ValidationUtils.checkObjectPresent;
import static com.neasaa.base.app.utils.ValidationUtils.checkValuePresent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.AuditInfo;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.familytree.dao.pg.AddressDao;
import com.neasaa.familytree.dao.pg.FamilyDao;
import com.neasaa.familytree.entity.Address;
import com.neasaa.familytree.entity.Family;
import com.neasaa.familytree.operation.OperationNames;
import com.neasaa.familytree.operation.family.model.AddFamilyRequest;
import com.neasaa.familytree.operation.family.model.AddFamilyResponse;
import com.neasaa.familytree.operation.family.model.AddressDto;
import com.neasaa.familytree.utils.Constants;
import com.neasaa.familytree.utils.DataFormatter;
import com.neasaa.familytree.utils.FamilytreeValidationUtils;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component ("AddFamilyOperation")
@Scope("prototype")
public class AddFamilyOperation extends AbstractOperation<AddFamilyRequest, AddFamilyResponse>{

	@Autowired
	private AddressDao addressDao;
	
	@Autowired
	private FamilyDao familyDao;
	
	@Override
	public String getOperationName() {
		return OperationNames.ADD_FAMILY;
	}

	@Override
	public void doValidate(AddFamilyRequest opRequest) throws OperationException {
		if (opRequest == null) {
			throw new ValidationException("Invalid request provided.");
		}
		checkValuePresent(opRequest.getFamilyName(), "family name");
		checkValuePresent(opRequest.getGotra(), "gotra");
		checkObjectPresent(opRequest.getAddress(), "address");
		FamilytreeValidationUtils.validateAddress(opRequest.getAddress());
		
	}

	@Override
	public AddFamilyResponse doExecute(AddFamilyRequest opRequest) throws OperationException {
		log.info("Adding family");
		Address familyAddress = getAddressFromRequest(opRequest);
		int addressId = addressDao.addAddress(familyAddress);
		familyAddress.setAddressId(addressId);
		int familyId = familyDao.addFamily(getFamilyFromRequest(opRequest, familyAddress));
		AddFamilyResponse response = AddFamilyResponse.builder().familyName(opRequest.getFamilyName()).familyId(familyId).build();
		response.setOperationMessage(String.format("%s family added successfully !!!", opRequest.getFamilyName()));
		return response;
	}
	
	private Address getAddressFromRequest (AddFamilyRequest opRequest) {
		AddressDto inputAddress = opRequest.getAddress();
		AuditInfo auditInfo = getAuditInfo();
		return Address.builder()
				.addressLine1(inputAddress.getAddressLine1())
				.addressLine2(inputAddress.getAddressLine2())
				.addressLine3(inputAddress.getAddressLine3())
				.city(inputAddress.getCity())
				.district(inputAddress.getDistrict())
				.state(inputAddress.getState())
				.postalCode(inputAddress.getPostalCode())
				.country(inputAddress.getCountry())
				.createdBy(auditInfo.getCreatedBy())
				.createdDate(auditInfo.getCreatedDate())
				.lastUpdatedBy(auditInfo.getLastUpdatedBy())
				.lastUpdatedDate(auditInfo.getLastUpdatedDate())
				.build();
	}
	
	private Family getFamilyFromRequest (AddFamilyRequest opRequest, Address familyAddress) {
		AuditInfo auditInfo = getAuditInfo();
		String phoneNumber = DataFormatter.formatPhoneNumber(opRequest.getPhone());
		String familyRegion = DataFormatter.getRegion(familyAddress);
		String NO_HEAD_OF_FAMILY = null;
		return Family.builder()
				.familyName(opRequest.getFamilyName())
				.gotra(opRequest.getGotra())
				.addressId(familyAddress.getAddressId())
				.region(familyRegion)
				.phone(phoneNumber)
				.isPhoneWhatsappRegistered(opRequest.isPhoneWhatsappRegistered())
				.email(opRequest.getEmail())
				//TODO: When adding head of family, this field will be set to the head of family name.
				.familyDisplayName(DataFormatter.getFamilyDisplayName(NO_HEAD_OF_FAMILY, opRequest.getFamilyName(), familyRegion))
				.active(true)
				.familyImage(Constants.DEFALT_FAMILY_IMAGE)
				.imageLastUpdated(auditInfo.getCreatedDate())
				.createdBy(auditInfo.getCreatedBy())
				.createdDate(auditInfo.getCreatedDate())
				.lastUpdatedBy(auditInfo.getLastUpdatedBy())
				.lastUpdatedDate(auditInfo.getLastUpdatedDate())
				.build();
	}

}
