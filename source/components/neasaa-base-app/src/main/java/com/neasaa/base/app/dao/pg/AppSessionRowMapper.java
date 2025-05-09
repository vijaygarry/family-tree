/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.base.app.dao.pg;

import java.sql.SQLException;
import java.sql.ResultSet;
import org.springframework.jdbc.core.RowMapper;
import com.neasaa.base.app.entity.AppSession;

public class AppSessionRowMapper implements RowMapper<AppSession> {

	@Override
	public AppSession mapRow(ResultSet aRs, int aRowNum) throws SQLException {
		AppSession appSession = new AppSession();
		appSession.setSessionId(aRs.getLong("SESSIONID"));
		appSession.setUserId(aRs.getInt("USERID"));
		appSession.setChannelId(aRs.getString("CHANNELID"));
		appSession.setActive(aRs.getString("ISACTIVE"));
		appSession.setSessionCreationTime(AbstractDao.getTimestampFromResultSet(aRs, "SESSIONCREATIONTIME"));
		appSession.setLogoutTime(AbstractDao.getTimestampFromResultSet(aRs, "LOGOUTTIME"));
		appSession.setLastAccessTime(AbstractDao.getTimestampFromResultSet(aRs, "LASTACCESSTIME"));
		appSession.setExitCode(aRs.getShort("EXITCODE"));
		appSession.setAppHostName(aRs.getString("APPHOSTNAME"));
		appSession.setClientIpAddress(aRs.getString("CLIENTIPADDRESS"));
		appSession.setUserAgent(aRs.getString("USER_AGENT"));
		return appSession;
	}

}
