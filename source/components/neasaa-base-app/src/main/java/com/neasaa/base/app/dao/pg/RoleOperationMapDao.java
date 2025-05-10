/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.base.app.dao.pg;

import java.sql.SQLException;
import java.sql.Connection;
import com.neasaa.base.app.entity.RoleOperationMap;
import org.springframework.jdbc.core.PreparedStatementCreator;
import java.sql.PreparedStatement;

public class RoleOperationMapDao extends AbstractDao {

	private PreparedStatement buildInsertStatement(Connection aConection, RoleOperationMap aRoleOperationMap) throws SQLException {
		String sqlStatement = "INSERT INTO LKPROLEOPERATIONMAP (ROLEID, OPERATIONID, CREATEDBY, CREATEDDATE, LASTUPDATEDBY, LASTUPDATEDDATE) VALUES (?, ?, ?, ?, ?, ?)";

		PreparedStatement prepareStatement = aConection.prepareStatement(sqlStatement);
		setStringInStatement(prepareStatement, 1, aRoleOperationMap.getRoleId());
		setStringInStatement(prepareStatement, 2, aRoleOperationMap.getOperationId());
		setIntInStatement(prepareStatement, 3, aRoleOperationMap.getCreatedBy());
		setTimestampInStatement(prepareStatement, 4, aRoleOperationMap.getCreatedDate());
		setIntInStatement(prepareStatement, 5, aRoleOperationMap.getLastupdatedBy());
		setTimestampInStatement(prepareStatement, 6, aRoleOperationMap.getLastupdatedDate());
		return prepareStatement;
	}

	public int insertRoleOperationMap(RoleOperationMap aRoleOperationMap) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
				return buildInsertStatement(aCon, aRoleOperationMap);
			}
		});

	}

	public int deleteRoleOperationMap(RoleOperationMap aRoleOperationMap) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aConection) throws SQLException {
				String deleteSqlQuery = "DELETE FROM LKPROLEOPERATIONMAP WHERE ROLEID = ? and OPERATIONID = ?";
				PreparedStatement prepareStatement = aConection.prepareStatement(deleteSqlQuery);
				setStringInStatement(prepareStatement, 1, aRoleOperationMap.getRoleId());
				setStringInStatement(prepareStatement, 2, aRoleOperationMap.getOperationId());
				return prepareStatement;
			}
		});

	}

	public PreparedStatement buildUpdateStatement(Connection aConection, RoleOperationMap aRoleOperationMap) throws SQLException {
		String updateStatement = "UPDATE LKPROLEOPERATIONMAP SET CREATEDBY = ? , CREATEDDATE = ? , LASTUPDATEDBY = ? , LASTUPDATEDDATE = ?  where ROLEID = ? and OPERATIONID = ?";

		PreparedStatement prepareStatement = aConection.prepareStatement(updateStatement);
		setIntInStatement(prepareStatement, 1, aRoleOperationMap.getCreatedBy());
		setTimestampInStatement(prepareStatement, 2, aRoleOperationMap.getCreatedDate());
		setIntInStatement(prepareStatement, 3, aRoleOperationMap.getLastupdatedBy());
		setTimestampInStatement(prepareStatement, 4, aRoleOperationMap.getLastupdatedDate());
		setStringInStatement(prepareStatement, 5, aRoleOperationMap.getRoleId());
		setStringInStatement(prepareStatement, 6, aRoleOperationMap.getOperationId());
		return prepareStatement;
	}

	public int updateRoleOperationMap(RoleOperationMap aRoleOperationMap) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
				return buildUpdateStatement(aCon, aRoleOperationMap);
			}
		});

	}

	public RoleOperationMap fetchRoleOperationMap(RoleOperationMap aRoleOperationMap) throws SQLException {
		String selectQuery = "select  ROLEID , OPERATIONID , CREATEDBY , CREATEDDATE , LASTUPDATEDBY , LASTUPDATEDDATE  from LKPROLEOPERATIONMAP where ROLEID = ?  and OPERATIONID = ? ";
		return getJdbcTemplate().queryForObject(selectQuery, new RoleOperationMapRowMapper(), aRoleOperationMap.getRoleId(), aRoleOperationMap.getOperationId());

	}

}
