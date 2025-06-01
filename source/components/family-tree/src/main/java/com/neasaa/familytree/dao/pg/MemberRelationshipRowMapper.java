/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.familytree.dao.pg;

import java.sql.SQLException;

import com.neasaa.base.app.dao.pg.AbstractDao;
import com.neasaa.familytree.entity.MemberRelationship;
import com.neasaa.familytree.enums.RelationshipType;

import java.sql.ResultSet;
import org.springframework.jdbc.core.RowMapper;

public class MemberRelationshipRowMapper implements RowMapper<MemberRelationship> {

	@Override
	public MemberRelationship mapRow(ResultSet aRs, int aRowNum) throws SQLException {
		MemberRelationship memberRelationship = new MemberRelationship();
		memberRelationship.setMemberId(aRs.getInt("MEMBERID"));
		memberRelationship.setRelationshipType(RelationshipType.getRelationshipType(aRs.getString("RELATIONSHIPTYPE")));
		memberRelationship.setRelatedMemberId(aRs.getInt("RELATEDMEMBERID"));
		memberRelationship.setCreatedBy(aRs.getInt("CREATEDBY"));
		memberRelationship.setCreatedDate(AbstractDao.getTimestampFromResultSet(aRs, "CREATEDDATE"));
		memberRelationship.setLastUpdatedBy(aRs.getInt("LASTUPDATEDBY"));
		memberRelationship.setLastUpdatedDate(AbstractDao.getTimestampFromResultSet(aRs, "LASTUPDATEDDATE"));
		return memberRelationship;
	}

}
