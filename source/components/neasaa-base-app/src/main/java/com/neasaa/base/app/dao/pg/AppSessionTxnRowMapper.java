/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.base.app.dao.pg;

import java.sql.SQLException;
import java.sql.ResultSet;
import org.springframework.jdbc.core.RowMapper;
import com.neasaa.base.app.entity.AppSessionTxn;

public class AppSessionTxnRowMapper implements RowMapper<AppSessionTxn> {

	@Override
	public AppSessionTxn mapRow(ResultSet aRs, int aRowNum) throws SQLException {
		AppSessionTxn appSessionTxn = new AppSessionTxn();
		appSessionTxn.setTxnId(aRs.getLong("TXNID"));
		appSessionTxn.setSessionId(aRs.getLong("SESSIONID"));
		appSessionTxn.setOperationId(aRs.getString("OPERATIONID"));
		appSessionTxn.setUserId(aRs.getInt("USERID"));
		appSessionTxn.setTxnStartTime(AbstractDao.getTimestampFromResultSet(aRs, "TXNSTARTTIME"));
		appSessionTxn.setTxnLatencyMillis(aRs.getLong("TXNLATENCYMILLIS"));
		appSessionTxn.setHttpResponseCode(aRs.getInt("HTTPRESPONSECODE"));
		appSessionTxn.setRequest(aRs.getString("REQUEST"));
		appSessionTxn.setResponse(aRs.getString("RESPONSE"));
		return appSessionTxn;
	}

}
