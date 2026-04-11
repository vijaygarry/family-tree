package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationRequest;
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
public class ProcessFamilyRegistrationRequest extends OperationRequest {
  private int familyRegistrationId;
  private String action;

  @Override
  public void normalize() {
    if (action != null) {
      action = action.trim().toUpperCase();
    }
  }
}