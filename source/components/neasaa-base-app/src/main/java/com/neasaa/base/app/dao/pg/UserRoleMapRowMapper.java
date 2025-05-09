/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.base.app.dao.pg;

import java.sql.SQLException;
import java.sql.ResultSet;
import org.springframework.jdbc.core.RowMapper;
import com.neasaa.base.app.entity.UserRoleMap;

public class UserRoleMapRowMapper implements RowMapper<UserRoleMap> {

	@Override
	public UserRoleMap mapRow(ResultSet aRs, int aRowNum) throws SQLException {
		UserRoleMap userRoleMap = new UserRoleMap();
		userRoleMap.setUserId(aRs.getInt("USERID"));
		userRoleMap.setRoleId(aRs.getString("ROLEID"));
		userRoleMap.setCreatedBy(aRs.getInt("CREATEDBY"));
		userRoleMap.setCreatedDate(AbstractDao.getTimestampFromResultSet(aRs, "CREATEDDATE"));
		userRoleMap.setLastupdatedBy(aRs.getInt("LASTUPDATEDBY"));
		userRoleMap.setLastupdatedDate(AbstractDao.getTimestampFromResultSet(aRs, "LASTUPDATEDDATE"));
		return userRoleMap;
	}

}
