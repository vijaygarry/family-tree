package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterMarriageRequest extends OperationRequest {

  private int husbandId;
  private String husbandFirstName;
  private int wifeId;
  private String wifeFirstName;
  private String weddingDate; // optional, ISO date format (yyyy-MM-dd)

  @Override
  public void normalize() {
    if (husbandFirstName != null) {
      husbandFirstName = husbandFirstName.trim();
    }
    if (wifeFirstName != null) {
      wifeFirstName = wifeFirstName.trim();
    }
    if (weddingDate != null) {
      weddingDate = weddingDate.trim();
    }
  }
}
