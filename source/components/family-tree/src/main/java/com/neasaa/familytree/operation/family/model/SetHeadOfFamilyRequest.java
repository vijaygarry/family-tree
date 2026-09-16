package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationRequest;
import java.io.Serial;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetHeadOfFamilyRequest extends OperationRequest {

  @Serial private static final long serialVersionUID = 1748919840099L;

  private Integer familyId;
  private String familyName;
  private Integer memberId;
  private String memberName;

  @Override
  public void normalize() {
    if (familyName != null) {
      familyName = familyName.trim();
    }
    if (memberName != null) {
      memberName = memberName.trim();
    }
  }
}
