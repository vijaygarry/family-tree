/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.base.app.dao.pg;

import java.sql.SQLException;
import java.sql.ResultSet;
import org.springframework.jdbc.core.RowMapper;
import com.neasaa.base.app.entity.AppSession;
import com.neasaa.base.app.enums.ChannelEnum;

public class AppSessionRowMapper implements RowMapper<AppSession> {

	@Override
	public AppSession mapRow(ResultSet aRs, int aRowNum) throws SQLException {
		AppSession appSession = AppSession.builder()
				.sessionId(aRs.getLong("SESSIONID"))
				.userId(aRs.getInt("USERID"))
				.channel(ChannelEnum.valueOf(aRs.getString("CHANNELID")))
				.active(aRs.getBoolean("ACTIVE"))
				.authenticated(aRs.getBoolean("AUTHENTICATED"))
				.sessionCreationTime(AbstractDao.getTimestampFromResultSet(aRs, "SESSIONCREATIONTIME"))
				.logoutTime(AbstractDao.getTimestampFromResultSet(aRs, "LOGOUTTIME"))
				.lastAccessTime(AbstractDao.getTimestampFromResultSet(aRs, "LASTACCESSTIME"))
				.exitCode(aRs.getShort("EXITCODE"))
				.appHostName(aRs.getString("APPHOSTNAME"))
				.clientIpAddress(aRs.getString("CLIENTIPADDRESS"))
				.userAgent(aRs.getString("USER_AGENT"))
				.build();
		return appSession;
	}

}
