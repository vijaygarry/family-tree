package com.neasaa.familytree.operation.family.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelationshipDto {
	int memberId; // This is the memberId
	String memberName;
	// This will be interpreted as 
	//member's relationshipType is relatedMemberId
	// E.g. member (Vijay)'s son is relatedMemberId (Arav)
	String relationshipType;

	int relatedMemberId;
	String relatedMemberName;
}
