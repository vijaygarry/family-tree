/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.familytree.dao.pg;

import java.sql.SQLException;

import com.neasaa.familytree.entity.MemberRelationshipEntity;
import com.neasaa.familytree.enums.RelationshipType;

import java.sql.ResultSet;
import org.springframework.jdbc.core.RowMapper;

public class MemberRelationshipRowMapper implements RowMapper<MemberRelationshipEntity> {

	@Override
	public MemberRelationshipEntity mapRow(ResultSet aRs, int aRowNum) throws SQLException {
		MemberRelationshipEntity memberRelationship = new MemberRelationshipEntity();
		memberRelationship.setMemberId(aRs.getInt("MEMBERID"));
		memberRelationship.setRelationshipType(RelationshipType.getRelationshipType(aRs.getString("RELATIONSHIPTYPE")));
		memberRelationship.setRelatedMemberId(aRs.getInt("RELATEDMEMBERID"));
		return memberRelationship;
	}

}
