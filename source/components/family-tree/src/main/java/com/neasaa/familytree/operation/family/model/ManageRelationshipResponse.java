package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ManageRelationshipResponse extends OperationResponse {
    private List<String> addedResult;
    private List<String> removedResult;
}
