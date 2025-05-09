/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.base.app.dao.pg;

import java.sql.SQLException;
import com.neasaa.base.app.entity.RoleOperationMap;
import java.sql.ResultSet;
import org.springframework.jdbc.core.RowMapper;

public class RoleOperationMapRowMapper implements RowMapper<RoleOperationMap> {

	@Override
	public RoleOperationMap mapRow(ResultSet aRs, int aRowNum) throws SQLException {
		RoleOperationMap roleOperationMap = new RoleOperationMap();
		roleOperationMap.setRoleId(aRs.getString("ROLEID"));
		roleOperationMap.setOperationId(aRs.getString("OPERATIONID"));
		roleOperationMap.setCreatedBy(aRs.getInt("CREATEDBY"));
		roleOperationMap.setCreatedDate(AbstractDao.getTimestampFromResultSet(aRs, "CREATEDDATE"));
		roleOperationMap.setLastupdatedBy(aRs.getInt("LASTUPDATEDBY"));
		roleOperationMap.setLastupdatedDate(AbstractDao.getTimestampFromResultSet(aRs, "LASTUPDATEDDATE"));
		return roleOperationMap;
	}

}
