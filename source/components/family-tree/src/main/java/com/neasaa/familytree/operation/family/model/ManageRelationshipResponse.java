package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationResponse;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ManageRelationshipResponse extends OperationResponse {
  private List<String> addedResult;
  private List<String> removedResult;
}
