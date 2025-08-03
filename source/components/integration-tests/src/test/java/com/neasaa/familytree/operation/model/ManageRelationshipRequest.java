package com.neasaa.familytree.operation.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ManageRelationshipRequest {
    private List<InputRelationship> toAdd;
    private List<InputRelationship> toRemove;
}
