package com.neasaa.familytree.operation.family;

import static com.neasaa.base.app.utils.ValidationUtils.checkObjectPresent;
import static com.neasaa.base.app.utils.ValidationUtils.checkValuePresent;
import static com.neasaa.base.app.utils.ValidationUtils.checkValueRange;
import static com.neasaa.familytree.utils.Constants.MISSING_BIRTH_DATE_VALUE;
import static com.neasaa.familytree.utils.FamilytreeValidationUtils.validateBirthDate;
import static com.neasaa.familytree.utils.FamilytreeValidationUtils.validateStringLength;

import com.neasaa.base.app.operation.AuditInfo;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.base.app.utils.EmailValidator;
import com.neasaa.familytree.constants.ImageConstants;
import com.neasaa.familytree.dao.pg.AddressDao;
import com.neasaa.familytree.dao.pg.FamilyDao;
import com.neasaa.familytree.dao.pg.FamilyMemberDao;
import com.neasaa.familytree.dao.pg.FamilyRegistrationRequestDao;
import com.neasaa.familytree.dao.pg.MemberRelationshipDao;
import com.neasaa.familytree.entity.AddressEntity;
import com.neasaa.familytree.entity.FamilyEntity;
import com.neasaa.familytree.entity.FamilyMemberEntity;
import com.neasaa.familytree.entity.FamilyMemberRegistrationEntity;
import com.neasaa.familytree.entity.FamilyRegistrationRequestEntity;
import com.neasaa.familytree.entity.MemberRelationshipEntity;
import com.neasaa.familytree.enums.FamilyRegistrationStatus;
import com.neasaa.familytree.enums.RelationshipType;
import com.neasaa.familytree.operation.family.model.AddressDto;
import com.neasaa.familytree.operation.family.model.ProcessFamilyRegistrationResponse;
import com.neasaa.familytree.operation.family.model.RelationshipDto;
import com.neasaa.familytree.utils.Constants;
import com.neasaa.familytree.utils.DataFormatter;
import com.neasaa.familytree.utils.FamilytreeValidationUtils;
import com.neasaa.familytree.utils.RelationshipUtils;
import com.neasaa.util.StringUtils;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Component
public class FamilyRegistrationApprovalUtil {

  @Autowired private FamilyRegistrationRequestDao familyRegistrationRequestDao;
  @Autowired private AddressDao addressDao;
  @Autowired private FamilyDao familyDao;
  @Autowired private FamilyMemberDao familyMemberDao;
  @Autowired private MemberRelationshipDao memberRelationshipDao;

  public ProcessFamilyRegistrationResponse approveRegistration(
      FamilyRegistrationRequestEntity familyRequest, AuditInfo auditInfo) throws OperationException {

    FamilyEntityWrapper familyEntityWrapper = FamilyEntityWrapper.getFamilyEntityWrapperInstance(familyRequest, auditInfo);
    familyEntityWrapper.validateFamilyAndAddress();

    List<FamilyMemberRegistrationEntity> memberRequestEntities =
        familyRegistrationRequestDao.getFamilyMemberRegistrationsByFamilyRequestId(familyRequest.getFamilyRequestId());
    if (memberRequestEntities == null || memberRequestEntities.isEmpty()) {
      throw new ValidationException("No members found for family registration request");
    }

    Map<Integer, FamilyMemberRegistrationEntity> memberRequestMap = memberRequestEntities.stream()
        .collect(Collectors.toMap(FamilyMemberRegistrationEntity::getMemberRequestId, reg -> reg));

    List<FamilyMemberEntityWrapper> familyMemberEntityWrappers = new ArrayList<>();
    for (FamilyMemberRegistrationEntity memberEntity : memberRequestEntities) {
      FamilyMemberEntityWrapper memberWrapper = new FamilyMemberEntityWrapper(
          familyEntityWrapper.getFamilyEntity(), memberEntity, memberRequestMap, auditInfo);
      familyMemberEntityWrappers.add(memberWrapper);
    }

    familyMemberEntityWrappers.stream()
        .filter(FamilyMemberEntityWrapper::isHeadOfFamily)
        .findFirst()
        .orElseThrow(() -> new ValidationException("No head of family found in registration request"));

    int addressId = addressDao.addAddress(familyEntityWrapper.getFamilyAddressEntity());
    familyEntityWrapper.getFamilyAddressEntity().setAddressId(addressId);
    familyEntityWrapper.getFamilyEntity().setAddressId(addressId);

    String familyPhone = DataFormatter.formatPhoneNumberForDBStorage(familyEntityWrapper.getFamilyEntity().getPhone());
    if (familyPhone != null && !familyPhone.isEmpty() && familyDao.isFamilyExistsForPhone(familyPhone)) {
      throw new ValidationException("A family with phone number " + familyPhone + " is already registered.");
    }

    int familyId = familyDao.addFamily(familyEntityWrapper.getFamilyEntity());
    familyEntityWrapper.getFamilyEntity().setFamilyId(familyId);

    for (FamilyMemberEntityWrapper familyMemberWrapper : familyMemberEntityWrappers) {
      familyMemberWrapper.getMemberEntity().setFamilyId(familyId);
      familyMemberWrapper.doValidate();
      addMemberToDB(familyMemberWrapper, auditInfo);
    }

    Map<Integer, FamilyMemberEntityWrapper> memberMap = familyMemberEntityWrappers.stream()
        .collect(Collectors.toMap(
            mew -> mew.getMemberRegistrationEntity().getMemberRequestId(),
            mew -> mew));

    for (FamilyMemberEntityWrapper familyMemberWrapper : familyMemberEntityWrappers) {
      FamilyMemberEntity memberEntity = familyMemberWrapper.getMemberEntity();
      if (!memberEntity.isHeadOfFamily()) {
        RelationshipDto relationshipWithMemberRegIdDto = familyMemberWrapper.getRelationshipWithMemberRegIdDto();
        FamilyMemberEntityWrapper relatedMemberEntityWrapper = memberMap.get(relationshipWithMemberRegIdDto.getMemberId());

        RelationshipDto relationshipWithMemberIdDto = RelationshipDto.builder()
            .memberId(relatedMemberEntityWrapper.getMemberEntity().getMemberId())
            .memberName(relatedMemberEntityWrapper.getMemberEntity().getFirstName())
            .relationshipType(relationshipWithMemberRegIdDto.getRelationshipType())
            .relatedMemberId(familyMemberWrapper.getMemberEntity().getMemberId())
            .relatedMemberName(familyMemberWrapper.getMemberEntity().getFirstName())
            .build();
        log.info("Updating relationship {}", relationshipWithMemberIdDto);
        updateRelationships(relationshipWithMemberIdDto, familyMemberWrapper.getMemberEntity(),
            relatedMemberEntityWrapper.getMemberEntity(), auditInfo);
      }
    }

    familyRequest.setStatus(FamilyRegistrationStatus.PROCESSED);
    familyRequest.setFamilyId(familyId);
    familyRegistrationRequestDao.updateFamilyRegistrationRequest(familyRequest, auditInfo);
    log.info("Family registration request {} approved and processed with family id: {}",
        familyRequest.getFamilyRequestId(), familyId);

    return ProcessFamilyRegistrationResponse.builder()
        .familyId(familyId)
        .familySurname(familyEntityWrapper.getFamilyEntity().getFamilyName())
        .familyRegistrationId(familyRequest.getFamilyRequestId())
        .message("Family registration request approved and family added successfully")
        .build();
  }

  public FamilyMemberEntity addMemberToDB(FamilyMemberEntityWrapper memberEntityWrapper, AuditInfo auditInfo) throws OperationException {
    log.info("Adding family member {} {} to family id: {}",
        memberEntityWrapper.getMemberEntity().getFirstName(),
        memberEntityWrapper.getMemberEntity().getLastName(),
        memberEntityWrapper.getMemberEntity().getFamilyId());

    FamilyMemberEntity memberEntity = memberEntityWrapper.getMemberEntity();
    checkIfEmailOrPhoneExists(memberEntity.getEmail(), memberEntity.getPhone());

    familyMemberDao.allMembersForFamily(memberEntity.getSamajId(), memberEntity.getFamilyId());

    FamilyMemberEntity newMemberFromDb = familyMemberDao.addFamilyMember(memberEntityWrapper.getMemberEntity());
    memberEntityWrapper.getMemberEntity().setMemberId(newMemberFromDb.getMemberId());

    if (newMemberFromDb.isHeadOfFamily()) {
      familyDao.updateFamilyDisplayName(memberEntityWrapper.getFamily(), newMemberFromDb, auditInfo);
    }

    return newMemberFromDb;
  }

  private void updateRelationships(
      RelationshipDto relationship, FamilyMemberEntity newMemberFromDb, FamilyMemberEntity relatedMember, AuditInfo auditInfo) {

    log.info("Input Relationship {}({})'s {} is {}",
        relationship.getMemberName(),
        relationship.getMemberId(),
        relationship.getRelationshipType(),
        newMemberFromDb.getFirstName());

    relationship.setRelatedMemberId(newMemberFromDb.getMemberId());
    relationship.setRelatedMemberName(newMemberFromDb.getFirstName());

    MemberRelationshipEntity relationshipEntity = relationship.entityFromDto();
    relationshipEntity = RelationshipUtils.normalizeRelationship(relationshipEntity, relatedMember);
    log.info("Member {}'s {} is {}",
        relationship.getMemberId(),
        relationship.getRelationshipType(),
        relationship.getRelatedMemberId());

    MemberRelationshipEntity memberRelationshipFromDb =
        memberRelationshipDao.getRelationshipBetweenMembers(
            relationship.getMemberId(), relationship.getRelatedMemberId());
    if (memberRelationshipFromDb == null) {
      log.info("Adding {}'s {} is {}",
          relationship.getMemberId(),
          relationship.getRelationshipType(),
          relationship.getRelatedMemberId());
      memberRelationshipDao.addMemberRelationship(relationshipEntity, auditInfo);
    } else {
      log.info("Relationship already exists between {} and {}",
          relationship.getMemberId(),
          relationship.getRelatedMemberId());
      if (memberRelationshipFromDb.getRelationshipType() != relationshipEntity.getRelationshipType()) {
        log.info("Relationship between member {} and {} is expected as {}, but found {}",
            relationship.getMemberId(),
            relationship.getRelatedMemberId(),
            relationship.getRelationshipType(),
            memberRelationshipFromDb.getRelationshipType());
      }
    }
  }

  private void checkIfEmailOrPhoneExists(String inputEmail, String inputPhone) {
    if (inputEmail != null && !inputEmail.isEmpty()) {
      if (familyMemberDao.isMemberExistsForEmail(inputEmail)) {
        throw new ValidationException("Email id '" + inputEmail + "' already in use, please provide other email id");
      }
    }
    if (inputPhone != null && !inputPhone.isEmpty()) {
      String normalizePhoneNumber = DataFormatter.formatPhoneNumberForDBStorage(inputPhone);
      if (familyMemberDao.isMemberExistsForPhone(normalizePhoneNumber)) {
        throw new ValidationException("Phone '" + inputPhone + "' already in use, please provide other phone number");
      }
    }
  }

  @Getter
  public static class FamilyEntityWrapper {
    private final FamilyEntity familyEntity;
    private final AddressEntity familyAddressEntity;
    private final AddressDto familyAddressDto;

    public FamilyEntityWrapper(FamilyEntity familyEntity, AddressEntity familyAddressEntity, AddressDto familyAddressDto) {
      this.familyEntity = familyEntity;
      this.familyAddressEntity = familyAddressEntity;
      this.familyAddressDto = familyAddressDto;
    }

    public void validateFamilyAndAddress() throws OperationException {
      checkValuePresent(familyEntity.getFamilyName(), "family name");
      FamilytreeValidationUtils.validateAddress(familyAddressDto);
    }

    public static FamilyEntityWrapper getFamilyEntityWrapperInstance(FamilyRegistrationRequestEntity familyRequest, AuditInfo auditInfo) {
      AddressDto familyAddressDto = getAddressFromRequest(familyRequest);
      AddressEntity familyAddressEntity = familyAddressDto.getAddressEntityFromDto(auditInfo);
      FamilyEntity familyEntity = getFamilyEntityFromRequest(familyRequest, familyAddressEntity, auditInfo);
      return new FamilyEntityWrapper(familyEntity, familyAddressEntity, familyAddressDto);
    }

    private static AddressDto getAddressFromRequest(FamilyRegistrationRequestEntity familyRequest) {
      return AddressDto.builder()
          .addressLine1(familyRequest.getAddressLine1())
          .addressLine2(familyRequest.getAddressLine2())
          .addressLine3(familyRequest.getAddressLine3())
          .city(familyRequest.getCity())
          .district(familyRequest.getDistrict())
          .state(familyRequest.getState())
          .postalCode(familyRequest.getPostalCode())
          .country(familyRequest.getCountry())
          .build();
    }

    private static FamilyEntity getFamilyEntityFromRequest(FamilyRegistrationRequestEntity familyRequest, AddressEntity address, AuditInfo auditInfo) {
      String phoneNumber = DataFormatter.formatPhoneNumberForDBStorage(familyRequest.getPhone());
      String familyRegion = DataFormatter.getRegion(address);
      String gotra = StringUtils.isEmpty(familyRequest.getGotra()) ? familyRequest.getFamilyName() : familyRequest.getGotra();
      FamilyEntity familyEntity = FamilyEntity.builder()
          .samajId(familyRequest.getSamajId())
          .familyName(familyRequest.getFamilyName())
          .familyNameInHindi(familyRequest.getFamilyNameInHindi())
          .gotra(gotra)
          .addressId(address.getAddressId())
          .region(familyRegion)
          .phone(phoneNumber)
          .email(familyRequest.getEmail())
          .active(true)
          .familyImage(ImageConstants.DEFAULT_FAMILY_IMAGE)
          .imageLastUpdated(auditInfo.getCreatedDate())
          .createdBy(auditInfo.getCreatedBy())
          .createdDate(auditInfo.getCreatedDate())
          .lastUpdatedBy(auditInfo.getLastUpdatedBy())
          .lastUpdatedDate(auditInfo.getLastUpdatedDate())
          .build();
      String familySearchString = DataFormatter.getFamilySearchString(familyEntity, null, address);
      familyEntity.setFamilysearchtext(familySearchString);
      return familyEntity;
    }
  }

  @Getter
  public static class FamilyMemberEntityWrapper {
    private final FamilyEntity family;
    private final FamilyMemberEntity memberEntity;
    private FamilyMemberRegistrationEntity memberRegistrationEntity;
    RelationshipDto relationshipWithMemberRegIdDto;

    private FamilyMemberEntityWrapper(FamilyEntity family, FamilyMemberRegistrationEntity regMemberEntity,
        Map<Integer, FamilyMemberRegistrationEntity> memberRequestMap, AuditInfo auditInfo) {
      this.family = family;
      this.memberRegistrationEntity = regMemberEntity;
      memberEntity = createMemberFromRequest(regMemberEntity, family.getRegion(), auditInfo);
      if (!memberEntity.isHeadOfFamily()) {
        if (regMemberEntity.getRelatedMemberId() <= 0) {
          throw new ValidationException("Related member id is required for non head of family member: " + regMemberEntity.getFirstName());
        }
        FamilyMemberRegistrationEntity relatedMemberRegEntity = memberRequestMap.get(regMemberEntity.getRelatedMemberId());
        if (relatedMemberRegEntity == null) {
          throw new ValidationException("Related member with member registration id : " + regMemberEntity.getRelatedMemberId() + " not found for member: " + regMemberEntity.getFirstName());
        }
        relationshipWithMemberRegIdDto = new RelationshipDto();
        relationshipWithMemberRegIdDto.setMemberId(relatedMemberRegEntity.getMemberRequestId());
        relationshipWithMemberRegIdDto.setMemberName(relatedMemberRegEntity.getFirstName());
        relationshipWithMemberRegIdDto.setRelationshipType(regMemberEntity.getRelationshipType());
        relationshipWithMemberRegIdDto.setRelatedMemberId(regMemberEntity.getMemberRequestId());
        relationshipWithMemberRegIdDto.setRelatedMemberName(regMemberEntity.getFirstName());
        log.info("Setting relationship with registration id: {}", relationshipWithMemberRegIdDto);
      }
    }

    public boolean isHeadOfFamily() {
      return memberEntity.isHeadOfFamily();
    }

    private FamilyMemberEntity createMemberFromRequest(
        FamilyMemberRegistrationEntity regMemberEntity, String region, AuditInfo auditInfo) {
      String phoneNumber = DataFormatter.formatPhoneNumberForDBStorage(regMemberEntity.getPhone());
      int memberAge = DataFormatter.getMemberAgeInYears(
          regMemberEntity.getBirthDay(), regMemberEntity.getBirthMonth(), regMemberEntity.getBirthYear());
      String profileImage = FamilyAbstractOperation.getMemberDefaultImagePath(
          regMemberEntity.getGender(), memberAge, regMemberEntity.getMaritalStatus());
      FamilyMemberEntity entity = FamilyMemberEntity.builder()
          .samajId(regMemberEntity.getSamajId())
          .headOfFamily(regMemberEntity.isHeadOfFamily())
          .firstName(regMemberEntity.getFirstName())
          .firstNameInHindi(regMemberEntity.getFirstNameInHindi())
          .lastName(family.getFamilyName())
          .maidenLastName(null)
          .nickName(null)
          .nickNameInHindi(null)
          .gender(regMemberEntity.getGender())
          .birthDay(regMemberEntity.getBirthDay())
          .birthMonth(regMemberEntity.getBirthMonth())
          .birthYear(regMemberEntity.getBirthYear())
          .dateOfDeath(null)
          .maritalStatus(regMemberEntity.getMaritalStatus())
          .weddingDate(regMemberEntity.getWeddingDate())
          .phone(phoneNumber)
          .isPhoneVerified(false)
          .isPhoneWhatsappRegistered(true)
          .email(regMemberEntity.getEmail())
          .isEmailVerified(false)
          .addressSameAsFamily(regMemberEntity.isAddressSameAsFamily())
          .memberAddressId(Constants.MEMBER_ADDRESS_SAME_AS_FAMILY_ADDRESS)
          .educationDetails(regMemberEntity.getEducationDetails())
          .occupation(regMemberEntity.getOccupation())
          .hobby(null)
          .profileImage(profileImage)
          .profileImageThumbnail(profileImage)
          .imageLastUpdated(auditInfo.getCreatedDate())
          .createdBy(auditInfo.getCreatedBy())
          .createdDate(auditInfo.getCreatedDate())
          .lastUpdatedBy(auditInfo.getLastUpdatedBy())
          .lastUpdatedDate(auditInfo.getLastUpdatedDate())
          .build();
      entity.updateSearchText(region);
      return entity;
    }

    public void doValidate() throws OperationException {
      checkObjectPresent(memberEntity.getFamilyId(), "family Id");
      checkValueRange(memberEntity.getFamilyId(), 1, Integer.MAX_VALUE, "family id");
      checkValueRange(memberEntity.getSamajId(), 1, Integer.MAX_VALUE, "samaj id");

      checkValuePresent(memberEntity.getFirstName(), "first name");
      validateStringLength(memberEntity.getFirstName(), "first name", 50);
      if (memberEntity.getFirstNameInHindi() != null) {
        validateStringLength(memberEntity.getFirstNameInHindi(), "first name in hindi", 50);
      }
      if (memberEntity.getMaidenLastName() != null) {
        validateStringLength(memberEntity.getMaidenLastName(), "maiden last name", 50);
      }
      if (memberEntity.getNickName() != null) {
        validateStringLength(memberEntity.getNickName(), "nick name", 50);
      }
      if (memberEntity.getNickNameInHindi() != null) {
        validateStringLength(memberEntity.getNickNameInHindi(), "nick name in hindi", 50);
      }

      EmailValidator.validateEmail(memberEntity.getEmail(), false);
      if (memberEntity.getPhone() != null) {
        FamilytreeValidationUtils.validatePhoneNumber(memberEntity.getPhone());
      }

      checkObjectPresent(memberEntity.getGender(), "gender");
      Short birthDay = (memberEntity.getBirthDay() == null || memberEntity.getBirthDay() == MISSING_BIRTH_DATE_VALUE) ? null : memberEntity.getBirthDay();
      validateBirthDate(birthDay, memberEntity.getBirthMonth().getMonthName(), memberEntity.getBirthYear());

      checkObjectPresent(memberEntity.getMaritalStatus(), "marital status");

      if (memberEntity.getEducationDetails() != null) {
        validateStringLength(memberEntity.getEducationDetails(), "education details", 200);
      }
      if (memberEntity.getOccupation() != null) {
        validateStringLength(memberEntity.getOccupation(), "occupation", 100);
      }

      if (!memberEntity.isAddressSameAsFamily()) {
        throw new ValidationException("Currently only address same as family is supported for family members");
      }

      if (!memberEntity.isHeadOfFamily()) {
        if (relationshipWithMemberRegIdDto == null) {
          throw new ValidationException("Relationship is required for this family member");
        }
        RelationshipType relationshipType =
            RelationshipType.getRelationshipType(relationshipWithMemberRegIdDto.getRelationshipType());
        if (relationshipType == null) {
          throw new ValidationException("Invalid relationship type provided");
        }
        checkObjectPresent(relationshipWithMemberRegIdDto.getMemberId(), "member Id");
        checkValueRange(relationshipWithMemberRegIdDto.getMemberId(), 1, Integer.MAX_VALUE, "member id");
        checkObjectPresent(relationshipWithMemberRegIdDto.getMemberName(), "member name");
      }
    }
  }
}
