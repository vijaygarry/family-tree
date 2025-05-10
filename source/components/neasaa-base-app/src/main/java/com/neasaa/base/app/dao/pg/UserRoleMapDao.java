/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.base.app.dao.pg;

import java.sql.SQLException;
import java.sql.Connection;
import com.neasaa.base.app.entity.UserRoleMap;
import org.springframework.jdbc.core.PreparedStatementCreator;
import java.sql.PreparedStatement;

public class UserRoleMapDao extends AbstractDao {

	private PreparedStatement buildInsertStatement(Connection aConection, UserRoleMap aUserRoleMap) throws SQLException {
		String sqlStatement = "INSERT INTO MSTUSERROLEMAP (USERID, ROLEID, CREATEDBY, CREATEDDATE, LASTUPDATEDBY, LASTUPDATEDDATE) VALUES (?, ?, ?, ?, ?, ?)";

		PreparedStatement prepareStatement = aConection.prepareStatement(sqlStatement);
		setIntInStatement(prepareStatement, 1, aUserRoleMap.getUserId());
		setStringInStatement(prepareStatement, 2, aUserRoleMap.getRoleId());
		setIntInStatement(prepareStatement, 3, aUserRoleMap.getCreatedBy());
		setTimestampInStatement(prepareStatement, 4, aUserRoleMap.getCreatedDate());
		setIntInStatement(prepareStatement, 5, aUserRoleMap.getLastupdatedBy());
		setTimestampInStatement(prepareStatement, 6, aUserRoleMap.getLastupdatedDate());
		return prepareStatement;
	}

	public int insertUserRoleMap(UserRoleMap aUserRoleMap) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
				return buildInsertStatement(aCon, aUserRoleMap);
			}
		});

	}

	public int deleteUserRoleMap(UserRoleMap aUserRoleMap) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aConection) throws SQLException {
				String deleteSqlQuery = "DELETE FROM MSTUSERROLEMAP WHERE USERID = ? and ROLEID = ?";
				PreparedStatement prepareStatement = aConection.prepareStatement(deleteSqlQuery);
				setIntInStatement(prepareStatement, 1, aUserRoleMap.getUserId());
				setStringInStatement(prepareStatement, 2, aUserRoleMap.getRoleId());
				return prepareStatement;
			}
		});

	}

	public PreparedStatement buildUpdateStatement(Connection aConection, UserRoleMap aUserRoleMap) throws SQLException {
		String updateStatement = "UPDATE MSTUSERROLEMAP SET CREATEDBY = ? , CREATEDDATE = ? , LASTUPDATEDBY = ? , LASTUPDATEDDATE = ?  where USERID = ? and ROLEID = ?";

		PreparedStatement prepareStatement = aConection.prepareStatement(updateStatement);
		setIntInStatement(prepareStatement, 1, aUserRoleMap.getCreatedBy());
		setTimestampInStatement(prepareStatement, 2, aUserRoleMap.getCreatedDate());
		setIntInStatement(prepareStatement, 3, aUserRoleMap.getLastupdatedBy());
		setTimestampInStatement(prepareStatement, 4, aUserRoleMap.getLastupdatedDate());
		setIntInStatement(prepareStatement, 5, aUserRoleMap.getUserId());
		setStringInStatement(prepareStatement, 6, aUserRoleMap.getRoleId());
		return prepareStatement;
	}

	public int updateUserRoleMap(UserRoleMap aUserRoleMap) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
				return buildUpdateStatement(aCon, aUserRoleMap);
			}
		});

	}

	public UserRoleMap fetchUserRoleMap(UserRoleMap aUserRoleMap) throws SQLException {
		String selectQuery = "select  USERID , ROLEID , CREATEDBY , CREATEDDATE , LASTUPDATEDBY , LASTUPDATEDDATE  from MSTUSERROLEMAP where USERID = ?  and ROLEID = ? ";
		return getJdbcTemplate().queryForObject(selectQuery, new UserRoleMapRowMapper(), aUserRoleMap.getUserId(), aUserRoleMap.getRoleId());

	}

}
