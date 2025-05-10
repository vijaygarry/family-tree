/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.base.app.dao.pg;

import java.sql.SQLException;
import java.sql.Connection;
import org.springframework.jdbc.core.PreparedStatementCreator;
import java.sql.PreparedStatement;
import com.neasaa.base.app.entity.AppSession;

public class AppSessionDao extends AbstractDao {

	private PreparedStatement buildInsertStatement(Connection aConection, AppSession aAppSession) throws SQLException {
		String sqlStatement = "INSERT INTO TXTSESSION (USERID, CHANNELID, ISACTIVE, SESSIONCREATIONTIME, LOGOUTTIME, LASTACCESSTIME, EXITCODE, APPHOSTNAME, CLIENTIPADDRESS, USER_AGENT) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		PreparedStatement prepareStatement = aConection.prepareStatement(sqlStatement);
		setIntInStatement(prepareStatement, 1, aAppSession.getUserId());
		setStringInStatement(prepareStatement, 2, aAppSession.getChannelId());
		setStringInStatement(prepareStatement, 3, aAppSession.getActive());
		setTimestampInStatement(prepareStatement, 4, aAppSession.getSessionCreationTime());
		setTimestampInStatement(prepareStatement, 5, aAppSession.getLogoutTime());
		setTimestampInStatement(prepareStatement, 6, aAppSession.getLastAccessTime());
		setSmallIntInStatement(prepareStatement, 7, aAppSession.getExitCode());
		setStringInStatement(prepareStatement, 8, aAppSession.getAppHostName());
		setStringInStatement(prepareStatement, 9, aAppSession.getClientIpAddress());
		setStringInStatement(prepareStatement, 10, aAppSession.getUserAgent());
		return prepareStatement;
	}

	public int insertAppSession(AppSession aAppSession) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
				return buildInsertStatement(aCon, aAppSession);
			}
		});

	}

	public int deleteAppSession(AppSession aAppSession) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aConection) throws SQLException {
				String deleteSqlQuery = "DELETE FROM TXTSESSION WHERE SESSIONID = ?";
				PreparedStatement prepareStatement = aConection.prepareStatement(deleteSqlQuery);
				setLongInStatement(prepareStatement, 1, aAppSession.getSessionId());
				return prepareStatement;
			}
		});

	}

	public PreparedStatement buildUpdateStatement(Connection aConection, AppSession aAppSession) throws SQLException {
		String updateStatement = "UPDATE TXTSESSION SET USERID = ? , CHANNELID = ? , ISACTIVE = ? , SESSIONCREATIONTIME = ? , LOGOUTTIME = ? , LASTACCESSTIME = ? , EXITCODE = ? , APPHOSTNAME = ? , CLIENTIPADDRESS = ? , USER_AGENT = ?  where SESSIONID = ?";

		PreparedStatement prepareStatement = aConection.prepareStatement(updateStatement);
		setIntInStatement(prepareStatement, 1, aAppSession.getUserId());
		setStringInStatement(prepareStatement, 2, aAppSession.getChannelId());
		setStringInStatement(prepareStatement, 3, aAppSession.getActive());
		setTimestampInStatement(prepareStatement, 4, aAppSession.getSessionCreationTime());
		setTimestampInStatement(prepareStatement, 5, aAppSession.getLogoutTime());
		setTimestampInStatement(prepareStatement, 6, aAppSession.getLastAccessTime());
		setSmallIntInStatement(prepareStatement, 7, aAppSession.getExitCode());
		setStringInStatement(prepareStatement, 8, aAppSession.getAppHostName());
		setStringInStatement(prepareStatement, 9, aAppSession.getClientIpAddress());
		setStringInStatement(prepareStatement, 10, aAppSession.getUserAgent());
		setLongInStatement(prepareStatement, 11, aAppSession.getSessionId());
		return prepareStatement;
	}

	public int updateAppSession(AppSession aAppSession) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
				return buildUpdateStatement(aCon, aAppSession);
			}
		});

	}

	public AppSession fetchAppSession(AppSession aAppSession) throws SQLException {
		String selectQuery = "select  SESSIONID , USERID , CHANNELID , ISACTIVE , SESSIONCREATIONTIME , LOGOUTTIME , LASTACCESSTIME , EXITCODE , APPHOSTNAME , CLIENTIPADDRESS , USER_AGENT  from TXTSESSION where SESSIONID = ? ";
		return getJdbcTemplate().queryForObject(selectQuery, new AppSessionRowMapper(), aAppSession.getSessionId());

	}

}
