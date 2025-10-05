package com.neasaa.familytree.operation.family;

import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.AuditInfo;
import com.neasaa.base.app.operation.exception.AccessDeniedException;
import com.neasaa.base.app.operation.exception.InternalServerException;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.base.app.utils.EmailValidator;
import com.neasaa.familytree.dao.pg.AddressDao;
import com.neasaa.familytree.dao.pg.FamilyDao;
import com.neasaa.familytree.dao.pg.FamilyMemberDao;
import com.neasaa.familytree.entity.AddressEntity;
import com.neasaa.familytree.entity.FamilyEntity;
import com.neasaa.familytree.entity.FamilyMemberEntity;
import com.neasaa.familytree.operation.family.model.AddressDto;
import com.neasaa.familytree.operation.family.model.FamilyDetailsDto;
import com.neasaa.familytree.operation.family.model.UpdateFamilyDetailsRequest;
import com.neasaa.familytree.operation.family.model.UpdateFamilyDetailsResponse;
import com.neasaa.familytree.utils.DataFormatter;
import com.neasaa.familytree.utils.FamilytreeValidationUtils;
import com.neasaa.familytree.utils.SessionUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.neasaa.base.app.utils.ValidationUtils.checkObjectPresent;
import static com.neasaa.base.app.utils.ValidationUtils.checkValuePresent;
import static com.neasaa.base.app.utils.ValidationUtils.checkValueRange;
import static com.neasaa.familytree.operation.OperationNames.UPDATE_MY_FAMILY_DETAILS;

@Log4j2
@Component("UpdateFamilyDetailsOperation")
@Scope("prototype")
public class UpdateFamilyDetailsOperation extends AbstractOperation<UpdateFamilyDetailsRequest, UpdateFamilyDetailsResponse> {

    @Autowired
    private AddressDao addressDao;

    @Autowired
    private FamilyDao familyDao;

    @Autowired
    private FamilyMemberDao familyMemberDao;

    @Override
    public String getOperationName() {
        return UPDATE_MY_FAMILY_DETAILS;
    }

    @Override
    public void doValidate(UpdateFamilyDetailsRequest opRequest) throws OperationException {

        if (opRequest == null) {
            throw new ValidationException("Invalid request provided.");
        }
        opRequest.trimFields();
        checkObjectPresent(opRequest.getFamilyId(), "family Id");
        checkValueRange(opRequest.getFamilyId(), 1, Integer.MAX_VALUE, "family Id");

        // Family name is mandatory
        checkValuePresent(opRequest.getFamilyName(), "family name");
        FamilytreeValidationUtils.validateFamilyName(opRequest.getFamilyName());


        // Family name in Hindi is optional and should not be more than 50 characters
        FamilytreeValidationUtils.validateStringLength(opRequest.getFamilyNameInHindi(), "family name in Hindi", 100);

        // Gotra is optional and should not be more than 50 characters
        FamilytreeValidationUtils.validateStringLength(opRequest.getGotra(), "gotra", 100);

        // Phone is optional and should be a valid phone number
        FamilytreeValidationUtils.validatePhoneNumber(opRequest.getPhone());
        boolean isEmailMandatory = false; // Email is optional for updating family details
        // Email is optional and should be a valid email address
        EmailValidator.validateEmail(opRequest.getEmail(), isEmailMandatory);

        // Address is mandatory and should be validated
        checkObjectPresent(opRequest.getFamilyAddress(), "family address");
        FamilytreeValidationUtils.validateAddress(opRequest.getFamilyAddress());
    }

    @Override
    public UpdateFamilyDetailsResponse doExecute(UpdateFamilyDetailsRequest opRequest) throws OperationException {
        int familyId = opRequest.getFamilyId();

        // Check if this user is allowed to update family details
        if(!isFamilyUpdateAllowedForUser(familyId) ) {
            throw new AccessDeniedException("You are not allowed to update details of this family.");
        }


        AddressEntity newFamilyAddress = getAddressFromRequest(opRequest);

        // Check if family exists
        FamilyEntity familyEntity = familyDao.getFamilyByFamilyId(familyId);
        if(familyEntity == null) {
            throw new ValidationException("Family with Id " + familyId + " not found.");
        }
        AddressEntity existingAddress = addressDao.getAddressById(familyEntity.getAddressId());
        if(existingAddress == null) {
            log.info("Address not found for family with Id {}", familyId);
            throw new InternalServerException("Internal exception occurred, please contact administrator.");
        }
        newFamilyAddress.setAddressId(existingAddress.getAddressId());
        boolean addressUpdated = !existingAddress.equals(newFamilyAddress);
        if(addressUpdated) {
            log.info("Address updated for family with Id {}, updating address record", familyId);
            // Update existing address record
            addressDao.updateAddress(newFamilyAddress);
        }
        FamilyMemberEntity headOfFamily = familyMemberDao.getHeadOfFamilyByFamilyId(familyId);
        // Create history record with audit details
        FamilyEntity newFamilyEntity = getFamilyFromRequest(opRequest, headOfFamily, newFamilyAddress);

        familyDao.updateFamily(newFamilyEntity, getAuditInfo());
        newFamilyEntity.setAddress(newFamilyAddress);
        UpdateFamilyDetailsResponse response = UpdateFamilyDetailsResponse.builder()
                .familyDetails(FamilyDetailsDto.fromFamilyDBEntity(newFamilyEntity, headOfFamily == null ? "" :headOfFamily.getFirstName(), true))
                .build();

        response.setOperationMessage(String.format("Family %s details updated successfully !!!", opRequest.getFamilyName()));
        return response;
    }

    private AddressEntity getAddressFromRequest (UpdateFamilyDetailsRequest opRequest) {
        AddressDto inputAddress = opRequest.getFamilyAddress();
        AuditInfo auditInfo = getAuditInfo();
        return AddressEntity.builder()
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

    private FamilyEntity getFamilyFromRequest (UpdateFamilyDetailsRequest opRequest, FamilyMemberEntity headOfFamily, AddressEntity familyAddress) {
        AuditInfo auditInfo = getAuditInfo();
        String phoneNumber = DataFormatter.formatPhoneNumber(opRequest.getPhone());
        String familyRegion = DataFormatter.getRegion(familyAddress);
        String emailId = (opRequest.getEmail() != null) ? opRequest.getEmail().toLowerCase() : null;
        String familyName = DataFormatter.capitalizeFirstLetter(opRequest.getFamilyName());
        FamilyEntity familyEntity = FamilyEntity.builder()
                .familyId(opRequest.getFamilyId())
                .familyName(familyName)
                .familyNameInHindi(opRequest.getFamilyNameInHindi())
                .gotra(opRequest.getGotra())
                .addressId(familyAddress.getAddressId())
                .region(familyRegion)
                .phone(phoneNumber)
                .isPhoneWhatsappRegistered(opRequest.isPhoneWhatsappRegistered())
                .email(emailId)
                .active(true)
                .lastUpdatedBy(auditInfo.getLastUpdatedBy())
                .lastUpdatedDate(auditInfo.getLastUpdatedDate())
                .build();
        familyEntity.setFamilysearchtext(DataFormatter.getFamilySearchString(familyEntity, headOfFamily, familyAddress));
        return familyEntity;
    }

    //TODO: This method is duplicate in several operations, move to a common utility class
    private boolean isFamilyUpdateAllowedForUser(int familyId) {
        if(getContext() == null || getContext().getAppSessionUser() == null) {
            log.info("Operation context or AppSessionUser is null, cannot check if family edit allowed for user");
            return false;
        }
        FamilyMemberEntity memberEntity = SessionUtils.getFamilyMemberFromSession( getContext().getAppSessionUser());
        if(memberEntity == null) {
            log.info("Family member not found in session, cannot check if family edit allowed for user");
            return false;
        }
        if(memberEntity.getFamilyId() != familyId) {
            // Only allowed to edit own family details.
            return false;
        }
        return isOperationAllowedForUser(UPDATE_MY_FAMILY_DETAILS);
    }

}
