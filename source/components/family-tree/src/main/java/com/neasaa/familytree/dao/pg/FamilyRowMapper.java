/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.familytree.dao.pg;

import java.sql.SQLException;

import com.neasaa.base.app.dao.pg.AbstractDao;
import com.neasaa.familytree.entity.Family;
import java.sql.ResultSet;
import org.springframework.jdbc.core.RowMapper;

public class FamilyRowMapper implements RowMapper<Family> {

	@Override
	public Family mapRow(ResultSet aRs, int aRowNum) throws SQLException {
		Family family = new Family();
		family.setFamilyId(aRs.getInt("FAMILYID"));
		family.setFamilyName(aRs.getString("FAMILYNAME"));
		family.setGotra(aRs.getString("GOTRA"));
		family.setAddressId(aRs.getInt("ADDRESSID"));
		family.setRegion(aRs.getString("REGION"));
		family.setPhone(aRs.getString("PHONE"));
		family.setPhoneWhatsappRegistered(aRs.getBoolean("ISPHONEWHATSAPPREGISTERED"));
		family.setEmail(aRs.getString("EMAIL"));
		family.setFamilyDisplayName(aRs.getString("FAMILYDISPLAYNAME"));
		family.setActive(aRs.getBoolean("ACTIVE"));
		family.setFamilyImage(aRs.getString("FAMILYIMAGE"));
		family.setImageLastUpdated(AbstractDao.getTimestampFromResultSet(aRs, "IMAGELASTUPDATED"));
		family.setCreatedBy(aRs.getInt("CREATEDBY"));
		family.setCreatedDate(AbstractDao.getTimestampFromResultSet(aRs, "CREATEDDATE"));
		family.setLastUpdatedBy(aRs.getInt("LASTUPDATEDBY"));
		family.setLastUpdatedDate(AbstractDao.getTimestampFromResultSet(aRs, "LASTUPDATEDDATE"));
		return family;
	}

}
