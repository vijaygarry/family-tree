package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationResponse;
import java.io.Serial;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetFamiliesByRegionResponse extends OperationResponse {
  @Serial private static final long serialVersionUID = 1L;

  private List<SearchFamilyDto> families;
  private int totalCount;
}
