package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationResponse;
import com.neasaa.familytree.entity.FamilyMemberEntity;
import com.neasaa.familytree.enums.Gender;
import com.neasaa.familytree.enums.MaritalStatus;
import lombok.Builder;
import lombok.Getter;

import java.io.Serial;
import java.util.Date;
import java.util.List;

@Getter
@Builder
public class GetMemberProfileResponse extends OperationResponse {
    @Serial
    private static final long serialVersionUID = -3478291045823901842L;

    private MemberProfileDto memberProfile;

    private List<MemberSummaryDto> parents;
    private MemberSummaryDto spouse;
    private List<MemberSummaryDto> children;
    private List<MemberSummaryDto> siblings;

}
