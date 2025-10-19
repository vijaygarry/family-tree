package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class UpdateFamilyDetailsResponse extends OperationResponse {
  private FamilyDetailsDto familyDetails;
}
