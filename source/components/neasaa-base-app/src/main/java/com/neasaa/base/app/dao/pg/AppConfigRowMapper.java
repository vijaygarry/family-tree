/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.base.app.dao.pg;

import java.sql.SQLException;
import java.sql.ResultSet;
import org.springframework.jdbc.core.RowMapper;
import com.neasaa.base.app.entity.AppConfig;

public class AppConfigRowMapper implements RowMapper<AppConfig> {

	@Override
	public AppConfig mapRow(ResultSet aRs, int aRowNum) throws SQLException {
		AppConfig appConfig = new AppConfig();
		appConfig.setConfigName(aRs.getString("CONFIGNAME"));
		appConfig.setParamName(aRs.getString("PARAMNAME"));
		appConfig.setParamValue(aRs.getString("PARAMVALUE"));
		appConfig.setEnable(aRs.getBoolean("ENABLE"));
		appConfig.setListOrderSeq(aRs.getShort("LISTORDERSEQ"));
		appConfig.setCreatedBy(aRs.getInt("CREATEDBY"));
		appConfig.setCreatedDate(AbstractDao.getTimestampFromResultSet(aRs, "CREATEDDATE"));
		appConfig.setLastUpdatedBy(aRs.getInt("LASTUPDATEDBY"));
		appConfig.setLastUpdatedDate(AbstractDao.getTimestampFromResultSet(aRs, "LASTUPDATEDDATE"));
		return appConfig;
	}

}
