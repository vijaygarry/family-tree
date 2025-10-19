package com.neasaa.familytree.operation.family;

import static com.neasaa.base.app.utils.ValidationUtils.checkObjectPresent;
import static com.neasaa.base.app.utils.ValidationUtils.checkValuePresent;
import static com.neasaa.base.app.utils.ValidationUtils.checkValueRange;
import static com.neasaa.familytree.utils.Constants.MISSING_BIRTH_DATE_VALUE;
import static com.neasaa.familytree.utils.DataFormatter.parseISODateToLocalDate;
import static com.neasaa.familytree.utils.FamilytreeValidationUtils.validateBirthDate;
import static com.neasaa.familytree.utils.FamilytreeValidationUtils.validateStringLength;

import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.AuditInfo;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.base.app.utils.EmailValidator;
import com.neasaa.familytree.constants.ImageConstants;
import com.neasaa.familytree.dao.pg.AddressDao;
import com.neasaa.familytree.dao.pg.FamilyDao;
import com.neasaa.familytree.dao.pg.FamilyMemberDao;
import com.neasaa.familytree.dao.pg.MemberRelationshipDao;
import com.neasaa.familytree.entity.AddressEntity;
import com.neasaa.familytree.entity.FamilyEntity;
import com.neasaa.familytree.entity.FamilyMemberEntity;
import com.neasaa.familytree.entity.MemberRelationshipEntity;
import com.neasaa.familytree.enums.Gender;
import com.neasaa.familytree.enums.MaritalStatus;
import com.neasaa.familytree.enums.Month;
import com.neasaa.familytree.enums.RelationshipType;
import com.neasaa.familytree.operation.OperationNames;
import com.neasaa.familytree.operation.family.model.AddFamilyMemberRequest;
import com.neasaa.familytree.operation.family.model.AddFamilyMemberResponse;
import com.neasaa.familytree.operation.family.model.AddressDto;
import com.neasaa.familytree.operation.family.model.RelationshipDto;
import com.neasaa.familytree.utils.Constants;
import com.neasaa.familytree.utils.DataFormatter;
import com.neasaa.familytree.utils.FamilytreeValidationUtils;
import com.neasaa.familytree.utils.RelationshipUtils;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Log4j2
@Component("AddFamilyMemberOperation")
@Scope("prototype")
public class AddFamilyMemberOperation
    extends AbstractOperation<AddFamilyMemberRequest, AddFamilyMemberResponse> {

  @Autowired private FamilyDao familyDao;

  @Autowired private FamilyMemberDao familyMemberDao;

  @Autowired private AddressDao addressDao;

  @Autowired private MemberRelationshipDao memberRelationshipDao;

  @Override
  public String getOperationName() {
    return OperationNames.ADD_MEMBER_TO_MY_FAMILY;
  }

  @Override
  public void doValidate(AddFamilyMemberRequest opRequest) throws OperationException {
    if (opRequest == null) {
      throw new ValidationException("Invalid request provided.");
    }
    opRequest.trimFields();
    checkObjectPresent(opRequest.getFamilyId(), "family Id");
    checkValueRange(opRequest.getFamilyId(), 1, Integer.MAX_VALUE, "family id");

    checkValuePresent(opRequest.getFirstName(), "first name");
    validateStringLength(opRequest.getFirstName(), "first name", 50);
    if (opRequest.getFirstNameInHindi() != null) {
      validateStringLength(opRequest.getFirstNameInHindi(), "first name in hindi", 50);
    }

    if (opRequest.getMaidenLastName() != null) {
      validateStringLength(opRequest.getMaidenLastName(), "maiden last name", 50);
    }
    if (opRequest.getNickName() != null) {
      validateStringLength(opRequest.getNickName(), "nick name", 50);
    }
    if (opRequest.getNickNameInHindi() != null) {
      validateStringLength(opRequest.getNickNameInHindi(), "nick name in hindi", 50);
    }

    EmailValidator.validateEmail(opRequest.getEmail(), false);
    if (opRequest.getPhone() != null) {
      FamilytreeValidationUtils.validatePhoneNumber(opRequest.getPhone());
    }

    checkValuePresent(opRequest.getGender(), "gender");
    if (Gender.getGenderByString(opRequest.getGender()) == null) {
      throw new ValidationException("Invalid value for field gender");
    }

    validateBirthDate(opRequest.getBirthDay(), opRequest.getBirthMonth(), opRequest.getBirthYear());

    checkValuePresent(opRequest.getMaritalStatus(), "marital status");
    if (MaritalStatus.getMaritalStatus(opRequest.getMaritalStatus()) == null) {
      throw new ValidationException("Invalid value for field marital status");
    }

    if (opRequest.getEducationDetails() != null) {
      validateStringLength(opRequest.getEducationDetails(), "education details", 200);
    }

    if (opRequest.getOccupation() != null) {
      validateStringLength(opRequest.getOccupation(), "occupation", 100);
    }

    if (!opRequest.isAddressSameAsFamily()) {
      checkObjectPresent(opRequest.getMemberAddress(), "member address");
      FamilytreeValidationUtils.validateAddress(opRequest.getMemberAddress());
    }

    if (!opRequest.isHeadOfFamily()) {
      if (opRequest.getRelationship() == null) {
        throw new ValidationException("Relationship is required this family member");
      }
      RelationshipType relationshipType =
          RelationshipType.getRelationshipType(opRequest.getRelationship().getRelationshipType());
      if (relationshipType == null) {
        throw new ValidationException("Invalid relationship type provided");
      }
      checkObjectPresent(opRequest.getRelationship().getMemberId(), "member Id");
      checkValueRange(opRequest.getRelationship().getMemberId(), 1, Integer.MAX_VALUE, "member id");
      checkObjectPresent(opRequest.getRelationship().getMemberName(), "member name");
    }
  }

  @Override
  public AddFamilyMemberResponse doExecute(AddFamilyMemberRequest opRequest)
      throws OperationException {
    log.info("Adding family member");
    FamilyEntity family = familyDao.getFamilyByFamilyId(opRequest.getFamilyId());
    if (family == null) {
      log.info("Family not found for family id {}", opRequest.getFamilyId());
      throw new ValidationException("Family not found");
    }

    // Fetch list of family members
    // TODO: Do we need all the members or only HOF should be sufficient.
    List<FamilyMemberEntity> familyMembers =
        familyMemberDao.allMembersForFamily(opRequest.getFamilyId());
    validateHeadOfFamilyValue(opRequest, familyMembers);

    AddressEntity newAddressEntity = null;
    int memberNewAddressId = Constants.MEMBER_ADDRESS_SAME_AS_FAMILY_ADDRESS;
    if (!opRequest.isAddressSameAsFamily()) {
      newAddressEntity = getAddressFromRequest(opRequest);
      if (newAddressEntity != null) {
        memberNewAddressId = addressDao.addAddress(newAddressEntity);
        newAddressEntity.setAddressId(memberNewAddressId);
      }
    }

    FamilyMemberEntity newMemberFromDb =
        familyMemberDao.addFamilyMember(
            getFamilyMemberFromRequest(opRequest, family, memberNewAddressId));
    if (newMemberFromDb.isHeadOfFamily()) {
      familyDao.updateFamilyDisplayName(family, newMemberFromDb, getAuditInfo());
    }

    // If not head of family, then add relationship
    if (!opRequest.isHeadOfFamily()) {
      updateRelationships(opRequest, newMemberFromDb);
    }

    AddFamilyMemberResponse response =
        AddFamilyMemberResponse.builder()
            .firstName(opRequest.getFirstName())
            .lastName(family.getFamilyName())
            .memberId(newMemberFromDb.getMemberId())
            .build();
    response.setOperationMessage(
        String.format(
            "Member %s %s added successfully !!!",
            opRequest.getFirstName(), family.getFamilyName()));
    return response;
  }

  private void validateHeadOfFamilyValue(
      AddFamilyMemberRequest opRequest, List<FamilyMemberEntity> allFamilyMembers)
      throws ValidationException {

    boolean familyAlreadyHasHeadOfFamily = false;
    if (allFamilyMembers != null && !allFamilyMembers.isEmpty()) {
      for (FamilyMemberEntity member : allFamilyMembers) {
        if (member.isHeadOfFamily()) {
          familyAlreadyHasHeadOfFamily = true;
          break;
        }
      }
    }

    if (opRequest.isHeadOfFamily()) {
      if (familyAlreadyHasHeadOfFamily) {
        throw new ValidationException(
            "Family can have only one head of family. Family already has head of family.");
      }
    } else {
      if (!familyAlreadyHasHeadOfFamily) {
        throw new ValidationException("First add head of family");
      }
    }
  }

  private AddressEntity getAddressFromRequest(AddFamilyMemberRequest opRequest) {
    AddressDto inputAddress = opRequest.getMemberAddress();
    if (inputAddress == null) {
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

  private FamilyMemberEntity getFamilyMemberFromRequest(
      AddFamilyMemberRequest opRequest, FamilyEntity family, int addressId) {
    AuditInfo auditInfo = getAuditInfo();
    String phoneNumber = DataFormatter.formatPhoneNumber(opRequest.getPhone());
    String emailId =
        opRequest.getEmail() != null ? opRequest.getEmail().toLowerCase().trim() : null;
    short birthDay = MISSING_BIRTH_DATE_VALUE;
    if (opRequest.getBirthDay() != null) {
      ;
      birthDay = opRequest.getBirthDay();
    }

    return FamilyMemberEntity.builder()
        .familyId(family.getFamilyId())
        .headOfFamily(opRequest.isHeadOfFamily())
        .firstName(opRequest.getFirstName())
        .firstNameInHindi(opRequest.getFirstNameInHindi())
        .lastName(family.getFamilyName())
        .maidenLastName(opRequest.getMaidenLastName())
        .nickName(opRequest.getNickName())
        .nickNameInHindi(opRequest.getNickNameInHindi())
        .addressSameAsFamily(opRequest.isAddressSameAsFamily())
        .memberAddressId(addressId)
        .phone(phoneNumber)
        .isPhoneWhatsappRegistered(opRequest.isPhoneWhatsappRegistered())
        .email(emailId)
        .gender(Gender.getGenderByString(opRequest.getGender()))
        .birthDay(birthDay)
        .birthMonth(Month.fromName(opRequest.getBirthMonth()))
        .birthYear(opRequest.getBirthYear())
        .dateOfDeath(parseISODateToLocalDate(opRequest.getDateOfDeath()))
        .maritalStatus(MaritalStatus.getMaritalStatus(opRequest.getMaritalStatus()))
        .weddingDate(parseISODateToLocalDate(opRequest.getWeddingDate()))
        .educationDetails(opRequest.getEducationDetails())
        .occupation(opRequest.getOccupation())
        .hobby(opRequest.getHobby())
        .profileImage(getDefaultImagePath(opRequest))
        .profileImageThumbnail(getDefaultThumbnailImagePath(opRequest))
        .imageLastUpdated(auditInfo.getCreatedDate())
        .createdBy(auditInfo.getCreatedBy())
        .createdDate(auditInfo.getCreatedDate())
        .lastUpdatedBy(auditInfo.getLastUpdatedBy())
        .lastUpdatedDate(auditInfo.getLastUpdatedDate())
        .build();
  }

  private void updateRelationships(
      AddFamilyMemberRequest opRequest, FamilyMemberEntity newMemberFromDb) {

    RelationshipDto relationship = opRequest.getRelationship();
    FamilyMemberEntity relatedMember = familyMemberDao.getMemberById(relationship.getMemberId());
    if (relatedMember == null) {
      throw new ValidationException(
          "Member whom adding relationship is missing " + relationship.getMemberName());
    }
    log.info(
        "Input Relationship {}({})'s {} is {} ",
        relationship.getMemberName(),
        relationship.getMemberId(),
        relationship.getRelationshipType(),
        opRequest.getFirstName());

    // Update relationship related member info (i.e. details for new member adding)
    relationship.setRelatedMemberId(newMemberFromDb.getMemberId());
    relationship.setRelatedMemberName(newMemberFromDb.getFirstName());

    // Add relationship
    MemberRelationshipEntity relationshipEntity = relationship.entityFromDto();
    relationshipEntity = RelationshipUtils.normalizeRelationship(relationshipEntity, relatedMember);
    log.info(
        "Member {}'s {} is {}",
        relationship.getMemberId(),
        relationship.getRelationshipType(),
        relationship.getRelatedMemberId());
    log.info("Adding relationship");

    // TODO: Check gender compatibility for relationship

    // TODO: For related member check the following:
    // 1. If Wife/Husband of related member is already present, then do not add husband or wife.
    // 2. If father/mother already exists, then do not add son/daughter.

    // TODO: Check the relationShipType to add
    // If Mother/Father then fetch mother and father of related member and validate
    // If Husband/Wife then fetch husband and wife of related member and validate

    MemberRelationshipEntity memberRelationshipFromDb =
        memberRelationshipDao.getRelationshipBetweenMembers(
            relationship.getMemberId(), relationship.getRelatedMemberId());
    if (memberRelationshipFromDb == null) {
      log.info(
          "Adding {}'s {} is {}",
          relationship.getMemberId(),
          relationship.getRelationshipType(),
          relationship.getRelatedMemberId());
      memberRelationshipDao.addMemberRelationship(relationshipEntity, getAuditInfo());
    } else {
      log.info(
          "Relationship already exists between {} and {}",
          relationship.getMemberId(),
          relationship.getRelatedMemberId());
      if (memberRelationshipFromDb.getRelationshipType()
          != relationshipEntity.getRelationshipType()) {
        log.info(
            "Relationship between member {} and {} is expected as {}, but found {}",
            relationship.getMemberId(),
            relationship.getRelatedMemberId(),
            relationship.getRelationshipType(),
            memberRelationshipFromDb.getRelationshipType());
      }
    }
  }

  private static String getDefaultImagePath(AddFamilyMemberRequest opRequest) {
    Gender gender = Gender.getGenderByString(opRequest.getGender());
    short birthDay = MISSING_BIRTH_DATE_VALUE;
    if (opRequest.getBirthDay() != null) {
      birthDay = opRequest.getBirthDay();
    }
    int memberAge =
        DataFormatter.getMemberAgeInYears(
            birthDay, Month.fromName(opRequest.getBirthMonth()), opRequest.getBirthYear());
    if (gender == Gender.Female) {
      if (memberAge < 20) {
        return ImageConstants.DEFAULT_KID_GIRL_IMAGE;
      } else if (memberAge < 60) {
        MaritalStatus memberMaritalStatus =
            MaritalStatus.getMaritalStatus(opRequest.getMaritalStatus());
        if (memberMaritalStatus == MaritalStatus.Single
            || memberMaritalStatus == MaritalStatus.Engaged) {
          return ImageConstants.DEFAULT_UNMARRIED_GIRL_IMAGE;
        }
        return ImageConstants.DEFAULT_MARRIED_WOMAN_IMAGE;
      } else {
        return ImageConstants.DEFAULT_OLD_WOMAN_IMAGE;
      }
    } else {
      if (memberAge < 20) {
        return ImageConstants.DEFAULT_KID_BOY_IMAGE;
      } else if (memberAge < 60) {
        return ImageConstants.DEFAULT_MAN_IMAGE;
      } else {
        return ImageConstants.DEFAULT_OLD_MAN_IMAGE;
      }
    }
  }

  private static String getDefaultThumbnailImagePath(AddFamilyMemberRequest opRequest) {
    return getDefaultImagePath(opRequest);
  }
}
