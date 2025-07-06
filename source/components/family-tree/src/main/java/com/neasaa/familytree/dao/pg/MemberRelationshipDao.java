/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.familytree.dao.pg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;

import com.neasaa.base.app.dao.pg.AbstractDao;
import com.neasaa.familytree.entity.MemberRelationship;

@Repository
public class MemberRelationshipDao extends AbstractDao {

	private static final String SELECT_RELATIONSHIP_BY_ID = "select  MEMBERID , RELATIONSHIPTYPE , RELATEDMEMBERID , CREATEDBY , CREATEDDATE , LASTUPDATEDBY , LASTUPDATEDDATE  "
			+ "from " + BASE_SCHEMA_NAME + "MEMBERRELATIONSHIP "
					+ "where MEMBERID = ?";
	
	private static final String SELECT_RELATIONSHIP_BETWEEN_MEMBERS = "select  MEMBERID , RELATIONSHIPTYPE , RELATEDMEMBERID , CREATEDBY , CREATEDDATE , LASTUPDATEDBY , LASTUPDATEDDATE  "
			+ "from " + BASE_SCHEMA_NAME + "MEMBERRELATIONSHIP "
					+ "where MEMBERID = ? AND RELATEDMEMBERID = ?";
	
	public List<MemberRelationship> getRelationshipByMemberId (int memberId) {
		return getJdbcTemplate().query(SELECT_RELATIONSHIP_BY_ID, new MemberRelationshipRowMapper(), memberId);
	}
	
	public MemberRelationship getRelationshipBetweenMembers (int memberId, int relatedMemberId) {
		List<MemberRelationship> relationshipList = getJdbcTemplate().query(SELECT_RELATIONSHIP_BETWEEN_MEMBERS, new MemberRelationshipRowMapper(), memberId, relatedMemberId);
		 
		if(relationshipList == null || relationshipList.size() == 0) {
			return null;
		}
		if(relationshipList.size() > 1) {
			throw new RuntimeException("Invalid relationship between members");
		}
		return relationshipList.get(0);
	}

	public List<MemberRelationship> getRelationshipsForMember (int memberId) {
		return getJdbcTemplate().query(SELECT_RELATIONSHIP_BY_ID, new MemberRelationshipRowMapper(), memberId);
	}
	
	
	public int addMemberRelationship(MemberRelationship aMemberRelationship) {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
				return buildInsertStatement(aCon, aMemberRelationship);
			}
		});
	}
	
	private PreparedStatement buildInsertStatement(Connection aConection, MemberRelationship aMemberRelationship) throws SQLException {
		String sqlStatement = "INSERT INTO " + BASE_SCHEMA_NAME + "MEMBERRELATIONSHIP (MEMBERID, RELATIONSHIPTYPE, RELATEDMEMBERID, CREATEDBY, CREATEDDATE, LASTUPDATEDBY, LASTUPDATEDDATE) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";

		PreparedStatement prepareStatement = aConection.prepareStatement(sqlStatement);
		setIntInStatement(prepareStatement, 1, aMemberRelationship.getMemberId());
		setStringInStatement(prepareStatement, 2, aMemberRelationship.getRelationshipType().name());
		setIntInStatement(prepareStatement, 3, aMemberRelationship.getRelatedMemberId());
		setIntInStatement(prepareStatement, 4, aMemberRelationship.getCreatedBy());
		setTimestampInStatement(prepareStatement, 5, aMemberRelationship.getCreatedDate());
		setIntInStatement(prepareStatement, 6, aMemberRelationship.getLastUpdatedBy());
		setTimestampInStatement(prepareStatement, 7, aMemberRelationship.getLastUpdatedDate());
		return prepareStatement;
	}

	

	public int deleteMemberRelationship(MemberRelationship aMemberRelationship) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aConection) throws SQLException {
				String deleteSqlQuery = "DELETE FROM MEMBERRELATIONSHIP WHERE MEMBERID = ? and RELATEDMEMBERID = ?";
				PreparedStatement prepareStatement = aConection.prepareStatement(deleteSqlQuery);
				setIntInStatement(prepareStatement, 1, aMemberRelationship.getMemberId());
				setIntInStatement(prepareStatement, 2, aMemberRelationship.getRelatedMemberId());
				return prepareStatement;
			}
		});

	}

	public PreparedStatement buildUpdateStatement(Connection aConection, MemberRelationship aMemberRelationship) throws SQLException {
		String updateStatement = "UPDATE MEMBERRELATIONSHIP SET RELATIONSHIPTYPE = ? , CREATEDBY = ? , CREATEDDATE = ? , LASTUPDATEDBY = ? , LASTUPDATEDDATE = ?  where MEMBERID = ? and RELATEDMEMBERID = ?";

		PreparedStatement prepareStatement = aConection.prepareStatement(updateStatement);
		setStringInStatement(prepareStatement, 1, aMemberRelationship.getRelationshipType().name());
		setIntInStatement(prepareStatement, 2, aMemberRelationship.getCreatedBy());
		setTimestampInStatement(prepareStatement, 3, aMemberRelationship.getCreatedDate());
		setIntInStatement(prepareStatement, 4, aMemberRelationship.getLastUpdatedBy());
		setTimestampInStatement(prepareStatement, 5, aMemberRelationship.getLastUpdatedDate());
		setIntInStatement(prepareStatement, 6, aMemberRelationship.getMemberId());
		setIntInStatement(prepareStatement, 7, aMemberRelationship.getRelatedMemberId());
		return prepareStatement;
	}

	public int updateMemberRelationship(MemberRelationship aMemberRelationship) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
				return buildUpdateStatement(aCon, aMemberRelationship);
			}
		});

	}

}
