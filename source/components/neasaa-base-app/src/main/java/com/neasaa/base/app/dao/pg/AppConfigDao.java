/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.base.app.dao.pg;

import java.sql.SQLException;
import java.sql.Connection;
import com.neasaa.base.app.entity.AppConfig;
import org.springframework.jdbc.core.PreparedStatementCreator;
import java.sql.PreparedStatement;

public class AppConfigDao extends AbstractDao {

	private PreparedStatement buildInsertStatement(Connection aConection, AppConfig aAppConfig) throws SQLException {
		String sqlStatement = "INSERT INTO LKPCONFIG (CONFIGNAME, PARAMNAME, PARAMVALUE, ENABLE, LISTORDERSEQ, CREATEDBY, CREATEDDATE, LASTUPDATEDBY, LASTUPDATEDDATE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

		PreparedStatement prepareStatement = aConection.prepareStatement(sqlStatement);
		setStringInStatement(prepareStatement, 1, aAppConfig.getConfigName());
		setStringInStatement(prepareStatement, 2, aAppConfig.getParamName());
		setStringInStatement(prepareStatement, 3, aAppConfig.getParamValue());
		setBooleanInStatement(prepareStatement, 4, aAppConfig.isEnable());
		setSmallIntInStatement(prepareStatement, 5, aAppConfig.getListOrderSeq());
		setIntInStatement(prepareStatement, 6, aAppConfig.getCreatedBy());
		setTimestampInStatement(prepareStatement, 7, aAppConfig.getCreatedDate());
		setIntInStatement(prepareStatement, 8, aAppConfig.getLastUpdatedBy());
		setTimestampInStatement(prepareStatement, 9, aAppConfig.getLastUpdatedDate());
		return prepareStatement;
	}

	public int insertAppConfig(AppConfig aAppConfig) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
				return buildInsertStatement(aCon, aAppConfig);
			}
		});

	}

	public int deleteAppConfig(AppConfig aAppConfig) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aConection) throws SQLException {
				String deleteSqlQuery = "DELETE FROM LKPCONFIG WHERE CONFIGNAME = ? and PARAMNAME = ?";
				PreparedStatement prepareStatement = aConection.prepareStatement(deleteSqlQuery);
				setStringInStatement(prepareStatement, 1, aAppConfig.getConfigName());
				setStringInStatement(prepareStatement, 2, aAppConfig.getParamName());
				return prepareStatement;
			}
		});

	}

	public PreparedStatement buildUpdateStatement(Connection aConection, AppConfig aAppConfig) throws SQLException {
		String updateStatement = "UPDATE LKPCONFIG SET PARAMVALUE = ? , ENABLE = ? , LISTORDERSEQ = ? , CREATEDBY = ? , CREATEDDATE = ? , LASTUPDATEDBY = ? , LASTUPDATEDDATE = ?  where CONFIGNAME = ? and PARAMNAME = ?";

		PreparedStatement prepareStatement = aConection.prepareStatement(updateStatement);
		setStringInStatement(prepareStatement, 1, aAppConfig.getParamValue());
		setBooleanInStatement(prepareStatement, 2, aAppConfig.isEnable());
		setSmallIntInStatement(prepareStatement, 3, aAppConfig.getListOrderSeq());
		setIntInStatement(prepareStatement, 4, aAppConfig.getCreatedBy());
		setTimestampInStatement(prepareStatement, 5, aAppConfig.getCreatedDate());
		setIntInStatement(prepareStatement, 6, aAppConfig.getLastUpdatedBy());
		setTimestampInStatement(prepareStatement, 7, aAppConfig.getLastUpdatedDate());
		setStringInStatement(prepareStatement, 8, aAppConfig.getConfigName());
		setStringInStatement(prepareStatement, 9, aAppConfig.getParamName());
		return prepareStatement;
	}

	public int updateAppConfig(AppConfig aAppConfig) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
				return buildUpdateStatement(aCon, aAppConfig);
			}
		});

	}

	public AppConfig fetchAppConfig(AppConfig aAppConfig) throws SQLException {
		String selectQuery = "select  CONFIGNAME , PARAMNAME , PARAMVALUE , ENABLE , LISTORDERSEQ , CREATEDBY , CREATEDDATE , LASTUPDATEDBY , LASTUPDATEDDATE  from LKPCONFIG where CONFIGNAME = ?  and PARAMNAME = ? ";
		return getJdbcTemplate().queryForObject(selectQuery, new AppConfigRowMapper(), aAppConfig.getConfigName(), aAppConfig.getParamName());

	}

}
