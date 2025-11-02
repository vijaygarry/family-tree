package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationRequest;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManageRelationshipRequest extends OperationRequest {
  private RelationshipDto relationship;
  private String action; // ADD or REMOVE

  private List<RelationshipDto> toAdd;
  private List<RelationshipDto> toRemove;
}
