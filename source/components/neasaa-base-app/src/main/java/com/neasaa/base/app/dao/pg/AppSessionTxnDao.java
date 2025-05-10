/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.base.app.dao.pg;

import java.sql.SQLException;
import java.sql.Connection;
import com.neasaa.base.app.entity.AppSessionTxn;
import org.springframework.jdbc.core.PreparedStatementCreator;
import java.sql.PreparedStatement;

public class AppSessionTxnDao extends AbstractDao {

	private PreparedStatement buildInsertStatement(Connection aConection, AppSessionTxn aAppSessionTxn) throws SQLException {
		String sqlStatement = "INSERT INTO TXTSESSIONTXN (SESSIONID, OPERATIONID, USERID, TXNSTARTTIME, TXNLATENCYMILLIS, HTTPRESPONSECODE, REQUEST, RESPONSE) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

		PreparedStatement prepareStatement = aConection.prepareStatement(sqlStatement);
		setLongInStatement(prepareStatement, 1, aAppSessionTxn.getSessionId());
		setStringInStatement(prepareStatement, 2, aAppSessionTxn.getOperationId());
		setIntInStatement(prepareStatement, 3, aAppSessionTxn.getUserId());
		setTimestampInStatement(prepareStatement, 4, aAppSessionTxn.getTxnStartTime());
		setLongInStatement(prepareStatement, 5, aAppSessionTxn.getTxnLatencyMillis());
		setIntInStatement(prepareStatement, 6, aAppSessionTxn.getHttpResponseCode());
		setStringInStatement(prepareStatement, 7, aAppSessionTxn.getRequest());
		setStringInStatement(prepareStatement, 8, aAppSessionTxn.getResponse());
		return prepareStatement;
	}

	public int insertAppSessionTxn(AppSessionTxn aAppSessionTxn) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
				return buildInsertStatement(aCon, aAppSessionTxn);
			}
		});

	}

	public int deleteAppSessionTxn(AppSessionTxn aAppSessionTxn) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aConection) throws SQLException {
				String deleteSqlQuery = "DELETE FROM TXTSESSIONTXN WHERE TXNID = ?";
				PreparedStatement prepareStatement = aConection.prepareStatement(deleteSqlQuery);
				setLongInStatement(prepareStatement, 1, aAppSessionTxn.getTxnId());
				return prepareStatement;
			}
		});

	}

	public PreparedStatement buildUpdateStatement(Connection aConection, AppSessionTxn aAppSessionTxn) throws SQLException {
		String updateStatement = "UPDATE TXTSESSIONTXN SET SESSIONID = ? , OPERATIONID = ? , USERID = ? , TXNSTARTTIME = ? , TXNLATENCYMILLIS = ? , HTTPRESPONSECODE = ? , REQUEST = ? , RESPONSE = ?  where TXNID = ?";

		PreparedStatement prepareStatement = aConection.prepareStatement(updateStatement);
		setLongInStatement(prepareStatement, 1, aAppSessionTxn.getSessionId());
		setStringInStatement(prepareStatement, 2, aAppSessionTxn.getOperationId());
		setIntInStatement(prepareStatement, 3, aAppSessionTxn.getUserId());
		setTimestampInStatement(prepareStatement, 4, aAppSessionTxn.getTxnStartTime());
		setLongInStatement(prepareStatement, 5, aAppSessionTxn.getTxnLatencyMillis());
		setIntInStatement(prepareStatement, 6, aAppSessionTxn.getHttpResponseCode());
		setStringInStatement(prepareStatement, 7, aAppSessionTxn.getRequest());
		setStringInStatement(prepareStatement, 8, aAppSessionTxn.getResponse());
		setLongInStatement(prepareStatement, 9, aAppSessionTxn.getTxnId());
		return prepareStatement;
	}

	public int updateAppSessionTxn(AppSessionTxn aAppSessionTxn) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
				return buildUpdateStatement(aCon, aAppSessionTxn);
			}
		});

	}

	public AppSessionTxn fetchAppSessionTxn(AppSessionTxn aAppSessionTxn) throws SQLException {
		String selectQuery = "select  TXNID , SESSIONID , OPERATIONID , USERID , TXNSTARTTIME , TXNLATENCYMILLIS , HTTPRESPONSECODE , REQUEST , RESPONSE  from TXTSESSIONTXN where TXNID = ? ";
		return getJdbcTemplate().queryForObject(selectQuery, new AppSessionTxnRowMapper(), aAppSessionTxn.getTxnId());

	}

}
