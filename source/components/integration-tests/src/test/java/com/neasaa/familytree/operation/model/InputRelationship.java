package com.neasaa.familytree.operation.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class InputRelationship {
	int memberId; // This is the memberId
	String memberName;
	// This will be interpreted as
	//member's relationshipType is relatedMemberId
	// E.g. member (Vijay)'s son is relatedMemberId (Arav)
	String relationshipType;

	int relatedMemberId;
	String relatedMemberName;
}
