/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.familytree.dao.pg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.neasaa.base.app.dao.pg.AbstractDao;
import com.neasaa.familytree.entity.Family;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Repository
public class FamilyDao extends AbstractDao {
	
	private static final String SELECT_FAMILY_BY_FAMILYID = "select FAMILYID, FAMILYNAME, GOTRA, ADDRESSID, "
			+ "REGION, PHONE, ISPHONEWHATSAPPREGISTERED, EMAIL, FAMILYDISPLAYNAME, ACTIVE, FAMILYIMAGE, IMAGELASTUPDATED, "
			+ "CREATEDBY, CREATEDDATE, LASTUPDATEDBY, LASTUPDATEDDATE "
			+ "from " + BASE_SCHEMA_NAME + "FAMILY where FAMILYID = ? ";
	
	public Family getFamilyByFamilyId(int familyId) {
		List<Family> familyList = getJdbcTemplate().query(SELECT_FAMILY_BY_FAMILYID, new FamilyRowMapper(), familyId);
		
		if(familyList == null || familyList.size() == 0) {
			return null;
		}
		if(familyList.size() > 1) {
			throw new RuntimeException("Invalid family id entry");
		}
		return familyList.get(0);		
	}
	
	public int addFamily (Family aFamily) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
				return buildInsertStatement(aCon, aFamily);
			}
		}, keyHolder);
		int familyId = -1;
		Number key = keyHolder.getKey();
		if (key != null) {
			familyId = key.intValue();
		}
		log.info("New family added with id " + familyId);
		return familyId;
	}
	
	private PreparedStatement buildInsertStatement(Connection aConection, Family aFamily) throws SQLException {
		String sqlStatement = "INSERT INTO " + BASE_SCHEMA_NAME + "FAMILY (FAMILYNAME, GOTRA, ADDRESSID, REGION, PHONE, ISPHONEWHATSAPPREGISTERED, EMAIL, FAMILYDISPLAYNAME, ACTIVE, FAMILYIMAGE, IMAGELASTUPDATED, CREATEDBY, CREATEDDATE, LASTUPDATEDBY, LASTUPDATEDDATE) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		PreparedStatement prepareStatement = aConection.prepareStatement(sqlStatement, new String[] { "familyid" });
		setStringInStatement(prepareStatement, 1, aFamily.getFamilyName());
		setStringInStatement(prepareStatement, 2, aFamily.getGotra());
		setIntInStatement(prepareStatement, 3, aFamily.getAddressId());
		setStringInStatement(prepareStatement, 4, aFamily.getRegion());
		setStringInStatement(prepareStatement, 5, aFamily.getPhone());
		setBooleanInStatement(prepareStatement, 6, aFamily.isPhoneWhatsappRegistered());
		setStringInStatement(prepareStatement, 7, aFamily.getEmail());
		setStringInStatement(prepareStatement, 8, aFamily.getFamilyDisplayName());
		setBooleanInStatement(prepareStatement, 9, aFamily.isActive());
		setStringInStatement(prepareStatement, 10, aFamily.getFamilyImage());
		setTimestampInStatement(prepareStatement, 11, aFamily.getImageLastUpdated());
		setIntInStatement(prepareStatement, 12, aFamily.getCreatedBy());
		setTimestampInStatement(prepareStatement, 13, aFamily.getCreatedDate());
		setIntInStatement(prepareStatement, 14, aFamily.getLastUpdatedBy());
		setTimestampInStatement(prepareStatement, 15, aFamily.getLastUpdatedDate());
		return prepareStatement;
	}

	

	public int deleteFamily(Family aFamily) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aConection) throws SQLException {
				String deleteSqlQuery = "DELETE FROM FAMILY WHERE FAMILYID = ?";
				PreparedStatement prepareStatement = aConection.prepareStatement(deleteSqlQuery);
				setIntInStatement(prepareStatement, 1, aFamily.getFamilyId());
				return prepareStatement;
			}
		});

	}

	public PreparedStatement buildUpdateStatement(Connection aConection, Family aFamily) throws SQLException {
		String updateStatement = "UPDATE FAMILY SET FAMILYNAME = ? , GOTRA = ? , ADDRESSID = ? , REGION = ? , PHONE = ? , ISPHONEWHATSAPPREGISTERED = ? , EMAIL = ? , FAMILYDISPLAYNAME = ? , ACTIVE = ? , FAMILYIMAGE = ? , IMAGELASTUPDATED = ? , CREATEDBY = ? , CREATEDDATE = ? , LASTUPDATEDBY = ? , LASTUPDATEDDATE = ?  where FAMILYID = ?";

		PreparedStatement prepareStatement = aConection.prepareStatement(updateStatement);
		setStringInStatement(prepareStatement, 1, aFamily.getFamilyName());
		setStringInStatement(prepareStatement, 2, aFamily.getGotra());
		setIntInStatement(prepareStatement, 3, aFamily.getAddressId());
		setStringInStatement(prepareStatement, 4, aFamily.getRegion());
		setStringInStatement(prepareStatement, 5, aFamily.getPhone());
		setBooleanInStatement(prepareStatement, 6, aFamily.isPhoneWhatsappRegistered());
		setStringInStatement(prepareStatement, 7, aFamily.getEmail());
		setStringInStatement(prepareStatement, 8, aFamily.getFamilyDisplayName());
		setBooleanInStatement(prepareStatement, 9, aFamily.isActive());
		setStringInStatement(prepareStatement, 10, aFamily.getFamilyImage());
		setTimestampInStatement(prepareStatement, 11, aFamily.getImageLastUpdated());
		setIntInStatement(prepareStatement, 12, aFamily.getCreatedBy());
		setTimestampInStatement(prepareStatement, 13, aFamily.getCreatedDate());
		setIntInStatement(prepareStatement, 14, aFamily.getLastUpdatedBy());
		setTimestampInStatement(prepareStatement, 15, aFamily.getLastUpdatedDate());
		setIntInStatement(prepareStatement, 16, aFamily.getFamilyId());
		return prepareStatement;
	}

	public int updateFamily(Family aFamily) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
				return buildUpdateStatement(aCon, aFamily);
			}
		});

	}

}
