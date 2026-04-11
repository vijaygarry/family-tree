package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationRequest;
import java.io.Serial;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetFamilyDetailsRequest extends OperationRequest {
  @Serial private static final long serialVersionUID = 1L;

  private Integer familyId;

  @Override
  public void normalize() {
  }
}
