package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchFamilyRequest extends OperationRequest {
  private static final int DEFAULT_PAGE_SIZE = 50;

  private String searchString;
  private int page = 0;
  private int pageSize = DEFAULT_PAGE_SIZE;

  @Override
  public void normalize() {
    if (searchString != null) {
      searchString = searchString.trim();
    }
    if (pageSize <= 0) {
      pageSize = DEFAULT_PAGE_SIZE;
    }
    if (page < 0) {
      page = 0;
    }
  }
}
