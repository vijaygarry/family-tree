/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.base.app.dao.pg;

import java.sql.SQLException;
import java.sql.Connection;
import com.neasaa.base.app.entity.AppUser;
import org.springframework.jdbc.core.PreparedStatementCreator;
import java.sql.PreparedStatement;

public class AppUserDao extends AbstractDao {

	private PreparedStatement buildInsertStatement(Connection aConection, AppUser aAppUser) throws SQLException {
		String sqlStatement = "INSERT INTO APPUSER (LOGONNAME, HASHPASSWORD, FIRSTNAME, LASTNAME, EMAILID, AUTHENTICATIONTYPE, SINGLESIGNONID, INVALIDLOGINATTEMPTS, LASTLOGINTIME, LASTPASSWORDCHANGETIME, STATUS, CREATEDBY, CREATEDDATE, LASTUPDATEDBY, LASTUPDATEDDATE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		PreparedStatement prepareStatement = aConection.prepareStatement(sqlStatement);
		setStringInStatement(prepareStatement, 1, aAppUser.getLogonName());
		setStringInStatement(prepareStatement, 2, aAppUser.getHashPassword());
		setStringInStatement(prepareStatement, 3, aAppUser.getFirstName());
		setStringInStatement(prepareStatement, 4, aAppUser.getLastName());
		setStringInStatement(prepareStatement, 5, aAppUser.getEmailId());
		setStringInStatement(prepareStatement, 6, aAppUser.getAuthenticationType());
		setStringInStatement(prepareStatement, 7, aAppUser.getSingleSignonId());
		setIntInStatement(prepareStatement, 8, aAppUser.getInvalidLoginAttempts());
		setTimestampInStatement(prepareStatement, 9, aAppUser.getLastLoginTime());
		setTimestampInStatement(prepareStatement, 10, aAppUser.getLastPasswordChangeTime());
		setStringInStatement(prepareStatement, 11, aAppUser.getStatus());
		setIntInStatement(prepareStatement, 12, aAppUser.getCreatedBy());
		setTimestampInStatement(prepareStatement, 13, aAppUser.getCreatedDate());
		setIntInStatement(prepareStatement, 14, aAppUser.getLastUpdatedBy());
		setTimestampInStatement(prepareStatement, 15, aAppUser.getLastUpdatedDate());
		return prepareStatement;
	}

	public int insertAppUser(AppUser aAppUser) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
				return buildInsertStatement(aCon, aAppUser);
			}
		});

	}

	public int deleteAppUser(AppUser aAppUser) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aConection) throws SQLException {
				String deleteSqlQuery = "DELETE FROM APPUSER WHERE USERID = ?";
				PreparedStatement prepareStatement = aConection.prepareStatement(deleteSqlQuery);
				setIntInStatement(prepareStatement, 1, aAppUser.getUserId());
				return prepareStatement;
			}
		});

	}

	public PreparedStatement buildUpdateStatement(Connection aConection, AppUser aAppUser) throws SQLException {
		String updateStatement = "UPDATE APPUSER SET LOGONNAME = ? , HASHPASSWORD = ? , FIRSTNAME = ? , LASTNAME = ? , EMAILID = ? , AUTHENTICATIONTYPE = ? , SINGLESIGNONID = ? , INVALIDLOGINATTEMPTS = ? , LASTLOGINTIME = ? , LASTPASSWORDCHANGETIME = ? , STATUS = ? , CREATEDBY = ? , CREATEDDATE = ? , LASTUPDATEDBY = ? , LASTUPDATEDDATE = ?  where USERID = ?";

		PreparedStatement prepareStatement = aConection.prepareStatement(updateStatement);
		setStringInStatement(prepareStatement, 1, aAppUser.getLogonName());
		setStringInStatement(prepareStatement, 2, aAppUser.getHashPassword());
		setStringInStatement(prepareStatement, 3, aAppUser.getFirstName());
		setStringInStatement(prepareStatement, 4, aAppUser.getLastName());
		setStringInStatement(prepareStatement, 5, aAppUser.getEmailId());
		setStringInStatement(prepareStatement, 6, aAppUser.getAuthenticationType());
		setStringInStatement(prepareStatement, 7, aAppUser.getSingleSignonId());
		setIntInStatement(prepareStatement, 8, aAppUser.getInvalidLoginAttempts());
		setTimestampInStatement(prepareStatement, 9, aAppUser.getLastLoginTime());
		setTimestampInStatement(prepareStatement, 10, aAppUser.getLastPasswordChangeTime());
		setStringInStatement(prepareStatement, 11, aAppUser.getStatus());
		setIntInStatement(prepareStatement, 12, aAppUser.getCreatedBy());
		setTimestampInStatement(prepareStatement, 13, aAppUser.getCreatedDate());
		setIntInStatement(prepareStatement, 14, aAppUser.getLastUpdatedBy());
		setTimestampInStatement(prepareStatement, 15, aAppUser.getLastUpdatedDate());
		setIntInStatement(prepareStatement, 16, aAppUser.getUserId());
		return prepareStatement;
	}

	public int updateAppUser(AppUser aAppUser) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
				return buildUpdateStatement(aCon, aAppUser);
			}
		});

	}

	public AppUser fetchAppUser(AppUser aAppUser) throws SQLException {
		String selectQuery = "select  USERID , LOGONNAME , HASHPASSWORD , FIRSTNAME , LASTNAME , EMAILID , AUTHENTICATIONTYPE , SINGLESIGNONID , INVALIDLOGINATTEMPTS , LASTLOGINTIME , LASTPASSWORDCHANGETIME , STATUS , CREATEDBY , CREATEDDATE , LASTUPDATEDBY , LASTUPDATEDDATE  from APPUSER where USERID = ? ";
		return getJdbcTemplate().queryForObject(selectQuery, new AppUserRowMapper(), aAppUser.getUserId());

	}

}
