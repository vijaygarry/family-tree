package com.neasaa.familytree.operation.family;

import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.familytree.dao.pg.FamilyRegistrationRequestDao;
import com.neasaa.familytree.entity.FamilyMemberRegistrationEntity;
import com.neasaa.familytree.entity.FamilyRegistrationRequestEntity;
import com.neasaa.familytree.operation.OperationNames;
import com.neasaa.familytree.operation.family.model.GetFamilyRegistrationDetailsRequest;
import com.neasaa.familytree.operation.family.model.GetFamilyRegistrationDetailsResponse;
import com.neasaa.familytree.operation.family.model.MemberSummaryDto;
import com.neasaa.familytree.operation.family.model.RegistrationDetailsDto;
import com.neasaa.familytree.enums.RelationshipType;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Log4j2
@Component("GetFamilyRegistrationDetailsOperation")
@Scope("prototype")
public class GetFamilyRegistrationDetailsOperation
    extends FamilyAbstractOperation<
        GetFamilyRegistrationDetailsRequest, GetFamilyRegistrationDetailsResponse> {

  @Autowired private FamilyRegistrationRequestDao familyRegistrationRequestDao;

  @Override
  public String getOperationName() {
    return OperationNames.GET_FAMILY_REGISTRATION_DETAILS;
  }

  @Override
  public void doValidate(GetFamilyRegistrationDetailsRequest opRequest) throws OperationException {
    if (opRequest == null || opRequest.getFamilyRequestId() == null) {
      throw new ValidationException("Family registration id is required.");
    }
    if (opRequest.getFamilyRequestId() < 1) {
      throw new ValidationException("Invalid family registration id provided.");
    }
  }

  @Override
  public GetFamilyRegistrationDetailsResponse doExecute(
      GetFamilyRegistrationDetailsRequest opRequest) throws OperationException {
    int familyRequestId = opRequest.getFamilyRequestId();
    log.info("Fetching family registration details for familyRequestId={}", familyRequestId);

    FamilyRegistrationRequestEntity registrationRequest =
        familyRegistrationRequestDao.getFamilyRegistrationRequestById(familyRequestId);
    if (registrationRequest == null) {
      throw new ValidationException("Family registration not found for id: " + familyRequestId);
    }

    List<FamilyMemberRegistrationEntity> memberEntities =
        familyRegistrationRequestDao.getFamilyMemberRegistrationsByFamilyRequestId(familyRequestId);

    FamilyMemberRegistrationEntity head = memberEntities.stream()
        .filter(FamilyMemberRegistrationEntity::isHeadOfFamily)
        .findFirst()
        .orElse(null);

    String headOfFamilyName = head != null ? head.getFirstName() : "Head of Family not defined";

    Map<Integer, String> memberNames = memberEntities.stream()
        .collect(Collectors.toMap(
            FamilyMemberRegistrationEntity::getMemberRequestId,
            FamilyMemberRegistrationEntity::getFirstName));

    List<MemberSummaryDto> memberList = memberEntities.stream()
        .map(m -> MemberSummaryDto.fromRegistrationEntity(m, resolveFamilyRelationship(m, memberNames)))
        .collect(Collectors.toList());

    GetFamilyRegistrationDetailsResponse response = new GetFamilyRegistrationDetailsResponse();
    response.setRegistrationDetails(RegistrationDetailsDto.fromEntity(registrationRequest, headOfFamilyName));
    response.setMemberList(memberList);
    return response;
  }

  private String resolveFamilyRelationship(
      FamilyMemberRegistrationEntity member, Map<Integer, String> memberNames) {
    if (member.isHeadOfFamily()) {
      return HEAD_OF_FAMILY;
    }
    RelationshipType relType = RelationshipType.getRelationshipType(member.getRelationshipType());
    if (relType == null) {
      return UNKNOWN_RELATIONSHIP;
    }
    String relatedName = memberNames.getOrDefault(member.getRelatedMemberId(), "");
    return switch (relType) {
      case Wife -> WIFE_OF_MEMBER.formatted(relatedName);
      case Husband -> HUSBAND_OF_MEMBER.formatted(relatedName);
      case Son -> SON_OF_MEMBER.formatted(relatedName);
      case Daughter -> DAUGHTER_OF_MEMBER.formatted(relatedName);
      case Father -> FATHER_OF_MEMBER.formatted(relatedName);
      case Mother -> MOTHER_OF_MEMBER.formatted(relatedName);
    };
  }
}
