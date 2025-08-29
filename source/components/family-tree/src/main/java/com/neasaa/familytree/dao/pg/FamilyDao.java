/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.familytree.dao.pg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.neasaa.base.app.operation.AuditInfo;
import com.neasaa.familytree.entity.FamilyEntity;
import com.neasaa.familytree.entity.FamilyMemberEntity;
import com.neasaa.familytree.entity.SearchFamilyEntity;
import com.neasaa.familytree.utils.DataFormatter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.neasaa.base.app.dao.pg.AbstractDao;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Repository
public class FamilyDao extends AbstractDao {
	
	private static final String SELECT_FAMILY_BY_FAMILYID = "SELECT f.familyid, f.familyname, f.familynameinhindi, f.gotra, f.addressid, " +
			"f.region, f.phone, f.isphonewhatsappregistered, f.email, f.familysearchtext, " +
			"f.active, f.familyimage, f.imagelastupdated, " +
			"a.addressline1, a.addressline2, a.addressline3, a.city, a.district, a.state, a.postalcode, a.country " +
			"FROM " + BASE_SCHEMA_NAME + "FAMILY f " +
			"LEFT JOIN " + BASE_SCHEMA_NAME + "ADDRESS a on f.addressid = a.addressid " +
			"WHERE f.familyid = ? and f.active = true";

	private static final String SEARCH_FAMILY = "SELECT f.familyid, f.familyname, f.familynameinhindi, f.gotra, " +
			"f.region, f.phone, f.isphonewhatsappregistered, f.familyimage, " +
			"m.FIRSTNAME, m. FIRSTNAMEINHINDI " +
			"FROM " + BASE_SCHEMA_NAME + "FAMILY f " +
			"LEFT JOIN " + BASE_SCHEMA_NAME + "FAMILYMEMBER m on f.familyid = m.familyid " +
			"WHERE m.HEADOFFAMILY = true and f.familysearchtext ilike ? and f.active = true";

	private static final String UPDATE_FAMILY_DISPLAY_NAME = "UPDATE " + BASE_SCHEMA_NAME + "FAMILY " +
			"SET region = ?, familysearchtext = ?, LASTUPDATEDBY = ?, lastupdateddate = ? WHERE FAMILYID = ?";

	private static final String FAMILY_INSERT_STATEMENT = "INSERT INTO " + BASE_SCHEMA_NAME + "FAMILY " +
			"(FAMILYNAME, FAMILYNAMEINHINDI, GOTRA, ADDRESSID, REGION, PHONE, ISPHONEWHATSAPPREGISTERED, EMAIL, FAMILYSEARCHTEXT, " +
			"ACTIVE, FAMILYIMAGE, IMAGELASTUPDATED, CREATEDBY, CREATEDDATE, LASTUPDATEDBY, LASTUPDATEDDATE) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String UPDATE_FAMILY_BY_ID_STATEMENT = "UPDATE " + BASE_SCHEMA_NAME + "FAMILY " +
			"SET FAMILYNAME = ? , FAMILYNAMEINHINDI = ? , GOTRA = ? , ADDRESSID = ? , REGION = ? , " +
			"PHONE = ? , ISPHONEWHATSAPPREGISTERED = ? , EMAIL = ? , FAMILYSEARCHTEXT = ? , " +
			"ACTIVE = ? , FAMILYIMAGE = ? , IMAGELASTUPDATED = ? , LASTUPDATEDBY = ? , LASTUPDATEDDATE = ?  " +
			"where FAMILYID = ?";

	private static final String DELETE_FAMILY_BY_ID_STATEMENT = "DELETE FROM " + BASE_SCHEMA_NAME + "FAMILY WHERE FAMILYID = ?";

	public FamilyEntity getFamilyByFamilyId(int familyId) {
		List<FamilyEntity> familyList = getJdbcTemplate().query(SELECT_FAMILY_BY_FAMILYID, new FamilyRowMapper(), familyId);
		
		if(familyList.isEmpty()) {
			return null;
		}
		if(familyList.size() > 1) {
			throw new RuntimeException("Invalid family id entry");
		}
		return familyList.get(0);		
	}

	public List<SearchFamilyEntity> searchFamily(String searchString) {
        return getJdbcTemplate().query(SEARCH_FAMILY, new SearchFamilyRowMapper(), "%" + searchString + "%");
	}
	
	public void updateFamilyDisplayName (FamilyEntity family, FamilyMemberEntity headOfFamily, AuditInfo auditInfo) {
		String familyRegion = DataFormatter.getRegion(family.getAddress());
		String searchString = DataFormatter.getFamilySearchString(family, headOfFamily, family.getAddress());
		log.info("Updating family search text to '{}' and region to '{}' for family id: {}", searchString, familyRegion, family.getFamilyId());
		getJdbcTemplate().update(UPDATE_FAMILY_DISPLAY_NAME, familyRegion, searchString, auditInfo.getLastUpdatedBy(), auditInfo.getLastUpdatedDate(), family.getFamilyId());
        log.info("Family region and search text is updated for family id: {}", family.getFamilyId());
	}

	public int addFamily (FamilyEntity aFamily) {
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
        log.info("New family added with id {}", familyId);
		return familyId;
	}

	private PreparedStatement buildInsertStatement(Connection aConection, FamilyEntity aFamily) throws SQLException {

		PreparedStatement prepareStatement = aConection.prepareStatement(FAMILY_INSERT_STATEMENT, new String[] { "familyid" });
		setStringInStatement(prepareStatement, 1, aFamily.getFamilyName());
		setStringInStatement(prepareStatement, 2, aFamily.getFamilyNameInHindi());
		setStringInStatement(prepareStatement, 3, aFamily.getGotra());
		setIntInStatement(prepareStatement, 4, aFamily.getAddressId());
		setStringInStatement(prepareStatement, 5, aFamily.getRegion());
		setStringInStatement(prepareStatement, 6, aFamily.getPhone());
		setBooleanInStatement(prepareStatement, 7, aFamily.isPhoneWhatsappRegistered());
		setStringInStatement(prepareStatement, 8, aFamily.getEmail());
		setStringInStatement(prepareStatement, 9, aFamily.getFamilysearchtext());
		setBooleanInStatement(prepareStatement, 10, aFamily.isActive());
		setStringInStatement(prepareStatement, 11, aFamily.getFamilyImage());
		setTimestampInStatement(prepareStatement, 12, aFamily.getImageLastUpdated());
		setIntInStatement(prepareStatement, 13, aFamily.getCreatedBy());
		setTimestampInStatement(prepareStatement, 14, aFamily.getCreatedDate());
		setIntInStatement(prepareStatement, 15, aFamily.getLastUpdatedBy());
		setTimestampInStatement(prepareStatement, 16, aFamily.getLastUpdatedDate());
		return prepareStatement;
	}



	public int updateFamily(FamilyEntity aFamily) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
				return buildUpdateStatement(aCon, aFamily);
			}
		});

	}

	public PreparedStatement buildUpdateStatement(Connection aConection, FamilyEntity aFamily) throws SQLException {

		PreparedStatement prepareStatement = aConection.prepareStatement(UPDATE_FAMILY_BY_ID_STATEMENT);
		setStringInStatement(prepareStatement, 1, aFamily.getFamilyName());
		setStringInStatement(prepareStatement, 2, aFamily.getFamilyNameInHindi());
		setStringInStatement(prepareStatement, 3, aFamily.getGotra());
		setIntInStatement(prepareStatement, 4, aFamily.getAddressId());
		setStringInStatement(prepareStatement, 5, aFamily.getRegion());
		setStringInStatement(prepareStatement, 6, aFamily.getPhone());
		setBooleanInStatement(prepareStatement, 7, aFamily.isPhoneWhatsappRegistered());
		setStringInStatement(prepareStatement, 8, aFamily.getEmail());
		setStringInStatement(prepareStatement, 9, aFamily.getFamilysearchtext());
		setBooleanInStatement(prepareStatement, 10, aFamily.isActive());
		setStringInStatement(prepareStatement, 11, aFamily.getFamilyImage());
		setTimestampInStatement(prepareStatement, 12, aFamily.getImageLastUpdated());
		setIntInStatement(prepareStatement, 13, aFamily.getLastUpdatedBy());
		setTimestampInStatement(prepareStatement, 14, aFamily.getLastUpdatedDate());
		setIntInStatement(prepareStatement, 15, aFamily.getFamilyId());
		return prepareStatement;

	}

	public int deleteFamilyById (int familyId) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aConection) throws SQLException {
				PreparedStatement prepareStatement = aConection.prepareStatement(DELETE_FAMILY_BY_ID_STATEMENT);
				setIntInStatement(prepareStatement, 1,familyId);
				return prepareStatement;
			}
		});
	}

}
