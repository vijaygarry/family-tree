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
	
	private static final String SELECT_FAMILY_BY_FAMILYID = "select FAMILYID, FAMILYNAME, FAMILYNAMEINHINDI, GOTRA, ADDRESSID, "
			+ "REGION, PHONE, ISPHONEWHATSAPPREGISTERED, EMAIL, FAMILYDISPLAYNAME, ACTIVE, FAMILYIMAGE, IMAGELASTUPDATED, CREATEDBY, "
			+ "CREATEDDATE, LASTUPDATEDBY, LASTUPDATEDDATE "
			+ "from " + BASE_SCHEMA_NAME + "FAMILY where FAMILYID = ? ";

	private static final String SEARCH_FAMILY = "select FAMILYID, FAMILYNAME, FAMILYNAMEINHINDI, GOTRA, ADDRESSID, "
			+ "REGION, PHONE, ISPHONEWHATSAPPREGISTERED, EMAIL, FAMILYDISPLAYNAME, ACTIVE, FAMILYIMAGE, IMAGELASTUPDATED, CREATEDBY, "
			+ "CREATEDDATE, LASTUPDATEDBY, LASTUPDATEDDATE "
			+ "from " + BASE_SCHEMA_NAME + "FAMILY where FAMILYDISPLAYNAME ilike ? ";
	
	public Family getFamilyByFamilyId(int familyId) {
		List<Family> familyList = getJdbcTemplate().query(SELECT_FAMILY_BY_FAMILYID, new FamilyRowMapper(), familyId);
		
		if(familyList.isEmpty()) {
			return null;
		}
		if(familyList.size() > 1) {
			throw new RuntimeException("Invalid family id entry");
		}
		return familyList.get(0);		
	}

	public List<Family> searchFamily(String searchString) {
        return getJdbcTemplate().query(SEARCH_FAMILY, new FamilyRowMapper(), "%" + searchString + "%");
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
		String sqlStatement = "INSERT INTO " + BASE_SCHEMA_NAME + "FAMILY (FAMILYNAME, FAMILYNAMEINHINDI, GOTRA, ADDRESSID, REGION, PHONE, ISPHONEWHATSAPPREGISTERED, EMAIL, FAMILYDISPLAYNAME, ACTIVE, FAMILYIMAGE, IMAGELASTUPDATED, CREATEDBY, CREATEDDATE, LASTUPDATEDBY, LASTUPDATEDDATE) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		PreparedStatement prepareStatement = aConection.prepareStatement(sqlStatement, new String[] { "familyid" });
		setStringInStatement(prepareStatement, 1, aFamily.getFamilyName());
		setStringInStatement(prepareStatement, 2, aFamily.getFamilyNameInHindi());
		setStringInStatement(prepareStatement, 3, aFamily.getGotra());
		setIntInStatement(prepareStatement, 4, aFamily.getAddressId());
		setStringInStatement(prepareStatement, 5, aFamily.getRegion());
		setStringInStatement(prepareStatement, 6, aFamily.getPhone());
		setBooleanInStatement(prepareStatement, 7, aFamily.isPhoneWhatsappRegistered());
		setStringInStatement(prepareStatement, 8, aFamily.getEmail());
		setStringInStatement(prepareStatement, 9, aFamily.getFamilyDisplayName());
		setBooleanInStatement(prepareStatement, 10, aFamily.isActive());
		setStringInStatement(prepareStatement, 11, aFamily.getFamilyImage());
		setTimestampInStatement(prepareStatement, 12, aFamily.getImageLastUpdated());
		setIntInStatement(prepareStatement, 13, aFamily.getCreatedBy());
		setTimestampInStatement(prepareStatement, 14, aFamily.getCreatedDate());
		setIntInStatement(prepareStatement, 15, aFamily.getLastUpdatedBy());
		setTimestampInStatement(prepareStatement, 16, aFamily.getLastUpdatedDate());
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
		String updateStatement = "UPDATE FAMILY SET FAMILYNAME = ? , FAMILYNAMEINHINDI = ? , GOTRA = ? , ADDRESSID = ? , REGION = ? , PHONE = ? , ISPHONEWHATSAPPREGISTERED = ? , EMAIL = ? , FAMILYDISPLAYNAME = ? , ACTIVE = ? , FAMILYIMAGE = ? , IMAGELASTUPDATED = ? , CREATEDBY = ? , CREATEDDATE = ? , LASTUPDATEDBY = ? , LASTUPDATEDDATE = ?  where FAMILYID = ?";

		PreparedStatement prepareStatement = aConection.prepareStatement(updateStatement);
		setStringInStatement(prepareStatement, 1, aFamily.getFamilyName());
		setStringInStatement(prepareStatement, 2, aFamily.getFamilyNameInHindi());
		setStringInStatement(prepareStatement, 3, aFamily.getGotra());
		setIntInStatement(prepareStatement, 4, aFamily.getAddressId());
		setStringInStatement(prepareStatement, 5, aFamily.getRegion());
		setStringInStatement(prepareStatement, 6, aFamily.getPhone());
		setBooleanInStatement(prepareStatement, 7, aFamily.isPhoneWhatsappRegistered());
		setStringInStatement(prepareStatement, 8, aFamily.getEmail());
		setStringInStatement(prepareStatement, 9, aFamily.getFamilyDisplayName());
		setBooleanInStatement(prepareStatement, 10, aFamily.isActive());
		setStringInStatement(prepareStatement, 11, aFamily.getFamilyImage());
		setTimestampInStatement(prepareStatement, 12, aFamily.getImageLastUpdated());
		setIntInStatement(prepareStatement, 13, aFamily.getCreatedBy());
		setTimestampInStatement(prepareStatement, 14, aFamily.getCreatedDate());
		setIntInStatement(prepareStatement, 15, aFamily.getLastUpdatedBy());
		setTimestampInStatement(prepareStatement, 16, aFamily.getLastUpdatedDate());
		setIntInStatement(prepareStatement, 17, aFamily.getFamilyId());
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
