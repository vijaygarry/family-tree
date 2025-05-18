/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.base.app.dao.pg;

import com.neasaa.base.app.entity.OperationEntity;
import com.neasaa.base.app.enums.AuthorizationType;

import java.sql.SQLException;
import java.sql.ResultSet;
import org.springframework.jdbc.core.RowMapper;

public class OperationEntityRowMapper implements RowMapper<OperationEntity> {

	@Override
	public OperationEntity mapRow(ResultSet aRs, int aRowNum) throws SQLException {
		OperationEntity operationEntity = new OperationEntity();
		operationEntity.setOperationId(aRs.getString("OPERATIONID"));
		operationEntity.setDescription(aRs.getString("DESCRIPTION"));
		operationEntity.setBeanName(aRs.getString("BEANNAME"));
		operationEntity.setAuthorizationRequired(aRs.getBoolean("ISAUTHORIZATIONREQUIRED"));
		operationEntity.setAuditRequired(aRs.getBoolean("ISAUDITREQUIRED"));
		operationEntity.setAuthorizationType(AuthorizationType.valueOf(aRs.getString("AUTHORIZATIONTYPE")));
		operationEntity.setActive(aRs.getBoolean("ACTIVE"));
		operationEntity.setCreatedBy(aRs.getInt("CREATEDBY"));
		operationEntity.setCreatedDate(AbstractDao.getTimestampFromResultSet(aRs, "CREATEDDATE"));
		operationEntity.setLastupdatedBy(aRs.getInt("LASTUPDATEDBY"));
		operationEntity.setLastupdatedDate(AbstractDao.getTimestampFromResultSet(aRs, "LASTUPDATEDDATE"));
		return operationEntity;
	}

}
