package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ManageRelationshipRequest extends OperationRequest {
    private List<RelationshipDto> toAdd;
    private List<RelationshipDto> toRemove;
}
