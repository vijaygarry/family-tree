package com.neasaa.familytree.operation.family.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RelationshipDto {
	// This will be interpreted as 
	//new member is relationshipType of relatedMemberId
	String relationshipType; 
	int relatedMemberId;
	String relatedMemberFullName;
}
