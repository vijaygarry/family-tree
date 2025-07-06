package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetMemberProfileRequest extends OperationRequest {

    private static final long serialVersionUID = -3478291045823901842L;

    private Integer memberId;
}
