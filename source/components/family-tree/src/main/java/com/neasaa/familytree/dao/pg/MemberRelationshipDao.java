/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.familytree.dao.pg;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.neasaa.base.app.operation.AuditInfo;
import com.neasaa.familytree.entity.MemberRelationshipEntity;
import com.neasaa.familytree.enums.RelationshipType;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;

import com.neasaa.base.app.dao.pg.AbstractDao;

@Log4j2
@Repository
public class MemberRelationshipDao extends AbstractDao {

	private static final String SELECT_RELATIONSHIP_BY_ID = "select  MEMBERID , RELATIONSHIPTYPE , RELATEDMEMBERID , CREATEDBY , CREATEDDATE , LASTUPDATEDBY , LASTUPDATEDDATE  "
			+ "from " + BASE_SCHEMA_NAME + "MEMBERRELATIONSHIP "
					+ "where MEMBERID = ? ";

	private static final String SELECT_RELATIONSHIP_BY_ID_AND_RELATION_TYPE = "select  MEMBERID , RELATIONSHIPTYPE , RELATEDMEMBERID , CREATEDBY , CREATEDDATE , LASTUPDATEDBY , LASTUPDATEDDATE  "
			+ "from " + BASE_SCHEMA_NAME + "MEMBERRELATIONSHIP "
			+ "where MEMBERID = ANY(?) and RELATIONSHIPTYPE = ANY(?) ";

	private static final String SELECT_RELATIONSHIP_BY_RELATED_ID_AND_RELATION_TYPE = "select  MEMBERID , RELATIONSHIPTYPE , RELATEDMEMBERID , CREATEDBY , CREATEDDATE , LASTUPDATEDBY , LASTUPDATEDDATE  "
			+ "from " + BASE_SCHEMA_NAME + "MEMBERRELATIONSHIP "
			+ "where RELATEDMEMBERID = ? and RELATIONSHIPTYPE = ANY(?) ";
	
	private static final String SELECT_RELATIONSHIP_BETWEEN_MEMBERS = "select  MEMBERID , RELATIONSHIPTYPE , RELATEDMEMBERID , CREATEDBY , CREATEDDATE , LASTUPDATEDBY , LASTUPDATEDDATE  "
			+ "from " + BASE_SCHEMA_NAME + "MEMBERRELATIONSHIP "
					+ "where MEMBERID = ? AND RELATEDMEMBERID = ?";

	private static final String SELECT_SPOUSE_FOR_FAMILY_MEMBER_BY_ID = "select  MEMBERID , RELATIONSHIPTYPE , RELATEDMEMBERID  "
			+ "from " + BASE_SCHEMA_NAME + "MEMBERRELATIONSHIP "
			+ "where relationshiptype IN ('Husband', 'Wife') and (memberId = ? or relatedmemberid = ?)";

	private static final String SELECT_CHILDREN_FOR_MEMBER_BY_ID = "select  MEMBERID , RELATIONSHIPTYPE , RELATEDMEMBERID  "
			+ "from " + BASE_SCHEMA_NAME + "MEMBERRELATIONSHIP "
			+ "where relationshiptype IN ('Son', 'Daughter') and MEMBERID = ANY(?)";

	// This query will return only one parent for a family member. Other parent can be found by finding spouse of first parent.
	private static final String SELECT_PARENTS_FOR_FAMILY_MEMBER_BY_ID = "select  MEMBERID , RELATIONSHIPTYPE , RELATEDMEMBERID  "
			+ "from " + BASE_SCHEMA_NAME + "MEMBERRELATIONSHIP "
			+ "where relationshiptype IN ('Son', 'Daughter') and relatedmemberid = ?";


	/**
	 * This method returns all the relationships define with member id or related member id.
	 * This method is called from GetMemberProfile API to get all the relationships for selected member.
	 *
	 * @param memberId
	 * @return
	 */
	public List<MemberRelationshipEntity> getRelationshipByMemberIdOrRelatedMemberId (int memberId) {
		return getJdbcTemplate().query(SELECT_RELATIONSHIP_BETWEEN_MEMBERS, new MemberRelationshipRowMapper(), memberId, memberId);
	}

	public List<MemberRelationshipEntity> getRelatedMembersByIdAndRelationType (List<Integer> memberIds, List<RelationshipType>  relationshipTypes) {
		return getJdbcTemplate().query(
				new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
						PreparedStatement ps = connection.prepareStatement(SELECT_RELATIONSHIP_BY_ID_AND_RELATION_TYPE);
						// Convert list to SQL array
						Array memberIdArray = connection.createArrayOf("INTEGER", memberIds.toArray());
						Array relationshipTypeArray = connection.createArrayOf("VARCHAR", relationshipTypes.toArray());
						ps.setArray(1, memberIdArray);
						ps.setArray(2, relationshipTypeArray);
						log.info("Executing query: " + ps.toString());
						return ps;
					}
				},
				new MemberRelationshipRowMapper()
		);
	}

	public List<MemberRelationshipEntity> getRelationByRelatedIdAndRelationType (int relatedMemberId, List<RelationshipType>  relationshipTypes) {
		return getJdbcTemplate().query(
				new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
						PreparedStatement ps = connection.prepareStatement(SELECT_RELATIONSHIP_BY_RELATED_ID_AND_RELATION_TYPE);
						// Convert list to SQL array
						Array relationshipTypeArray = connection.createArrayOf("VARCHAR", relationshipTypes.toArray());
						setIntInStatement(ps, 1, relatedMemberId);
						ps.setArray(2, relationshipTypeArray);
						log.info("Executing query: " + ps.toString());
						return ps;
					}
				},
				new MemberRelationshipRowMapper()
		);
	}
	
	public MemberRelationshipEntity getRelationshipBetweenMembers (int memberId, int relatedMemberId) {
		List<MemberRelationshipEntity> relationshipList = getJdbcTemplate().query(SELECT_RELATIONSHIP_BETWEEN_MEMBERS, new MemberRelationshipRowMapper(), memberId, relatedMemberId);

		if(relationshipList == null || relationshipList.size() == 0) {
			return null;
		}
		if(relationshipList.size() > 1) {
			throw new RuntimeException("Invalid relationship between members");
		}
		return relationshipList.get(0);
	}

	public List<MemberRelationshipEntity> getParentsForMemberById (int memberId) {
		List<MemberRelationshipEntity> relationshipList = getJdbcTemplate().query(SELECT_PARENTS_FOR_FAMILY_MEMBER_BY_ID, new MemberRelationshipRowMapper(), memberId);

		if(relationshipList.isEmpty()) {
			return null;
		}
		if(relationshipList.size() == 2) {
			//Both parents are found, return both.
			return relationshipList;
		}
		MemberRelationshipEntity firstParent = relationshipList.get(0);
		// If only one parent is found, return found other parent.
		MemberRelationshipEntity spouseForFirstParent = getSpouseForMemberById(firstParent.getMemberId());
		if(spouseForFirstParent != null) {
			// getSpouseForMemberById returns relatedMemberId as spouse member id, so create new relationship for second parent
			MemberRelationshipEntity secondParent = MemberRelationshipEntity.builder().memberId(spouseForFirstParent.getRelatedMemberId())
					.relationshipType(firstParent.getRelationshipType()) // both parents will have same relationship type
					.relatedMemberId(firstParent.getRelatedMemberId())
					.build();
			relationshipList.add(secondParent);
		}
		return relationshipList;
	}

	/**
	 * This method returns spouse for a family member.
	 * Return relationship will have memberId as specified member id and relatedMemberId as spouse member id.
	 * If spouse is not found, it returns null.
	 * @param memberId
	 * @return
	 */
	public MemberRelationshipEntity getSpouseForMemberById (int memberId) {
		List<MemberRelationshipEntity> relationshipList = getJdbcTemplate().query(SELECT_SPOUSE_FOR_FAMILY_MEMBER_BY_ID, new MemberRelationshipRowMapper(), memberId, memberId);

		if(relationshipList.isEmpty()) {
			return null;
		}
		for (MemberRelationshipEntity relationship : relationshipList) {
			if (relationship.getMemberId() == memberId) {
				return relationship;
			} else if (relationship.getRelatedMemberId() == memberId) {
				return MemberRelationshipEntity.inverseSpouseRelationship(relationship);
			}
		}
		throw new RuntimeException("Invalid relationship configuration for member " + memberId);
	}

	public List<MemberRelationshipEntity> getChildrenForMemberById (int parent1Id, int parent2Id) {
		List<Integer> memberIds = new ArrayList<>();
		if (parent1Id > 0) {
			memberIds.add(parent1Id);
		}
		if (parent2Id > 0) {
			memberIds.add(parent2Id);
		}

		List<MemberRelationshipEntity> relationshipList = getJdbcTemplate().query(
				new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
						PreparedStatement ps = connection.prepareStatement(SELECT_CHILDREN_FOR_MEMBER_BY_ID);
						// Convert list to SQL array
						Array memberIdArray = connection.createArrayOf("INTEGER", memberIds.toArray());
						ps.setArray(1, memberIdArray);
						return ps;
					}
				},
				new MemberRelationshipRowMapper()
		);

		if(relationshipList.isEmpty()) {
			return null;
		}

		return relationshipList;
	}

	/**
	 * This method is called recursively to build family tree from GetFamilyDetails API
	 * It returns all the relationships for a member.
	 * @param memberId
	 * @return
	 */
	public List<MemberRelationshipEntity> getRelationshipsForMember (int memberId) {
		return getJdbcTemplate().query(SELECT_RELATIONSHIP_BY_ID, new MemberRelationshipRowMapper(), memberId);
	}
	
	
	public int addMemberRelationship(MemberRelationshipEntity aMemberRelationship, AuditInfo auditInfo) {
		try {
			return getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
					return buildInsertStatement(aCon, aMemberRelationship, auditInfo);
				}
			});
		} catch (Exception ex) {
			log.error("Error while adding member relationship for memberId: " + aMemberRelationship.getMemberId()
					+ ", relatedMemberId: " + aMemberRelationship.getRelatedMemberId()
					+ ", relationshipType: " + aMemberRelationship.getRelationshipType(), ex);
			throw new RuntimeException("Error while adding member relationship.");

		}
	}
	
	private PreparedStatement buildInsertStatement(Connection aConection, MemberRelationshipEntity aMemberRelationship, AuditInfo auditInfo) throws SQLException {
		String sqlStatement = "INSERT INTO " + BASE_SCHEMA_NAME + "MEMBERRELATIONSHIP (MEMBERID, RELATIONSHIPTYPE, RELATEDMEMBERID, CREATEDBY, CREATEDDATE, LASTUPDATEDBY, LASTUPDATEDDATE) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";

		PreparedStatement prepareStatement = aConection.prepareStatement(sqlStatement);
		setIntInStatement(prepareStatement, 1, aMemberRelationship.getMemberId());
		setStringInStatement(prepareStatement, 2, aMemberRelationship.getRelationshipType().name());
		setIntInStatement(prepareStatement, 3, aMemberRelationship.getRelatedMemberId());
		setIntInStatement(prepareStatement, 4, auditInfo.getCreatedBy());
		setTimestampInStatement(prepareStatement, 5, auditInfo.getCreatedDate());
		setIntInStatement(prepareStatement, 6, auditInfo.getLastUpdatedBy());
		setTimestampInStatement(prepareStatement, 7, auditInfo.getLastUpdatedDate());
		return prepareStatement;
	}

	

	public int deleteMemberRelationship(MemberRelationshipEntity aMemberRelationship) throws SQLException {
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

	public PreparedStatement buildUpdateStatement(Connection aConection, MemberRelationshipEntity aMemberRelationship) throws SQLException {
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

	public int updateMemberRelationship(MemberRelationshipEntity aMemberRelationship) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
				return buildUpdateStatement(aCon, aMemberRelationship);
			}
		});

	}

}
