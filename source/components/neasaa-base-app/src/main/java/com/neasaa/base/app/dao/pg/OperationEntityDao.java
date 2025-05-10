/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.base.app.dao.pg;

import com.neasaa.base.app.entity.OperationEntity;
import java.sql.SQLException;
import java.sql.Connection;
import org.springframework.jdbc.core.PreparedStatementCreator;
import java.sql.PreparedStatement;

public class OperationEntityDao extends AbstractDao {

	private PreparedStatement buildInsertStatement(Connection aConection, OperationEntity aOperationEntity) throws SQLException {
		String sqlStatement = "INSERT INTO LKPOPERATION (OPERATIONID, DESCRIPTION, BEANNAME, ISAUTHORIZATIONREQUIRED, ISAUDITREQUIRED, AUTHORIZATIONTYPE, ACTIVE, CREATEDBY, CREATEDDATE, LASTUPDATEDBY, LASTUPDATEDDATE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		PreparedStatement prepareStatement = aConection.prepareStatement(sqlStatement);
		setStringInStatement(prepareStatement, 1, aOperationEntity.getOperationId());
		setStringInStatement(prepareStatement, 2, aOperationEntity.getDescription());
		setStringInStatement(prepareStatement, 3, aOperationEntity.getBeanName());
		setBooleanInStatement(prepareStatement, 4, aOperationEntity.isAuthorizationRequired());
		setBooleanInStatement(prepareStatement, 5, aOperationEntity.isAuditRequired());
		setStringInStatement(prepareStatement, 6, aOperationEntity.getAuthorizationType());
		setBooleanInStatement(prepareStatement, 7, aOperationEntity.isActive());
		setIntInStatement(prepareStatement, 8, aOperationEntity.getCreatedBy());
		setTimestampInStatement(prepareStatement, 9, aOperationEntity.getCreatedDate());
		setIntInStatement(prepareStatement, 10, aOperationEntity.getLastupdatedBy());
		setTimestampInStatement(prepareStatement, 11, aOperationEntity.getLastupdatedDate());
		return prepareStatement;
	}

	public int insertOperationEntity(OperationEntity aOperationEntity) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
				return buildInsertStatement(aCon, aOperationEntity);
			}
		});

	}

	public int deleteOperationEntity(OperationEntity aOperationEntity) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aConection) throws SQLException {
				String deleteSqlQuery = "DELETE FROM LKPOPERATION WHERE OPERATIONID = ?";
				PreparedStatement prepareStatement = aConection.prepareStatement(deleteSqlQuery);
				setStringInStatement(prepareStatement, 1, aOperationEntity.getOperationId());
				return prepareStatement;
			}
		});

	}

	public PreparedStatement buildUpdateStatement(Connection aConection, OperationEntity aOperationEntity) throws SQLException {
		String updateStatement = "UPDATE LKPOPERATION SET DESCRIPTION = ? , BEANNAME = ? , ISAUTHORIZATIONREQUIRED = ? , ISAUDITREQUIRED = ? , AUTHORIZATIONTYPE = ? , ACTIVE = ? , CREATEDBY = ? , CREATEDDATE = ? , LASTUPDATEDBY = ? , LASTUPDATEDDATE = ?  where OPERATIONID = ?";

		PreparedStatement prepareStatement = aConection.prepareStatement(updateStatement);
		setStringInStatement(prepareStatement, 1, aOperationEntity.getDescription());
		setStringInStatement(prepareStatement, 2, aOperationEntity.getBeanName());
		setBooleanInStatement(prepareStatement, 3, aOperationEntity.isAuthorizationRequired());
		setBooleanInStatement(prepareStatement, 4, aOperationEntity.isAuditRequired());
		setStringInStatement(prepareStatement, 5, aOperationEntity.getAuthorizationType());
		setBooleanInStatement(prepareStatement, 6, aOperationEntity.isActive());
		setIntInStatement(prepareStatement, 7, aOperationEntity.getCreatedBy());
		setTimestampInStatement(prepareStatement, 8, aOperationEntity.getCreatedDate());
		setIntInStatement(prepareStatement, 9, aOperationEntity.getLastupdatedBy());
		setTimestampInStatement(prepareStatement, 10, aOperationEntity.getLastupdatedDate());
		setStringInStatement(prepareStatement, 11, aOperationEntity.getOperationId());
		return prepareStatement;
	}

	public int updateOperationEntity(OperationEntity aOperationEntity) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
				return buildUpdateStatement(aCon, aOperationEntity);
			}
		});

	}

	public OperationEntity fetchOperationEntity(OperationEntity aOperationEntity) throws SQLException {
		String selectQuery = "select  OPERATIONID , DESCRIPTION , BEANNAME , ISAUTHORIZATIONREQUIRED , ISAUDITREQUIRED , AUTHORIZATIONTYPE , ACTIVE , CREATEDBY , CREATEDDATE , LASTUPDATEDBY , LASTUPDATEDDATE  from LKPOPERATION where OPERATIONID = ? ";
		return getJdbcTemplate().queryForObject(selectQuery, new OperationEntityRowMapper(), aOperationEntity.getOperationId());

	}

}
