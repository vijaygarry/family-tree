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
import com.neasaa.familytree.dao.pg.FamilyRegistrationRequestDao;
import com.neasaa.familytree.entity.AddressEntity;
import com.neasaa.familytree.entity.FamilyEntity;
import com.neasaa.familytree.entity.FamilyMemberEntity;
import com.neasaa.familytree.entity.FamilyMemberRegistrationEntity;
import com.neasaa.familytree.entity.FamilyRegistrationRequestEntity;
import com.neasaa.familytree.entity.MemberRelationshipEntity;
import com.neasaa.familytree.enums.FamilyRegistrationStatus;
import com.neasaa.familytree.enums.RelationshipType;
import com.neasaa.familytree.operation.OperationNames;
import com.neasaa.familytree.operation.family.model.AddressDto;
import com.neasaa.familytree.operation.family.model.ProcessFamilyRegistrationRequest;
import com.neasaa.familytree.operation.family.model.ProcessFamilyRegistrationResponse;
import com.neasaa.familytree.operation.family.model.RelationshipDto;
import com.neasaa.familytree.utils.Constants;
import com.neasaa.familytree.utils.DataFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.neasaa.familytree.utils.FamilytreeValidationUtils;
import com.neasaa.familytree.utils.RelationshipUtils;
import com.neasaa.util.StringUtils;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Log4j2
@Component("ProcessFamilyRegistrationOperation")
@Scope("prototype")
public class ProcessFamilyRegistrationOperation extends FamilyAbstractOperation<ProcessFamilyRegistrationRequest, ProcessFamilyRegistrationResponse> {

  @Autowired
  private FamilyRegistrationRequestDao familyRegistrationRequestDao;

  @Override
  public String getOperationName() {
    return OperationNames.PROCESS_FAMILY_REGISTRATION;
  }

  @Override
  public void doValidate(ProcessFamilyRegistrationRequest opRequest) throws OperationException {
    if (opRequest == null) {
      throw new ValidationException("Invalid request provided.");
    }
    checkValueRange(opRequest.getFamilyRegistrationId(), 1, Integer.MAX_VALUE, "family registration id");
    checkObjectPresent(opRequest.getAction(), "action");
    RegistrationAction action = RegistrationAction.getActionByString(opRequest.getAction());
    if(action == null) {
      throw new ValidationException("Invalid action provided. Allowed values are: " + Arrays.toString(RegistrationAction.values()));
    }
  }


  @Override
  public ProcessFamilyRegistrationResponse doExecute(ProcessFamilyRegistrationRequest opRequest) throws OperationException {
    log.info("Processing family registration request with id: {} and action: {}", 
        opRequest.getFamilyRegistrationId(), opRequest.getAction());

    // Load family registration request
    FamilyRegistrationRequestEntity familyRequest = familyRegistrationRequestDao.getFamilyRegistrationRequestById(opRequest.getFamilyRegistrationId());
    
    if (familyRequest == null) {
      throw new ValidationException("Family RegistrationRequest not found");
    }

    // Check if status is PENDING
    if (familyRequest.getStatus() != FamilyRegistrationStatus.PENDING) {
      throw new ValidationException("Family registration request already processed with status: " + familyRequest.getStatus());
    }

    // Handle different actions
    RegistrationAction action = RegistrationAction.getActionByString(opRequest.getAction());
    switch (action) {
      case APPROVE:
        return approveRegistration(familyRequest);
      case DENY:
        return updateRegistrationStatus(familyRequest, FamilyRegistrationStatus.INVALID, "Registration request denied");
      case DUPLICATE:
        return updateRegistrationStatus(familyRequest, FamilyRegistrationStatus.DUPLICATE, "Registration request marked as duplicate");
      case INVALID:
        return updateRegistrationStatus(familyRequest, FamilyRegistrationStatus.INVALID, "Registration request marked as invalid");
      default:
        throw new ValidationException("Invalid action provided");
    }
  }

  private ProcessFamilyRegistrationResponse approveRegistration(FamilyRegistrationRequestEntity familyRequest) throws OperationException {

    FamilyEntityWrapper familyEntityWrapper = FamilyEntityWrapper.getFamilyEntityWrapperInstance(familyRequest, getAuditInfo());
    familyEntityWrapper.validateFamilyAndAddress();

    // Fetch all family members from registration
    List<FamilyMemberRegistrationEntity> memberRequestEntities = familyRegistrationRequestDao.getFamilyMemberRegistrationsByFamilyRequestId(familyRequest.getFamilyRequestId());
    if (memberRequestEntities == null || memberRequestEntities.isEmpty()) {
      throw new ValidationException("No members found for family registration request");
    }

    //Map memberRegId -> FamilyMemberRegistrationEntity
    Map<Integer, FamilyMemberRegistrationEntity> memberRequestMap = memberRequestEntities.stream()
        .collect(java.util.stream.Collectors.toMap(
                FamilyMemberRegistrationEntity::getMemberRequestId, reg -> reg));
    List<FamilyMemberEntityWrapper> familyMemberEntityWrappers = new ArrayList<>();

    for(FamilyMemberRegistrationEntity memberEntity : memberRequestEntities) {
      FamilyMemberEntityWrapper memberWrapper = new FamilyMemberEntityWrapper(familyEntityWrapper.familyEntity, memberEntity, memberRequestMap, getAuditInfo());
      familyMemberEntityWrappers.add(memberWrapper);
    }

    // Find head of family
    FamilyMemberEntityWrapper headOfFamilyMemberEntityWrapper = familyMemberEntityWrappers.stream()
        .filter(FamilyMemberEntityWrapper::isHeadOfFamily)
        .findFirst()
        .orElseThrow(() -> new ValidationException("No head of family found in registration request"));

    // Create and insert family
    int addressId = addressDao.addAddress(familyEntityWrapper.getFamilyAddressEntity());
    familyEntityWrapper.getFamilyAddressEntity().setAddressId(addressId);
    familyEntityWrapper.getFamilyEntity().setAddressId(addressId);

    int familyId = familyDao.addFamily(familyEntityWrapper.getFamilyEntity());
    familyEntityWrapper.getFamilyEntity().setFamilyId(familyId);

    for(FamilyMemberEntityWrapper familyMemberWrapper : familyMemberEntityWrappers) {
      familyMemberWrapper.getMemberEntity().setFamilyId(familyId);
      familyMemberWrapper.doValidate();
      addMemberToDB(familyMemberWrapper);
    }
    // MemberRegistrationId to FamilyMemberEntityWrapper map for all members
    Map<Integer, FamilyMemberEntityWrapper> memberMap = familyMemberEntityWrappers.stream()
            .collect(java.util.stream.Collectors.toMap(
                    (memberEntityWrapperK) -> memberEntityWrapperK.getMemberRegistrationEntity().getMemberRequestId(),
                    memberEntityWrapperV -> memberEntityWrapperV));

    for(FamilyMemberEntityWrapper familyMemberWrapper : familyMemberEntityWrappers) {
        FamilyMemberEntity memberEntity = familyMemberWrapper.getMemberEntity();
        if(!memberEntity.isHeadOfFamily()) {
          RelationshipDto relationshipWithMemberRegIdDto = familyMemberWrapper.getRelationshipWithMemberRegIdDto();
          FamilyMemberEntityWrapper relatedMemberEntityWrapper = memberMap.get(relationshipWithMemberRegIdDto.getMemberId());

          RelationshipDto relationshipWithMemberIdDto = RelationshipDto.builder()
                    .memberId(relatedMemberEntityWrapper.getMemberEntity().getMemberId())
                    .memberName(relatedMemberEntityWrapper.getMemberEntity().getFirstName())
                    .relationshipType(relationshipWithMemberRegIdDto.getRelationshipType())
                    .relatedMemberId(familyMemberWrapper.memberEntity.getMemberId())
                    .relatedMemberName(familyMemberWrapper.memberEntity.getFirstName())
                  .build();
          log.info("Updating relationship {} ", relationshipWithMemberIdDto);
          updateRelationships(relationshipWithMemberIdDto, familyMemberWrapper.getMemberEntity(), relatedMemberEntityWrapper.getMemberEntity());
        }
    }




    // Update family id back in family registration table and update status as Approved
    familyRequest.setStatus(FamilyRegistrationStatus.PROCESSED);
    familyRequest.setFamilyId(familyId);
    familyRegistrationRequestDao.updateFamilyRegistrationRequest(familyRequest, getAuditInfo());
    log.info("Family registration request {} approved and processed with family id: {}", 
        familyRequest.getFamilyRequestId(), familyId);

    return ProcessFamilyRegistrationResponse.builder()
        .familyId(familyId)
        .familySurname(familyEntityWrapper.getFamilyEntity().getFamilyName())
        .familyRegistrationId(familyRequest.getFamilyRequestId())
//        .memberCount(memberMap.size())
        .message("Family registration request approved and family added successfully")
        .build();
  }

  private ProcessFamilyRegistrationResponse updateRegistrationStatus(
      FamilyRegistrationRequestEntity familyRequest, 
      FamilyRegistrationStatus status,
      String message) {
    familyRequest.setStatus(status);
    AuditInfo auditInfo = getAuditInfo();
    familyRegistrationRequestDao.updateFamilyRegistrationRequest(familyRequest, auditInfo);
    
    log.info("Family registration request {} updated to status: {}", 
        familyRequest.getFamilyRequestId(), status);

    return ProcessFamilyRegistrationResponse.builder()
        .familyRegistrationId(familyRequest.getFamilyRequestId())
        .message(message)
        .build();
  }

  public FamilyMemberEntity addMemberToDB(FamilyMemberEntityWrapper memberEntityWrapper)
          throws OperationException {
    log.info("Adding family member {} {} to family id: {}",
            memberEntityWrapper.getMemberEntity().getFirstName(),
            memberEntityWrapper.getMemberEntity().getLastName(),
            memberEntityWrapper.getMemberEntity().getFamilyId());

    FamilyMemberEntity memberEntity = memberEntityWrapper.getMemberEntity();
    checkIfEmailOrPhoneExists(memberEntity.getEmail(), memberEntity.getPhone());

    // Fetch list of family members
    // TODO: Do we need all the members or only HOF should be sufficient.
    List<FamilyMemberEntity> familyMembers =
            familyMemberDao.allMembersForFamily(memberEntity.getSamajId(), memberEntity.getFamilyId());

    FamilyMemberEntity newMemberFromDb =
            familyMemberDao.addFamilyMember(memberEntityWrapper.getMemberEntity());
    memberEntityWrapper.memberEntity.setMemberId(newMemberFromDb.getMemberId());

    if (newMemberFromDb.isHeadOfFamily()) {
      familyDao.updateFamilyDisplayName(memberEntityWrapper.getFamily(), newMemberFromDb, getAuditInfo());
    }

    // TODO: Update member id back in member registration table

    return newMemberFromDb;
  }


  private void updateRelationships(
          RelationshipDto relationship, FamilyMemberEntity newMemberFromDb, FamilyMemberEntity relatedMember) {

    log.info(
            "Input Relationship {}({})'s {} is {} ",
            relationship.getMemberName(),
            relationship.getMemberId(),
            relationship.getRelationshipType(),
            newMemberFromDb.getFirstName());

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
  private MemberRelationshipEntity createRelationship(
      FamilyMemberRegistrationEntity regMember,
      FamilyMemberEntity member,
      Map<Integer, FamilyMemberEntity> memberMap) throws OperationException {
    
    // Find the related member
    FamilyMemberEntity relatedMember = memberMap.get(regMember.getRelatedMemberId());
    if (relatedMember == null) {
      throw new ValidationException("Related member not found for member: " + regMember.getFirstName());
    }

    AuditInfo auditInfo = getAuditInfo();
    return MemberRelationshipEntity.builder()
        .memberId(member.getMemberId())
        .relatedMemberId(relatedMember.getMemberId())
        .relationshipType(RelationshipType.getRelationshipType(regMember.getRelationshipType()))
        .createdBy(auditInfo.getCreatedBy())
        .createdDate(auditInfo.getCreatedDate())
        .lastUpdatedBy(auditInfo.getLastUpdatedBy())
        .lastUpdatedDate(auditInfo.getLastUpdatedDate())
        .build();
  }

  public enum RegistrationAction {
    APPROVE,
    DENY,
    DUPLICATE,
    INVALID;

    public static RegistrationAction getActionByString(String input) {
      for (RegistrationAction action : RegistrationAction.values()) {
        if (action.name().equalsIgnoreCase(input)) {
          return action;
        }
      }
      return null;
    }
  }

  @Getter
  public static class FamilyEntityWrapper {
    private FamilyEntity familyEntity;
    private AddressEntity familyAddressEntity;
    private AddressDto familyAddressDto;

    public FamilyEntityWrapper(FamilyEntity familyEntity, AddressEntity familyAddressEntity, AddressDto familyAddressDto) {
      this.familyEntity = familyEntity;
      this.familyAddressEntity = familyAddressEntity;
      this.familyAddressDto = familyAddressDto;
    }

    private void validateFamilyAndAddress() throws OperationException {
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
      //TODO: Provide head of family entity to getFamilySearchString method to generate better search string with head of family name.
      String familySearchString =
              DataFormatter.getFamilySearchString(familyEntity, null, address);
      familyEntity.setFamilysearchtext(familySearchString);
      return familyEntity;
    }
  }

  @Getter
  public static class FamilyMemberEntityWrapper {
    private final FamilyEntity family;
    private final FamilyMemberEntity memberEntity;
    private FamilyMemberRegistrationEntity memberRegistrationEntity;
//    private MemberRelationshipEntity spouse;
//    private List<MemberRelationshipEntity> children;
    RelationshipDto relationshipWithMemberRegIdDto;

    private FamilyMemberEntityWrapper (FamilyEntity family, FamilyMemberRegistrationEntity regMemberEntity, Map<Integer, FamilyMemberRegistrationEntity> memberRequestMap, AuditInfo auditInfo) {
      this.family = family;
      this.memberRegistrationEntity = regMemberEntity;
      memberEntity = createMemberFromRequest(regMemberEntity, family.getRegion(), auditInfo);
      if(!memberEntity.isHeadOfFamily()) {
        if(regMemberEntity.getRelatedMemberId() <= 0) {
          throw new ValidationException("Related member id is required for non head of family member: " + regMemberEntity.getFirstName());
        }

        FamilyMemberRegistrationEntity relatedMemberRegEntity = memberRequestMap.get(regMemberEntity.getRelatedMemberId());
        if(relatedMemberRegEntity == null) {
          throw new ValidationException("Related member with member registration id : " + regMemberEntity.getRelatedMemberId() + " not found for member: " + regMemberEntity.getFirstName());
        }
        relationshipWithMemberRegIdDto = new RelationshipDto();
        relationshipWithMemberRegIdDto.setMemberId(relatedMemberRegEntity.getMemberRequestId());
        relationshipWithMemberRegIdDto.setMemberName(relatedMemberRegEntity.getFirstName());
        relationshipWithMemberRegIdDto.setRelationshipType(regMemberEntity.getRelationshipType());
        relationshipWithMemberRegIdDto.setRelatedMemberId(regMemberEntity.getMemberRequestId());
        relationshipWithMemberRegIdDto.setRelatedMemberName(regMemberEntity.getFirstName());
        log.info("Setting relationship with registration id :{}", relationshipWithMemberRegIdDto);
      }
    }

    public boolean isHeadOfFamily () {
        return memberEntity.isHeadOfFamily();
    }

    private FamilyMemberEntity createMemberFromRequest(
            FamilyMemberRegistrationEntity regMemberEntity,
            String region,
            AuditInfo auditInfo) {

      String phoneNumber = DataFormatter.formatPhoneNumberForDBStorage(regMemberEntity.getPhone());
      int memberAge =
              DataFormatter.getMemberAgeInYears(
                      regMemberEntity.getBirthDay(), regMemberEntity.getBirthMonth(), regMemberEntity.getBirthYear());
      String profileImage = getMemberDefaultImagePath(regMemberEntity.getGender(), memberAge, regMemberEntity.getMaritalStatus());
      FamilyMemberEntity memberEntity = FamilyMemberEntity.builder()
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
      memberEntity.updateSearchText(region);
        return memberEntity;
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
      //If birth day is not provided or set as -1 then pass value as null to validation
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