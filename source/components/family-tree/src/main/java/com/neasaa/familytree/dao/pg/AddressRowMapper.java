/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.familytree.dao.pg;

import java.sql.SQLException;

import com.neasaa.base.app.dao.pg.AbstractDao;
import com.neasaa.familytree.entity.Address;
import java.sql.ResultSet;
import org.springframework.jdbc.core.RowMapper;

public class AddressRowMapper implements RowMapper<Address> {

	@Override
	public Address mapRow(ResultSet aRs, int aRowNum) throws SQLException {
		Address address = new Address();
		address.setAddressId(aRs.getInt("ADDRESSID"));
		address.setAddressLine1(aRs.getString("ADDRESSLINE1"));
		address.setAddressLine2(aRs.getString("ADDRESSLINE2"));
		address.setAddressLine3(aRs.getString("ADDRESSLINE3"));
		address.setCity(aRs.getString("CITY"));
		address.setDistrict(aRs.getString("DISTRICT"));
		address.setState(aRs.getString("STATE"));
		address.setPostalCode(aRs.getString("POSTALCODE"));
		address.setCountry(aRs.getString("COUNTRY"));
		address.setCreatedBy(aRs.getInt("CREATEDBY"));
		address.setCreatedDate(AbstractDao.getTimestampFromResultSet(aRs, "CREATEDDATE"));
		address.setLastUpdatedBy(aRs.getInt("LASTUPDATEDBY"));
		address.setLastUpdatedDate(AbstractDao.getTimestampFromResultSet(aRs, "LASTUPDATEDDATE"));
		return address;
	}

}
