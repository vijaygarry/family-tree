package com.neasaa.familytree.operation.family;

import com.neasaa.base.app.operation.AuditInfo;
import com.neasaa.base.app.operation.exception.AccessDeniedException;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.base.app.utils.EmailValidator;
import com.neasaa.familytree.entity.AddressEntity;
import com.neasaa.familytree.entity.FamilyEntity;
import com.neasaa.familytree.entity.FamilyMemberEntity;
import com.neasaa.familytree.enums.Gender;
import com.neasaa.familytree.enums.MaritalStatus;
import com.neasaa.familytree.enums.Month;
import com.neasaa.familytree.operation.family.model.AddressDto;
import com.neasaa.familytree.operation.family.model.MemberProfileDto;
import com.neasaa.familytree.operation.family.model.UpdateFamilyMemberProfileRequest;
import com.neasaa.familytree.operation.family.model.UpdateFamilyMemberProfileResponse;
import com.neasaa.familytree.utils.Constants;
import com.neasaa.familytree.utils.DataFormatter;
import com.neasaa.familytree.utils.FamilytreeValidationUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.neasaa.base.app.utils.ValidationUtils.checkObjectPresent;
import static com.neasaa.base.app.utils.ValidationUtils.checkValuePresent;
import static com.neasaa.base.app.utils.ValidationUtils.checkValueRange;
import static com.neasaa.familytree.operation.OperationNames.UPDATE_MY_FAMILY_MEMBER;
import static com.neasaa.familytree.utils.Constants.MISSING_BIRTH_DATE_VALUE;
import static com.neasaa.familytree.utils.DataFormatter.getFamilyMemberSearchString;
import static com.neasaa.familytree.utils.DataFormatter.parseISODateToLocalDate;
import static com.neasaa.familytree.utils.FamilytreeValidationUtils.validateBirthDate;
import static com.neasaa.familytree.utils.FamilytreeValidationUtils.validateStringLength;

@Log4j2
@Component("UpdateFamilyMemberProfileOperation")
@Scope("prototype")
public class UpdateFamilyMemberProfileOperation extends FamilyAbstractOperation<UpdateFamilyMemberProfileRequest, UpdateFamilyMemberProfileResponse> {

    @Override
    public String getOperationName() {
        return UPDATE_MY_FAMILY_MEMBER;
    }

    @Override
    public void doValidate(UpdateFamilyMemberProfileRequest opRequest) throws OperationException {
        if (opRequest == null) {
            throw new ValidationException("Invalid request provided.");
        }
        opRequest.trimFields();
        checkObjectPresent(opRequest.getFamilyId(), "family Id");
        checkObjectPresent(opRequest.getMemberId(), "member Id");
        checkValueRange(opRequest.getFamilyId(), 1, Integer.MAX_VALUE, "family id");
        checkValueRange(opRequest.getMemberId(), 1, Integer.MAX_VALUE, "member id");

        checkValuePresent(opRequest.getFirstName(), "first name");
        validateStringLength(opRequest.getFirstName(), "first name", 50);
        if(opRequest.getFirstNameInHindi() != null) {
            validateStringLength(opRequest.getFirstNameInHindi(), "first name in hindi", 50);
        }

        if(opRequest.getMaidenLastName() != null) {
            validateStringLength(opRequest.getMaidenLastName(), "maiden last name", 50);
        }
        if(opRequest.getNickName() != null) {
            validateStringLength(opRequest.getNickName(), "nick name", 50);
        }

        if(opRequest.getNickNameInHindi() != null) {
            validateStringLength(opRequest.getNickNameInHindi(), "nick name in hindi", 50);
        }

        EmailValidator.validateEmail(opRequest.getEmail(), false);
        if(opRequest.getPhone() != null) {
            FamilytreeValidationUtils.validatePhoneNumber(opRequest.getPhone());
        }

        checkValuePresent(opRequest.getGender(), "gender");
        if(Gender.getGenderByString(opRequest.getGender()) == null) {
            throw new ValidationException ("Invalid value for field gender");
        }
        validateBirthDate(opRequest.getBirthDay(), opRequest.getBirthMonth(), opRequest.getBirthYear());

        checkValuePresent(opRequest.getMaritalStatus(), "marital status");
        if(MaritalStatus.getMaritalStatus(opRequest.getMaritalStatus()) == null) {
            throw new ValidationException ("Invalid value for field marital status");
        }

        if(opRequest.getEducationDetails() != null) {
            validateStringLength(opRequest.getEducationDetails(), "education details", 200);
        }

        if(opRequest.getOccupation() != null) {
            validateStringLength(opRequest.getOccupation(), "occupation", 100);
        }

        if(!opRequest.isAddressSameAsFamily() ) {
            checkObjectPresent(opRequest.getMemberAddress(), "member address");
            FamilytreeValidationUtils.validateAddress(opRequest.getMemberAddress());
        }
    }

    @Override
    public UpdateFamilyMemberProfileResponse doExecute(UpdateFamilyMemberProfileRequest opRequest) throws OperationException {
        // Fetch existing family member details
        FamilyMemberEntity memberEntityFromDb = familyMemberDao.getMemberById(opRequest.getMemberId());
        if(memberEntityFromDb == null) {
            throw new ValidationException("No family member found for the given member id.");
        }

        // Make sure family id if right
        if(memberEntityFromDb.getFamilyId() != opRequest.getFamilyId()) {
            throw new ValidationException("No family member found for the given member id in this family.");
        }

        // Check if this user is allowed to update family member details
        if(!canLoggedInUserUpdateMember(memberEntityFromDb.getFamilyId())) {
            log.info("User is not allowed to update the member details for member id: {}", opRequest.getMemberId());
            throw new AccessDeniedException("You are not allowed to update the member details.");
        }

        int memberNewAddressId = memberEntityFromDb.getMemberAddressId();
        AddressEntity newAddressEntity = null;
        // Get the member address
        AddressEntity memberAddressFromDb = addressDao.getAddressById(memberEntityFromDb.getMemberAddressId());
        // If user selected address same as family, but existing address is different, then delete the existing address
        if(opRequest.isAddressSameAsFamily()) {
            memberNewAddressId = Constants.MEMBER_ADDRESS_SAME_AS_FAMILY_ADDRESS;
            // Address can not be deleted here, because still linked to member profile
        } else {
            newAddressEntity = getAddressFromRequest(opRequest);
            if(memberEntityFromDb.getMemberAddressId() == Constants.MEMBER_ADDRESS_SAME_AS_FAMILY_ADDRESS) {
                // create a new address record
                if(newAddressEntity != null) {
                    memberNewAddressId = addressDao.addAddress(newAddressEntity);
                    newAddressEntity.setAddressId(memberNewAddressId);
                }
            } else {
                // Compare address, if different
                if(newAddressEntity != null && !newAddressEntity.equals(memberAddressFromDb)) {
                    // update existing address record
                    newAddressEntity.setAddressId(memberEntityFromDb.getMemberAddressId());
                    addressDao.updateAddress(newAddressEntity);
                }
            }
        }


        // Update family member details
        FamilyMemberEntity newFamilyMemberEntity = updateFamilyMemberEntityWithRequestAttributes(memberEntityFromDb, opRequest, memberNewAddressId);
        // Build search text for member
        newFamilyMemberEntity.setMemberSearchText(getFamilyMemberSearchString(newFamilyMemberEntity, newAddressEntity));
        // Update profile in main table
        familyMemberDao.updateFamilyMember(newFamilyMemberEntity, getAuditInfo());

        // If head of family, update family search text too
        if(newFamilyMemberEntity.isHeadOfFamily()) {
            FamilyEntity familyEntity = familyDao.getFamilyByFamilyId(newFamilyMemberEntity.getFamilyId());
            familyDao.updateFamilyDisplayName(familyEntity, newFamilyMemberEntity, getAuditInfo());
        }

        AddressDto memberAddressDto = null;
        if(newAddressEntity != null) {
            memberAddressDto = AddressDto.getAddressDtoFromEntity(newAddressEntity);
        }

        MemberProfileDto memberProfile = MemberProfileDto.fromFamilyMemberDBEntity(newFamilyMemberEntity, memberAddressDto, null, true);

        UpdateFamilyMemberProfileResponse response = UpdateFamilyMemberProfileResponse.builder()
                .memberProfile(memberProfile)
                .build();
        // Delete the old member address, if member switch to same as family address
        // This can be done only after updating the member with new address.
        deleteAddressIfNotLinkedToMember(opRequest, memberEntityFromDb);

        response.setOperationMessage(String.format("Member %s %s updated successfully !!!", opRequest.getFirstName(), newFamilyMemberEntity.getLastName()));
        return response;
    }

    private void deleteAddressIfNotLinkedToMember (UpdateFamilyMemberProfileRequest opRequest, FamilyMemberEntity memberEntityFromDb) {
        if(opRequest.isAddressSameAsFamily()) {
            if(memberEntityFromDb.getMemberAddressId() != Constants.MEMBER_ADDRESS_SAME_AS_FAMILY_ADDRESS) {
                addressDao.deleteAddress(memberEntityFromDb.getMemberAddressId());
            }
        }
    }
    private FamilyMemberEntity updateFamilyMemberEntityWithRequestAttributes (FamilyMemberEntity memberEntityFromDb, UpdateFamilyMemberProfileRequest opRequest, int addressId) {
        AuditInfo auditInfo = getAuditInfo();
        String phoneNumber = DataFormatter.formatPhoneNumber(opRequest.getPhone());

        // Email id can be updated only if existing email is null or empty
        // i.e. once email id is set, it cannot be changed.
        if(opRequest.getEmail() != null && !opRequest.getEmail().isEmpty()) {
            if(memberEntityFromDb.getEmail() != null && !memberEntityFromDb.getEmail().isEmpty()
                    && !memberEntityFromDb.getEmail().equalsIgnoreCase(opRequest.getEmail())) {
                throw new ValidationException("You cannot change existing email id.");
            }
        }
        FamilyMemberEntity newMemberEntity = memberEntityFromDb.copyOf();

        newMemberEntity.setFirstName(opRequest.getFirstName());
        newMemberEntity.setFirstNameInHindi(opRequest.getFirstNameInHindi());
        newMemberEntity.setMaidenLastName(opRequest.getMaidenLastName());
        newMemberEntity.setNickName(opRequest.getNickName());
        newMemberEntity.setNickNameInHindi(opRequest.getNickNameInHindi());
        newMemberEntity.setPhone(phoneNumber);
        newMemberEntity.setPhoneWhatsappRegistered(opRequest.isPhoneWhatsappRegistered());
        newMemberEntity.setGender(Gender.getGenderByString(opRequest.getGender()));
        newMemberEntity.setMaritalStatus(MaritalStatus.getMaritalStatus(opRequest.getMaritalStatus()));
        newMemberEntity.setWeddingDate(parseISODateToLocalDate(opRequest.getWeddingDate()));
        short birthDay = MISSING_BIRTH_DATE_VALUE;
        if(opRequest.getBirthDay() != null) {;
            birthDay = opRequest.getBirthDay();
        }
        newMemberEntity.setBirthDay(birthDay);

        newMemberEntity.setBirthMonth(Month.fromName(opRequest.getBirthMonth()));
        newMemberEntity.setBirthYear(opRequest.getBirthYear());

        if(newMemberEntity.getEmail() == null || newMemberEntity.getEmail().isEmpty()) {
            newMemberEntity.setEmail(opRequest.getEmail());
        }
        newMemberEntity.setEducationDetails(opRequest.getEducationDetails());
        newMemberEntity.setOccupation(opRequest.getOccupation());
        newMemberEntity.setHobby(opRequest.getHobby());

        newMemberEntity.setAddressSameAsFamily(opRequest.isAddressSameAsFamily());
        newMemberEntity.setMemberAddressId(addressId);

        return newMemberEntity;


    }


    private AddressEntity getAddressFromRequest (UpdateFamilyMemberProfileRequest opRequest) {
        AddressDto inputAddress = opRequest.getMemberAddress();
        if(inputAddress ==null) {
            log.info("Member address is not provided, not creating address for member");
            return null;
        }

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

}
