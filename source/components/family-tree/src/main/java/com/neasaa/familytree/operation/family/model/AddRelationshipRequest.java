package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddRelationshipRequest extends OperationRequest {

  private int memberId;
  private int relatedMemberId;
  private String relationship;

  @Override
  public void normalize() {
    if (relationship != null) {
      relationship = relationship.trim();
    }
  }
}
