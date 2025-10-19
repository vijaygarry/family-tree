package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationResponse;
import java.io.Serial;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateFamilyMemberProfileResponse extends OperationResponse {
  @Serial private static final long serialVersionUID = -3478291045823901842L;

  private MemberProfileDto memberProfile;
}
