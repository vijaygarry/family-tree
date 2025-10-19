package com.neasaa.familytree.operation.family.model;

import com.neasaa.familytree.entity.MemberRelationshipEntity;
import com.neasaa.familytree.enums.RelationshipType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * Interpretation: memberId (memberName)'s relationshipType is relatedMemberId (relatedMemberName).
 * E.g., if memberId is 1 (Vijay), relatedMemberId is 2 (Arav), and relationshipType is "Son" then
 * it means:, Vijay's Son is Arav.
 */
public class RelationshipDto {
  Integer memberId;
  String memberName;
  String relationshipType;
  Integer relatedMemberId;
  String relatedMemberName;

  public void trimFields() {
    if (memberName != null) {
      memberName = memberName.trim();
    }
    if (relationshipType != null) {
      relationshipType = relationshipType.trim();
    }
    if (relatedMemberName != null) {
      relatedMemberName = relatedMemberName.trim();
    }
  }

  public MemberRelationshipEntity entityFromDto() {
    MemberRelationshipEntity entity = new MemberRelationshipEntity();
    entity.setMemberId(this.memberId);
    RelationshipType relationshipTypeEnum =
        RelationshipType.getRelationshipType(getRelationshipType());
    entity.setRelationshipType(relationshipTypeEnum);
    entity.setRelatedMemberId(this.relatedMemberId);
    return entity;
  }
}
