package com.neasaa.familytree.utils;

import java.util.ArrayList;
import java.util.List;

import com.neasaa.base.app.operation.AuditInfo;
import com.neasaa.familytree.entity.FamilyMember;
import com.neasaa.familytree.entity.MemberRelationship;
import com.neasaa.familytree.enums.RelationshipType;
import com.neasaa.familytree.enums.Gender;

public class RelationshipUtils {
	
	public static FamilyMember findMemberByIdInList (List<FamilyMember> familyMembers, int memberId) {
		for(FamilyMember memberFromFamily : familyMembers) {
			if(memberFromFamily.getMemberId() == memberId) {
				return memberFromFamily;
			}
		}
		return null;
	}
	
	/**
	 * Input parameter is interpreted as 
	 * member is relatedRelationshipType of relatedMember
	 * 
	 * Base on this, 2 relationship will be built.
	 * E.g. 
	 * x is son of y will result in 
	 * x is son of y and 
	 * y is father of x
	 * 
	 * @param member
	 * @param relatedRelationshipType
	 * @param relatedMember
	 * @return
	 */
	public static List<MemberRelationship> buildRelationships (FamilyMember member, RelationshipType relatedRelationshipType, FamilyMember relatedMember, AuditInfo auditInfo) {
		
		RelationshipType reverseRelationshipType = null;
		switch(relatedRelationshipType) {
		case Father:
		case Mother:
			if(relatedMember.getGender() == Gender.Male) {
				reverseRelationshipType = RelationshipType.Son;
			}
			if(relatedMember.getGender() == Gender.Female) {
				reverseRelationshipType = RelationshipType.Daughter;
			}
			break;
		case Son:
		case Daughter:
			if(relatedMember.getGender() == Gender.Male) {
				reverseRelationshipType = RelationshipType.Father;
			}
			if(relatedMember.getGender() == Gender.Female) {
				reverseRelationshipType = RelationshipType.Mother;
			}
			break;
		case Husband:
			reverseRelationshipType = RelationshipType.Wife;
			break;
		case Wife:
			reverseRelationshipType = RelationshipType.Husband;
			break;
		}
		
		List<MemberRelationship> relationships = new ArrayList<>();
		MemberRelationship memberRelationship = MemberRelationship.builder()
				.memberId(member.getMemberId())
				.relationshipType(relatedRelationshipType)
				.relatedMemberId(relatedMember.getMemberId())
				.createdBy(auditInfo.getCreatedBy())
				.createdDate(auditInfo.getCreatedDate())
				.lastUpdatedBy(auditInfo.getLastUpdatedBy())
				.lastUpdatedDate(auditInfo.getLastUpdatedDate())
				.build();
		relationships.add(memberRelationship);
		
		MemberRelationship reverseRelationship = MemberRelationship.builder()
				.memberId(relatedMember.getMemberId())
				.relationshipType(reverseRelationshipType)
				.relatedMemberId(member.getMemberId())
				.createdBy(auditInfo.getCreatedBy())
				.createdDate(auditInfo.getCreatedDate())
				.lastUpdatedBy(auditInfo.getLastUpdatedBy())
				.lastUpdatedDate(auditInfo.getLastUpdatedDate())
				.build();
		relationships.add(reverseRelationship);
		return relationships;
	}
}
