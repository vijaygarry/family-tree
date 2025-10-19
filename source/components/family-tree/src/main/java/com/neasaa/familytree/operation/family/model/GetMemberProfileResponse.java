package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationResponse;
import java.io.Serial;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetMemberProfileResponse extends OperationResponse {
  @Serial private static final long serialVersionUID = -3478291045823901842L;

  private MemberProfileDto memberProfile;

  private List<MemberSummaryDto> parents;
  private MemberSummaryDto spouse;
  private List<MemberSummaryDto> children;
  private List<MemberSummaryDto> siblings;
}
