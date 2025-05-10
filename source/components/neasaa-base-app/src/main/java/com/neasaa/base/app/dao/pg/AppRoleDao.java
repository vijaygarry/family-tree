/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.base.app.dao.pg;

import java.sql.SQLException;
import java.sql.Connection;
import com.neasaa.base.app.entity.AppRole;
import org.springframework.jdbc.core.PreparedStatementCreator;
import java.sql.PreparedStatement;

public class AppRoleDao extends AbstractDao {

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
