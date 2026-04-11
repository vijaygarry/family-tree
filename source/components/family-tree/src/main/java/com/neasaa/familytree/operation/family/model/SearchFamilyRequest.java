package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchFamilyRequest extends OperationRequest {
  private String searchString;

  @Override
  public void normalize() {
    if (searchString != null) {
      searchString = searchString.trim();
    }
  }
}
