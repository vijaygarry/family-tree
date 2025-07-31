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
	 *
	 * @param member
	 * @param relatedRelationshipType
	 * @param relatedMember
	 * @return
	 */
	public static List<MemberRelationship> buildRelationships (FamilyMember member, RelationshipType relatedRelationshipType, FamilyMember relatedMember, AuditInfo auditInfo) {
		if(relatedRelationshipType == RelationshipType.Son || relatedRelationshipType == RelationshipType.Daughter || relatedRelationshipType == RelationshipType.Wife) {
			MemberRelationship memberRelationship = MemberRelationship.builder()
					.memberId(member.getMemberId())
					.relationshipType(relatedRelationshipType)
					.relatedMemberId(relatedMember.getMemberId())
					.createdBy(auditInfo.getCreatedBy())
					.createdDate(auditInfo.getCreatedDate())
					.lastUpdatedBy(auditInfo.getLastUpdatedBy())
					.lastUpdatedDate(auditInfo.getLastUpdatedDate())
					.build();
			return List.of(memberRelationship);
		}

		 if(relatedRelationshipType == RelationshipType.Husband) {
			 MemberRelationship memberRelationship = MemberRelationship.builder()
					 .memberId(relatedMember.getMemberId())
					 .relationshipType(RelationshipType.Wife)
					 .relatedMemberId(member.getMemberId())
					 .createdBy(auditInfo.getCreatedBy())
					 .createdDate(auditInfo.getCreatedDate())
					 .lastUpdatedBy(auditInfo.getLastUpdatedBy())
					 .lastUpdatedDate(auditInfo.getLastUpdatedDate())
					 .build();
			 return List.of(memberRelationship);
		 }

		RelationshipType reverseRelationshipType = null;
		if(relatedRelationshipType == RelationshipType.Father || relatedRelationshipType == RelationshipType.Mother) {
			if(member.getGender() == Gender.Male) {
				reverseRelationshipType = RelationshipType.Son;
			}
			if(member.getGender() == Gender.Female) {
				reverseRelationshipType = RelationshipType.Daughter;
			}
			MemberRelationship memberRelationship = MemberRelationship.builder()
					.memberId(relatedMember.getMemberId())
					.relationshipType(reverseRelationshipType)
					.relatedMemberId(member.getMemberId())
					.createdBy(auditInfo.getCreatedBy())
					.createdDate(auditInfo.getCreatedDate())
					.lastUpdatedBy(auditInfo.getLastUpdatedBy())
					.lastUpdatedDate(auditInfo.getLastUpdatedDate())
					.build();
			return List.of(memberRelationship);
		}
		throw new IllegalArgumentException("Invalid relationship type: " + relatedRelationshipType);

//		RelationshipType reverseRelationshipType = null;
//		switch(relatedRelationshipType) {
//		case Father:
//		case Mother:
//			if(relatedMember.getGender() == Gender.Male) {
//				reverseRelationshipType = RelationshipType.Son;
//			}
//			if(relatedMember.getGender() == Gender.Female) {
//				reverseRelationshipType = RelationshipType.Daughter;
//			}
//			break;
//		case Son:
//		case Daughter:
//			if(relatedMember.getGender() == Gender.Male) {
//				reverseRelationshipType = RelationshipType.Father;
//			}
//			if(relatedMember.getGender() == Gender.Female) {
//				reverseRelationshipType = RelationshipType.Mother;
//			}
//			break;
//		case Husband:
//			reverseRelationshipType = RelationshipType.Wife;
//			break;
//		case Wife:
//			reverseRelationshipType = RelationshipType.Husband;
//			break;
//		}
//
//		List<MemberRelationship> relationships = new ArrayList<>();
//		MemberRelationship memberRelationship = MemberRelationship.builder()
//				.memberId(member.getMemberId())
//				.relationshipType(relatedRelationshipType)
//				.relatedMemberId(relatedMember.getMemberId())
//				.createdBy(auditInfo.getCreatedBy())
//				.createdDate(auditInfo.getCreatedDate())
//				.lastUpdatedBy(auditInfo.getLastUpdatedBy())
//				.lastUpdatedDate(auditInfo.getLastUpdatedDate())
//				.build();
//		relationships.add(memberRelationship);
//
//		MemberRelationship reverseRelationship = MemberRelationship.builder()
//				.memberId(relatedMember.getMemberId())
//				.relationshipType(reverseRelationshipType)
//				.relatedMemberId(member.getMemberId())
//				.createdBy(auditInfo.getCreatedBy())
//				.createdDate(auditInfo.getCreatedDate())
//				.lastUpdatedBy(auditInfo.getLastUpdatedBy())
//				.lastUpdatedDate(auditInfo.getLastUpdatedDate())
//				.build();
//		relationships.add(reverseRelationship);
//		return relationships;
	}
}
