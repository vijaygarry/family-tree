package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationRequest;
import java.io.Serial;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MarkAsDeceasedRequest extends OperationRequest {

  @Serial private static final long serialVersionUID = 7831045692014738291L;

  private Integer memberId;
  private String memberName;
  private Integer familyId;
  private String dateOfDeath;

  @Override
  public void normalize() {
    if (memberName != null) {
      memberName = memberName.trim();
    }
    if (dateOfDeath != null) {
      dateOfDeath = dateOfDeath.trim();
    }
  }
}
