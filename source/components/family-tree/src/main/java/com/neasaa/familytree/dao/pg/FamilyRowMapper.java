/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.familytree.dao.pg;

import java.sql.SQLException;

import com.neasaa.base.app.dao.pg.AbstractDao;

import java.sql.ResultSet;
import java.util.Date;

import com.neasaa.familytree.entity.AddressEntity;
import com.neasaa.familytree.entity.FamilyEntity;
import com.neasaa.familytree.utils.DataFormatter;
import org.springframework.jdbc.core.RowMapper;

public class FamilyRowMapper implements RowMapper<FamilyEntity> {

	@Override
	public FamilyEntity mapRow(ResultSet aRs, int aRowNum) throws SQLException {
		FamilyEntity family = new FamilyEntity();
		family.setFamilyId(aRs.getInt("FAMILYID"));
		family.setFamilyName(aRs.getString("FAMILYNAME"));
		family.setFamilyNameInHindi(aRs.getString("FAMILYNAMEINHINDI"));
		family.setGotra(aRs.getString("GOTRA"));
		family.setAddressId(aRs.getInt("ADDRESSID"));
		family.setRegion(aRs.getString("REGION"));
		String phone = aRs.getString("PHONE");
		family.setPhone(DataFormatter.formatPhoneNumber(phone));
		family.setPhoneWhatsappRegistered(aRs.getBoolean("ISPHONEWHATSAPPREGISTERED"));
		family.setEmail(aRs.getString("EMAIL"));
		family.setFamilysearchtext(aRs.getString("FAMILYSEARCHTEXT"));
		family.setActive(aRs.getBoolean("ACTIVE"));
		family.setFamilyImage(aRs.getString("FAMILYIMAGE"));
		family.setImageLastUpdated(AbstractDao.getTimestampFromResultSet(aRs, "IMAGELASTUPDATED"));
		String addressLine1 = aRs.getString("ADDRESSLINE1");
		if (addressLine1 == null) {
			return family; // No address associated with the family
		}
		AddressEntity address = new AddressEntity();
		address.setAddressLine1(addressLine1);
		address.setAddressLine2(aRs.getString("ADDRESSLINE2"));
		address.setAddressLine3(aRs.getString("ADDRESSLINE3"));
		address.setCity(aRs.getString("CITY"));
		address.setDistrict(aRs.getString("DISTRICT"));
		address.setState(aRs.getString("STATE"));
		address.setPostalCode(aRs.getString("POSTALCODE"));
		address.setCountry(aRs.getString("COUNTRY"));
		family.setAddress(address);
		return family;
	}

}
