package com.neasaa.familytree.utils;

import static com.neasaa.familytree.enums.Gender.Male;

import com.neasaa.base.app.operation.AuditInfo;
import com.neasaa.familytree.entity.FamilyMemberEntity;
import com.neasaa.familytree.entity.MemberRelationshipEntity;
import com.neasaa.familytree.enums.Gender;
import com.neasaa.familytree.enums.RelationshipType;
import com.neasaa.familytree.operation.family.model.RelationshipDto;
import java.util.List;

public class RelationshipUtils {

  public static FamilyMemberEntity findMemberByIdInList(
      List<FamilyMemberEntity> familyMembers, int memberId) {
    for (FamilyMemberEntity memberFromFamily : familyMembers) {
      if (memberFromFamily.getMemberId() == memberId) {
        return memberFromFamily;
      }
    }
    return null;
  }

  /**
   * @param relationship - Relationship DTO
   * @param member - Family member represents memberId in relationship
   * @return
   */
  public static RelationshipDto normalizeRelationship(
      RelationshipDto relationship, FamilyMemberEntity member) {
    if (relationship == null) {
      throw new IllegalArgumentException("Invalid relationship provided.");
    }
    RelationshipType relationshipType =
        RelationshipType.getRelationshipType(relationship.getRelationshipType());
    if (relationshipType == null) {
      throw new IllegalArgumentException(
          "Invalid relationship type " + relationship.getRelationshipType() + " provided.");
    }
    switch (relationshipType) {
      case Son:
      case Daughter:
      case Wife:
        return relationship;
      case Husband:
        return RelationshipDto.builder()
            .memberId(relationship.getRelatedMemberId())
            .memberName(relationship.getRelatedMemberName())
            .relationshipType(RelationshipType.Wife.name())
            .relatedMemberId(relationship.getMemberId())
            .relatedMemberName(relationship.getMemberName())
            .build();
      case Father:
      case Mother:
        RelationshipType childRelationshipType =
            member.getGender() == Male ? RelationshipType.Son : RelationshipType.Daughter;
        return RelationshipDto.builder()
            .memberId(relationship.getRelatedMemberId())
            .memberName(relationship.getRelatedMemberName())
            .relationshipType(childRelationshipType.name())
            .relatedMemberId(relationship.getMemberId())
            .relatedMemberName(relationship.getMemberName())
            .build();
      default:
        throw new IllegalArgumentException("Invalid relationship type: " + relationshipType);
    }
  }

  /**
   * @param relationship - Relationship DTO
   * @param member - Family member represents memberId in relationship
   * @return
   */
  public static MemberRelationshipEntity normalizeRelationship(
      MemberRelationshipEntity relationship, FamilyMemberEntity member) {
    if (relationship == null) {
      throw new IllegalArgumentException("Invalid relationship provided.");
    }
    return switch (relationship.getRelationshipType()) {
      case Son, Daughter, Wife -> relationship;
      case Husband -> MemberRelationshipEntity.builder()
          .memberId(relationship.getRelatedMemberId())
          .relationshipType(RelationshipType.Wife)
          .relatedMemberId(relationship.getMemberId())
          .build();
      case Father, Mother -> {
        RelationshipType childRelationshipType =
            member.getGender() == Male ? RelationshipType.Son : RelationshipType.Daughter;
        yield MemberRelationshipEntity.builder()
            .memberId(relationship.getRelatedMemberId())
            .relationshipType(childRelationshipType)
            .relatedMemberId(relationship.getMemberId())
            .build();
      }
      default -> throw new IllegalArgumentException(
          "Invalid relationship type: " + relationship.getRelationshipType());
    };
  }

  /**
   * Input parameter is interpreted as member is relatedRelationshipType of relatedMember
   *
   * @param member
   * @param relatedRelationshipType
   * @param relatedMember
   * @return Return relationship which read as R.member is relatedRelationshipType of
   *     R.relatedMember
   */
  public static MemberRelationshipEntity buildRelationships(
      FamilyMemberEntity member,
      RelationshipType relatedRelationshipType,
      FamilyMemberEntity relatedMember,
      AuditInfo auditInfo) {
    if (relatedRelationshipType == RelationshipType.Son
        || relatedRelationshipType == RelationshipType.Daughter
        || relatedRelationshipType == RelationshipType.Wife) {
      return MemberRelationshipEntity.builder()
          .memberId(member.getMemberId())
          .relationshipType(relatedRelationshipType)
          .relatedMemberId(relatedMember.getMemberId())
          .createdBy(auditInfo.getCreatedBy())
          .createdDate(auditInfo.getCreatedDate())
          .lastUpdatedBy(auditInfo.getLastUpdatedBy())
          .lastUpdatedDate(auditInfo.getLastUpdatedDate())
          .build();
    }

    if (relatedRelationshipType == RelationshipType.Husband) {
      return MemberRelationshipEntity.builder()
          .memberId(relatedMember.getMemberId())
          .relationshipType(RelationshipType.Wife)
          .relatedMemberId(member.getMemberId())
          .createdBy(auditInfo.getCreatedBy())
          .createdDate(auditInfo.getCreatedDate())
          .lastUpdatedBy(auditInfo.getLastUpdatedBy())
          .lastUpdatedDate(auditInfo.getLastUpdatedDate())
          .build();
    }

    RelationshipType reverseRelationshipType = null;
    if (relatedRelationshipType == RelationshipType.Father
        || relatedRelationshipType == RelationshipType.Mother) {
      if (member.getGender() == Male) {
        reverseRelationshipType = RelationshipType.Son;
      }
      if (member.getGender() == Gender.Female) {
        reverseRelationshipType = RelationshipType.Daughter;
      }
      return MemberRelationshipEntity.builder()
          .memberId(relatedMember.getMemberId())
          .relationshipType(reverseRelationshipType)
          .relatedMemberId(member.getMemberId())
          .createdBy(auditInfo.getCreatedBy())
          .createdDate(auditInfo.getCreatedDate())
          .lastUpdatedBy(auditInfo.getLastUpdatedBy())
          .lastUpdatedDate(auditInfo.getLastUpdatedDate())
          .build();
    }
    throw new IllegalArgumentException("Invalid relationship type: " + relatedRelationshipType);
  }
}
