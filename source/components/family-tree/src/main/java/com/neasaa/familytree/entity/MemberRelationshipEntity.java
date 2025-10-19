/*
 * Copyright (c) 2018- 2021
 */

package com.neasaa.familytree.entity;

import com.neasaa.base.app.entity.BaseEntity;
import com.neasaa.familytree.enums.RelationshipType;
import java.io.Serial;
import java.util.Date;
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
public class MemberRelationshipEntity extends BaseEntity {

  @Serial private static final long serialVersionUID = 1748576601294L;

  private int memberId;
  private RelationshipType relationshipType;
  private int relatedMemberId;
  private int createdBy;
  private Date createdDate;
  private int lastUpdatedBy;
  private Date lastUpdatedDate;

  public static MemberRelationshipEntity inverseSpouseRelationship(
      MemberRelationshipEntity relationship) {
    return MemberRelationshipEntity.builder()
        .memberId(relationship.relatedMemberId)
        .relationshipType(RelationshipType.getReverseRelationship(relationship.relationshipType))
        .relatedMemberId(relationship.memberId)
        .build();
  }
}
