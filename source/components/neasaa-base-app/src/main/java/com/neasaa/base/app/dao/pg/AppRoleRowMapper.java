/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.base.app.dao.pg;

import java.sql.SQLException;
import java.sql.ResultSet;
import org.springframework.jdbc.core.RowMapper;
import com.neasaa.base.app.entity.AppRole;

public class AppRoleRowMapper implements RowMapper<AppRole> {

	@Override
	public AppRole mapRow(ResultSet aRs, int aRowNum) throws SQLException {
		AppRole appRole = new AppRole();
		appRole.setRoleId(aRs.getString("ROLEID"));
		appRole.setRoledesc(aRs.getString("ROLEDESC"));
		appRole.setEnable(aRs.getBoolean("ENABLE"));
		appRole.setCreatedBy(aRs.getInt("CREATEDBY"));
		appRole.setCreatedDate(AbstractDao.getTimestampFromResultSet(aRs, "CREATEDDATE"));
		appRole.setLastupdatedBy(aRs.getInt("LASTUPDATEDBY"));
		appRole.setLastupdatedDate(AbstractDao.getTimestampFromResultSet(aRs, "LASTUPDATEDDATE"));
		return appRole;
	}

}
