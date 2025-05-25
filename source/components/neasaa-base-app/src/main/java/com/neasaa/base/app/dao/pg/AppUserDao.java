/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.base.app.dao.pg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;

import com.neasaa.base.app.entity.AppUser;

@Repository
public class AppUserDao extends AbstractDao {
	
	private static final String GET_USER_BY_LOGON_NAME = "SELECT "
			+ " USERID , LOGONNAME , HASHPASSWORD , FIRSTNAME , LASTNAME , EMAILID , AUTHENTICATIONTYPE , "
			+ "SINGLESIGNONID , INVALIDLOGINATTEMPTS , LASTLOGINTIME , LASTPASSWORDCHANGETIME , STATUS , "
			+ "CREATEDBY , CREATEDDATE , LASTUPDATEDBY , LASTUPDATEDDATE"
			+ " FROM " + BASE_SCHEMA_NAME + "APPUSER"
			+ " WHERE LOGONNAME = ? ";
	
	private static final String UPDATE_INVALID_LOGIN_ATTEMPTS_STATEMENT = "UPDATE " + BASE_SCHEMA_NAME + "APPUSER "
			+ "SET INVALIDLOGINATTEMPTS = ?, "
			+ "STATUS = ?, "
			+ "LASTUPDATEDDATE = NOW() "
			+ "WHERE LOGONNAME = ?";
	
	private static final String UPDATE_LAST_SUCCESS_LOGIN_TIME_STATEMENT = "UPDATE " + BASE_SCHEMA_NAME + "APPUSER "
			+ "SET INVALIDLOGINATTEMPTS = 0, "
			+ "LASTLOGINTIME = NOW(), "
			+ "LASTUPDATEDDATE = NOW(), "
			+ "LASTUPDATEDBY = ? "
			+ "WHERE LOGONNAME = ?";
	
	private static final String UPDATE_USER_PASSWORD_STATEMENT = "UPDATE " + BASE_SCHEMA_NAME + "APPUSER "
			+ "SET hashpassword = ?, "
			+ "lastpasswordchangetime = NOW(), "
			+ "lastupdateddate = NOW(), "
			+ "lastupdatedby = (select userid from " + BASE_SCHEMA_NAME + "APPUSER where LOGONNAME = ?) "
			+ "WHERE LOGONNAME = ?";
	  
	
	public AppUser getUserByLogonName(String logonName) throws SQLException {
		List<AppUser> userList= getJdbcTemplate().query(GET_USER_BY_LOGON_NAME, new AppUserRowMapper(), logonName);
		if(userList == null || userList.size() == 0) {
			return null;
		}
		if(userList.size() > 1) {
			throw new RuntimeException("Invalid user entry");
		}
		return userList.get(0);
	}
	
	/** Update invalid login attempt for logon name.
	 * This method does not check status of logon name.
	 * @param logonName
	 * @param numberOfInvalidAttempts
	 * @return
	 * @throws SQLException
	 */
	public int updateInvalidLoginAttempt(String logonName, int numberOfInvalidAttempts, String status) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
				PreparedStatement prepareStatement = aCon.prepareStatement(UPDATE_INVALID_LOGIN_ATTEMPTS_STATEMENT);
				setIntInStatement(prepareStatement, 1, numberOfInvalidAttempts);
				setStringInStatement(prepareStatement, 2, status);
				setStringInStatement(prepareStatement, 3, logonName);
				return prepareStatement;
			}
		});
	}
	
	public int updateLastSuccessLoginTime(String logonName, int userId) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
				PreparedStatement prepareStatement = aCon.prepareStatement(UPDATE_LAST_SUCCESS_LOGIN_TIME_STATEMENT);
				setIntInStatement(prepareStatement, 1, userId);
				setStringInStatement(prepareStatement, 2, logonName);
				return prepareStatement;
			}
		});
	}
	
	public int updateUserPassword(String logonName, String hashPassword) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
				PreparedStatement prepareStatement = aCon.prepareStatement(UPDATE_USER_PASSWORD_STATEMENT);
				setStringInStatement(prepareStatement, 1, hashPassword);
				setStringInStatement(prepareStatement, 2, logonName);
				setStringInStatement(prepareStatement, 3, logonName);
				return prepareStatement;
			}
		});
	}
	
	private PreparedStatement buildInsertStatement(Connection aConection, AppUser aAppUser) throws SQLException {
		String sqlStatement = "INSERT INTO APPUSER (LOGONNAME, HASHPASSWORD, FIRSTNAME, LASTNAME, "
				+ "EMAILID, AUTHENTICATIONTYPE, SINGLESIGNONID, INVALIDLOGINATTEMPTS, LASTLOGINTIME, "
				+ "LASTPASSWORDCHANGETIME, STATUS, CREATEDBY, CREATEDDATE, LASTUPDATEDBY, LASTUPDATEDDATE) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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

	private PreparedStatement buildUpdateStatement(Connection aConection, AppUser aAppUser) throws SQLException {
		String updateStatement = "UPDATE APPUSER "
				+ "SET LOGONNAME = ? , HASHPASSWORD = ? , FIRSTNAME = ? , LASTNAME = ? , EMAILID = ? , "
				+ "AUTHENTICATIONTYPE = ? , SINGLESIGNONID = ? , INVALIDLOGINATTEMPTS = ? , LASTLOGINTIME = ? , "
				+ "LASTPASSWORDCHANGETIME = ? , STATUS = ? , CREATEDBY = ? , CREATEDDATE = ? , "
				+ "LASTUPDATEDBY = ? , LASTUPDATEDDATE = ?  where USERID = ?";

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

}
