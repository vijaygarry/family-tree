package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationRequest;
import java.io.Serial;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetFamilyRegistrationListRequest extends OperationRequest {
  @Serial private static final long serialVersionUID = 1L;

  private String status;

  @Override
  public void normalize() {
    if (status != null) status = status.trim().toUpperCase();
  }
}
