package com.neasaa.familytree.operation.family;

import static com.neasaa.familytree.operation.OperationNames.ADD_RELATIONSHIP;

import com.neasaa.base.app.operation.AuditInfo;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.base.app.operation.model.EmptyOperationResponse;
import com.neasaa.familytree.entity.FamilyMemberEntity;
import com.neasaa.familytree.entity.MemberRelationshipEntity;
import com.neasaa.familytree.enums.Gender;
import com.neasaa.familytree.enums.RelationshipType;
import com.neasaa.familytree.operation.family.model.AddRelationshipRequest;
import com.neasaa.familytree.utils.RelationshipUtils;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Log4j2
@Component("AddRelationshipOperation")
@Scope("prototype")
public class AddRelationshipOperation
    extends FamilyAbstractOperation<AddRelationshipRequest, EmptyOperationResponse> {

  @Override
  public String getOperationName() {
    return ADD_RELATIONSHIP;
  }

  @Override
  public void doValidate(AddRelationshipRequest opRequest) throws OperationException {
    if (opRequest.getMemberId() <= 0) {
      throw new ValidationException("Invalid member ID provided.");
    }
    if (opRequest.getRelatedMemberId() <= 0) {
      throw new ValidationException("Invalid related member ID provided.");
    }
    if (opRequest.getMemberId() == opRequest.getRelatedMemberId()) {
      throw new ValidationException("Member ID and related member ID cannot be the same.");
    }
    if (opRequest.getRelationship() == null || opRequest.getRelationship().isEmpty()) {
      throw new ValidationException("Relationship type is required.");
    }
    if (RelationshipType.getRelationshipType(opRequest.getRelationship()) == null) {
      throw new ValidationException(
          "Invalid relationship type: "
              + opRequest.getRelationship()
              + ". Valid values are: Son, Daughter, Father, Mother.");
    }
    RelationshipType relationshipType =
        RelationshipType.getRelationshipType(opRequest.getRelationship());
    if (relationshipType == RelationshipType.Husband || relationshipType == RelationshipType.Wife) {
      // Use Register Marriage feature to add Husband or Wife relationships
      throw new ValidationException(
          "Adding a Husband or Wife relationship is not allowed.");
    }
  }

  @Override
  public EmptyOperationResponse doExecute(AddRelationshipRequest opRequest)
      throws OperationException {
    int samajId = getSamajIdFromSession();

    FamilyMemberEntity member = familyMemberDao.getMemberById(samajId, opRequest.getMemberId());
    if (member == null) {
      throw new ValidationException("Member not found for ID: " + opRequest.getMemberId());
    }

    FamilyMemberEntity relatedMember = familyMemberDao.getMemberById(samajId, opRequest.getRelatedMemberId());
    if (relatedMember == null) {
      throw new ValidationException(
          "Related member not found for ID: " + opRequest.getRelatedMemberId());
    }

    RelationshipType relationshipType =
        RelationshipType.getRelationshipType(opRequest.getRelationship());

    switch (relationshipType) {
      case Son, Daughter -> validateChild(samajId, member, relatedMember, relationshipType);
      case Father, Mother -> validateParent(samajId, member, relatedMember, relationshipType);
      case Husband, Wife -> validateSpouse(samajId, member, relatedMember, relationshipType);
    }

    AuditInfo auditInfo = getAuditInfo();
    MemberRelationshipEntity relationshipEntity =
        RelationshipUtils.buildRelationships(member, relationshipType, relatedMember, auditInfo);

    MemberRelationshipEntity existing =
        memberRelationshipDao.getRelationshipBetweenMembers(
            relationshipEntity.getMemberId(), relationshipEntity.getRelatedMemberId());
    if (existing != null) {
      throw new ValidationException("Relationship already exists between these members.");
    }

    memberRelationshipDao.addMemberRelationship(relationshipEntity, auditInfo);
    String message = String.format("Added relationship (%d) %s's  %s is (%d) %s",
        member.getMemberId(),
        member.getFirstName(),
        relationshipType.name(),
        relatedMember.getMemberId(),
        relatedMember.getFirstName());
    log.info(message);
    return new EmptyOperationResponse(message);
  }

  private void validateChild(
      int samajId, FamilyMemberEntity member, FamilyMemberEntity relatedMember, RelationshipType type) {
    // member's son/daughter is relatedMember — validate relatedMember's gender
    Gender expectedGender = (type == RelationshipType.Son) ? Gender.Male : Gender.Female;
    if (relatedMember.getGender() != null && relatedMember.getGender() != expectedGender) {
      throw new ValidationException(
          "Related member's (" + relatedMember.getFirstName() + ") gender must be "
              + expectedGender.name()
              + " for a "
              + type.name()
              + " relationship.");
    }

    // relatedMember (child) must be born after member (parent)
    if (member.getBirthYear() != null && relatedMember.getBirthYear() != null) {
      if (relatedMember.getBirthYear() < member.getBirthYear()) {
        throw new ValidationException(
            relatedMember.getFirstName()
                + " (child) must be born after "
                + member.getFirstName()
                + " (parent).");
      }
    }

    // member must not already have a child with the same first name
    List<MemberRelationshipEntity> existingChildren =
        memberRelationshipDao.getRelatedMembersByIdAndRelationType(
            List.of(member.getMemberId()), List.of(RelationshipType.Son, RelationshipType.Daughter));
    if (existingChildren != null) {
      for (MemberRelationshipEntity childRelation : existingChildren) {
        FamilyMemberEntity existingChild =
            familyMemberDao.getMemberById(samajId, childRelation.getRelatedMemberId());
        if (existingChild != null
            && existingChild.getFirstName().equalsIgnoreCase(relatedMember.getFirstName())) {
          throw new ValidationException(
              member.getFirstName()
                  + " already has a child named "
                  + relatedMember.getFirstName()
                  + ".");
        }
      }
    }
  }

  private void validateParent(
      int samajId, FamilyMemberEntity member, FamilyMemberEntity relatedMember, RelationshipType type) {
    // member's father/mother is relatedMember — validate relatedMember's gender
    Gender expectedGender = (type == RelationshipType.Father) ? Gender.Male : Gender.Female;
    if (relatedMember.getGender() != null && relatedMember.getGender() != expectedGender) {
      throw new ValidationException(
          "Related member's (" + relatedMember.getFirstName() + ") gender must be "
              + expectedGender.name()
              + " for a "
              + type.name()
              + " relationship.");
    }

    // relatedMember (parent) must be born before member (child)
    if (member.getBirthYear() != null && relatedMember.getBirthYear() != null) {
      if (relatedMember.getBirthYear() > member.getBirthYear()) {
        throw new ValidationException(
            relatedMember.getFirstName()
                + " (parent) must be born before "
                + member.getFirstName()
                + " (child).");
      }
    }

    // member must not already have any parent recorded
    List<MemberRelationshipEntity> existingParents =
        memberRelationshipDao.getParentsForMemberById(member.getMemberId());
    if (existingParents != null && !existingParents.isEmpty()) {
      throw new ValidationException(
          member.getFirstName() + " already has a parent with parent id " + existingParents.get(0).getRelatedMemberId() + ".");
    }
  }

  private void validateSpouse(
      int samajId, FamilyMemberEntity member, FamilyMemberEntity relatedMember, RelationshipType type) {
    // relatedMember is member's husband/wife — validate relatedMember's gender
    Gender expectedGender = (type == RelationshipType.Husband) ? Gender.Male : Gender.Female;
    if (relatedMember.getGender() != null && relatedMember.getGender() != expectedGender) {
      throw new ValidationException(
          "Related member's gender must be "
              + expectedGender.name()
              + " for a "
              + type.name()
              + " relationship.");
    }

    // member must not already have a spouse
    MemberRelationshipEntity existingSpouse =
        memberRelationshipDao.getSpouseForMemberById(member.getMemberId());
    if (existingSpouse != null) {
      throw new ValidationException(
          member.getFirstName() + " already has a spouse with spouse id " + existingSpouse.getRelatedMemberId() + ".");
    }
  }
}
