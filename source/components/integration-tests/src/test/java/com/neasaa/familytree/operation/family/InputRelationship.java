package com.neasaa.familytree.operation.family;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class InputRelationship {
	// This will be interpreted as 
	//new member is relationshipType of relatedMemberId
	String relationshipType; 
	int relatedMemberId;
	String relatedMemberFullName;
}
