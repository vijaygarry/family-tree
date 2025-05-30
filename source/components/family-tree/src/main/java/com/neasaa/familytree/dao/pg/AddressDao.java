/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.familytree.dao.pg;

import com.neasaa.base.app.dao.pg.AbstractDao;
import com.neasaa.familytree.entity.Address;

import lombok.extern.log4j.Log4j2;

import java.sql.SQLException;
import java.sql.Connection;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;

@Log4j2
@Repository
public class AddressDao extends AbstractDao {

	public int addAddress (Address address) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
				return buildInsertStatement(aCon, address);
			}
		}, keyHolder);
		int addressId = -1;
		Number key = keyHolder.getKey();
		if (key != null) {
			addressId = key.intValue();
		}
		log.info("New Address is added with address id " + addressId);
		return addressId;
	}
	private PreparedStatement buildInsertStatement(Connection aConection, Address aAddress) throws SQLException {
		String sqlStatement = "INSERT INTO " + BASE_SCHEMA_NAME + "ADDRESS (ADDRESSLINE1, ADDRESSLINE2, ADDRESSLINE3, CITY, DISTRICT, STATE, POSTALCODE, COUNTRY, CREATEDBY, CREATEDDATE, LASTUPDATEDBY, LASTUPDATEDDATE) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		PreparedStatement prepareStatement = aConection.prepareStatement(sqlStatement, new String[] { "addressid" });
		setStringInStatement(prepareStatement, 1, aAddress.getAddressLine1());
		setStringInStatement(prepareStatement, 2, aAddress.getAddressLine2());
		setStringInStatement(prepareStatement, 3, aAddress.getAddressLine3());
		setStringInStatement(prepareStatement, 4, aAddress.getCity());
		setStringInStatement(prepareStatement, 5, aAddress.getDistrict());
		setStringInStatement(prepareStatement, 6, aAddress.getState());
		setStringInStatement(prepareStatement, 7, aAddress.getPostalCode());
		setStringInStatement(prepareStatement, 8, aAddress.getCountry());
		setIntInStatement(prepareStatement, 9, aAddress.getCreatedBy());
		setTimestampInStatement(prepareStatement, 10, aAddress.getCreatedDate());
		setIntInStatement(prepareStatement, 11, aAddress.getLastUpdatedBy());
		setTimestampInStatement(prepareStatement, 12, aAddress.getLastUpdatedDate());
		return prepareStatement;
	}


	public int deleteAddress(Address aAddress) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aConection) throws SQLException {
				String deleteSqlQuery = "DELETE FROM ADDRESS WHERE ADDRESSID = ?";
				PreparedStatement prepareStatement = aConection.prepareStatement(deleteSqlQuery);
				setIntInStatement(prepareStatement, 1, aAddress.getAddressId());
				return prepareStatement;
			}
		});

	}

	public PreparedStatement buildUpdateStatement(Connection aConection, Address aAddress) throws SQLException {
		String updateStatement = "UPDATE ADDRESS SET ADDRESSLINE1 = ? , ADDRESSLINE2 = ? , ADDRESSLINE3 = ? , CITY = ? , DISTRICT = ? , STATE = ? , POSTALCODE = ? , COUNTRY = ? , CREATEDBY = ? , CREATEDDATE = ? , LASTUPDATEDBY = ? , LASTUPDATEDDATE = ?  where ADDRESSID = ?";

		PreparedStatement prepareStatement = aConection.prepareStatement(updateStatement);
		setStringInStatement(prepareStatement, 1, aAddress.getAddressLine1());
		setStringInStatement(prepareStatement, 2, aAddress.getAddressLine2());
		setStringInStatement(prepareStatement, 3, aAddress.getAddressLine3());
		setStringInStatement(prepareStatement, 4, aAddress.getCity());
		setStringInStatement(prepareStatement, 5, aAddress.getDistrict());
		setStringInStatement(prepareStatement, 6, aAddress.getState());
		setStringInStatement(prepareStatement, 7, aAddress.getPostalCode());
		setStringInStatement(prepareStatement, 8, aAddress.getCountry());
		setIntInStatement(prepareStatement, 9, aAddress.getCreatedBy());
		setTimestampInStatement(prepareStatement, 10, aAddress.getCreatedDate());
		setIntInStatement(prepareStatement, 11, aAddress.getLastUpdatedBy());
		setTimestampInStatement(prepareStatement, 12, aAddress.getLastUpdatedDate());
		setIntInStatement(prepareStatement, 13, aAddress.getAddressId());
		return prepareStatement;
	}

	public int updateAddress(Address aAddress) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
				return buildUpdateStatement(aCon, aAddress);
			}
		});

	}

	public Address fetchAddress(Address aAddress) throws SQLException {
		String selectQuery = "select  ADDRESSID , ADDRESSLINE1 , ADDRESSLINE2 , ADDRESSLINE3 , CITY , DISTRICT , STATE , POSTALCODE , COUNTRY , CREATEDBY , CREATEDDATE , LASTUPDATEDBY , LASTUPDATEDDATE  from ADDRESS where ADDRESSID = ? ";
		return getJdbcTemplate().queryForObject(selectQuery, new AddressRowMapper(), aAddress.getAddressId());

	}

}
