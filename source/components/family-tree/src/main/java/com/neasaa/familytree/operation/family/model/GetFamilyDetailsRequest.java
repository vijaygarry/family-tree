package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationRequest;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter
@Setter
public class GetFamilyDetailsRequest extends OperationRequest {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer familyId;
}
