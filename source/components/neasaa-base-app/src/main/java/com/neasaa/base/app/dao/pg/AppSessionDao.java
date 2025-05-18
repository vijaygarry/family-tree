/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.base.app.dao.pg;

import java.sql.SQLException;
import java.util.List;
import java.sql.Connection;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import com.neasaa.base.app.entity.AppSession;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Repository
public class AppSessionDao extends AbstractDao {

	private static final String GET_SESSION_BYID = "SELECT  SESSIONID , USERID , CHANNELID , ACTIVE , "
			+ "AUTHENTICATED , SESSIONCREATIONTIME , LOGOUTTIME , LASTACCESSTIME , EXITCODE , APPHOSTNAME , "
			+ "CLIENTIPADDRESS , USER_AGENT  from " + BASE_SCHEMA_NAME + "TXTSESSION where SESSIONID = ? ";
	
	public boolean isSessionValidAndAuthenticated(long sessionId) {
				AppSession sessionEntity = getSessionById(sessionId);
		if (sessionEntity == null) {
			return false;
		}
		return sessionEntity.isAuthenticated();
	}
	
	public AppSession addAppSession(AppSession aAppSession) throws SQLException {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
				return buildInsertStatement(aCon, aAppSession);
			}
		}, keyHolder);
		Long sessionId = -1l;
		Number key = keyHolder.getKey();
		if (key != null) {
			sessionId = key.longValue();
		}
		log.info("New session created with session id " + sessionId);
		return getSessionById(sessionId);
	}
	
	private PreparedStatement buildInsertStatement(Connection aConection, AppSession aAppSession) throws SQLException {
		String sqlStatement = "INSERT INTO " + BASE_SCHEMA_NAME + "TXTSESSION (USERID, CHANNELID, ACTIVE, AUTHENTICATED, SESSIONCREATIONTIME, LOGOUTTIME, "
				+ "LASTACCESSTIME, EXITCODE, APPHOSTNAME, CLIENTIPADDRESS, USER_AGENT) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		PreparedStatement prepareStatement = aConection.prepareStatement(sqlStatement, new String[] { "sessionid" });
		setIntInStatement(prepareStatement, 1, aAppSession.getUserId());
		setStringInStatement(prepareStatement, 2, aAppSession.getChannel().name());
		setBooleanInStatement(prepareStatement, 3, aAppSession.isActive());
		setBooleanInStatement(prepareStatement, 4, aAppSession.isAuthenticated());
		setTimestampInStatement(prepareStatement, 5, aAppSession.getSessionCreationTime());
		setTimestampInStatement(prepareStatement, 6, aAppSession.getLogoutTime());
		setTimestampInStatement(prepareStatement, 7, aAppSession.getLastAccessTime());
		setSmallIntInStatement(prepareStatement, 8, aAppSession.getExitCode());
		setStringInStatement(prepareStatement, 9, aAppSession.getAppHostName());
		setStringInStatement(prepareStatement, 10, aAppSession.getClientIpAddress());
		setStringInStatement(prepareStatement, 11, aAppSession.getUserAgent());
		return prepareStatement;
	}

	
	public AppSession getSessionById(Long aSessionId) {
		List<AppSession> appSessions =  getJdbcTemplate().query(GET_SESSION_BYID, new AppSessionRowMapper(), aSessionId);
		if(appSessions == null || appSessions.size() == 0) {
			return null;
		}
		if(appSessions.size() > 1) {
			throw new RuntimeException("Invalid session entry");
		}
		return appSessions.get(0);
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
		String updateStatement = "UPDATE TXTSESSION SET USERID = ? , CHANNELID = ? , ACTIVE = ? , AUTHENTICATED = ? , SESSIONCREATIONTIME = ? , LOGOUTTIME = ? , LASTACCESSTIME = ? , EXITCODE = ? , APPHOSTNAME = ? , CLIENTIPADDRESS = ? , USER_AGENT = ?  where SESSIONID = ?";

		PreparedStatement prepareStatement = aConection.prepareStatement(updateStatement);
		setIntInStatement(prepareStatement, 1, aAppSession.getUserId());
		setStringInStatement(prepareStatement, 2, aAppSession.getChannel().name());
		setBooleanInStatement(prepareStatement, 3, aAppSession.isActive());
		setBooleanInStatement(prepareStatement, 4, aAppSession.isAuthenticated());
		setTimestampInStatement(prepareStatement, 5, aAppSession.getSessionCreationTime());
		setTimestampInStatement(prepareStatement, 6, aAppSession.getLogoutTime());
		setTimestampInStatement(prepareStatement, 7, aAppSession.getLastAccessTime());
		setSmallIntInStatement(prepareStatement, 8, aAppSession.getExitCode());
		setStringInStatement(prepareStatement, 9, aAppSession.getAppHostName());
		setStringInStatement(prepareStatement, 10, aAppSession.getClientIpAddress());
		setStringInStatement(prepareStatement, 11, aAppSession.getUserAgent());
		setLongInStatement(prepareStatement, 12, aAppSession.getSessionId());
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

}
