package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationResponse;
import java.io.Serial;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FamilyRegistrationResponse extends OperationResponse {

  @Serial private static final long serialVersionUID = 1L;

  private String surname;
  private int familyRegistrationId;
}