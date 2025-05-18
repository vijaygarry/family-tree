/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.base.app.dao.pg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;

import com.neasaa.base.app.entity.AppRole;

@Repository
public class AppRoleDao extends AbstractDao {
	private static final String SELECT_ALL_ACTIVE_ROLES_QUERY = "select  ROLEID , ROLEDESC , ENABLE , "
			+ "CREATEDBY , CREATEDDATE , LASTUPDATEDBY , LASTUPDATEDDATE  "
			+ "from " + BASE_SCHEMA_NAME + "LKPROLE where enable = true";
	private static final String SELECT_OPERATIONS_FOR_A_ROLE_QUERY = "select  OPERATIONID "
			+ "from " + BASE_SCHEMA_NAME + "LKPROLEOPERATIONMAP where ROLEID = ?";
	
	public List<AppRole> getAllActiveRoles () {
		List<AppRole> roles =  getJdbcTemplate().query(SELECT_ALL_ACTIVE_ROLES_QUERY, new AppRoleRowMapper());
		List<AppRole> rolesWithOperaions = new ArrayList<>();
		
		for(AppRole role : roles) {
			List<String> operationIds = getJdbcTemplate().query(SELECT_OPERATIONS_FOR_A_ROLE_QUERY, new StringRowMapper(), role.getRoleId());
			
			AppRole roleWithOperations = new AppRole();
			roleWithOperations.setRoleId(role.getRoleId());
			roleWithOperations.setRoledesc(role.getRoledesc());
			roleWithOperations.setEnable(role.isEnable());
			roleWithOperations.setCreatedBy(role.getCreatedBy());
			roleWithOperations.setCreatedDate(role.getCreatedDate());
			roleWithOperations.setLastupdatedBy(role.getLastupdatedBy());
			roleWithOperations.setLastupdatedDate(role.getLastupdatedDate());
			roleWithOperations.setOperationIds(operationIds);
			
			rolesWithOperaions.add(roleWithOperations);
		}
		
		return rolesWithOperaions;
	}
	
	private PreparedStatement buildInsertStatement(Connection aConection, AppRole aAppRole) throws SQLException {
		String sqlStatement = "INSERT INTO LKPROLE (ROLEID, ROLEDESC, ENABLE, CREATEDBY, CREATEDDATE, LASTUPDATEDBY, LASTUPDATEDDATE) VALUES (?, ?, ?, ?, ?, ?, ?)";

		PreparedStatement prepareStatement = aConection.prepareStatement(sqlStatement);
		setStringInStatement(prepareStatement, 1, aAppRole.getRoleId());
		setStringInStatement(prepareStatement, 2, aAppRole.getRoledesc());
		setBooleanInStatement(prepareStatement, 3, aAppRole.isEnable());
		setIntInStatement(prepareStatement, 4, aAppRole.getCreatedBy());
		setTimestampInStatement(prepareStatement, 5, aAppRole.getCreatedDate());
		setIntInStatement(prepareStatement, 6, aAppRole.getLastupdatedBy());
		setTimestampInStatement(prepareStatement, 7, aAppRole.getLastupdatedDate());
		return prepareStatement;
	}

	public int insertAppRole(AppRole aAppRole) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
				return buildInsertStatement(aCon, aAppRole);
			}
		});

	}

	public int deleteAppRole(AppRole aAppRole) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aConection) throws SQLException {
				String deleteSqlQuery = "DELETE FROM LKPROLE WHERE ROLEID = ?";
				PreparedStatement prepareStatement = aConection.prepareStatement(deleteSqlQuery);
				setStringInStatement(prepareStatement, 1, aAppRole.getRoleId());
				return prepareStatement;
			}
		});

	}

	public PreparedStatement buildUpdateStatement(Connection aConection, AppRole aAppRole) throws SQLException {
		String updateStatement = "UPDATE LKPROLE SET ROLEDESC = ? , ENABLE = ? , CREATEDBY = ? , CREATEDDATE = ? , LASTUPDATEDBY = ? , LASTUPDATEDDATE = ?  where ROLEID = ?";

		PreparedStatement prepareStatement = aConection.prepareStatement(updateStatement);
		setStringInStatement(prepareStatement, 1, aAppRole.getRoledesc());
		setBooleanInStatement(prepareStatement, 2, aAppRole.isEnable());
		setIntInStatement(prepareStatement, 3, aAppRole.getCreatedBy());
		setTimestampInStatement(prepareStatement, 4, aAppRole.getCreatedDate());
		setIntInStatement(prepareStatement, 5, aAppRole.getLastupdatedBy());
		setTimestampInStatement(prepareStatement, 6, aAppRole.getLastupdatedDate());
		setStringInStatement(prepareStatement, 7, aAppRole.getRoleId());
		return prepareStatement;
	}

	public int updateAppRole(AppRole aAppRole) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
				return buildUpdateStatement(aCon, aAppRole);
			}
		});

	}

	public AppRole fetchAppRole(AppRole aAppRole) throws SQLException {
		String selectQuery = "select  ROLEID , ROLEDESC , ENABLE , CREATEDBY , CREATEDDATE , LASTUPDATEDBY , LASTUPDATEDDATE  from LKPROLE where ROLEID = ? ";
		return getJdbcTemplate().queryForObject(selectQuery, new AppRoleRowMapper(), aAppRole.getRoleId());

	}

}
