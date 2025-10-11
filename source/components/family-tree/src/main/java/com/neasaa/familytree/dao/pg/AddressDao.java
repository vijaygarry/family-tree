/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.familytree.dao.pg;

import com.neasaa.base.app.dao.pg.AbstractDao;

import com.neasaa.base.app.operation.exception.InternalServerException;
import com.neasaa.familytree.entity.AddressEntity;
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
	private static final String  SELECT_ADDRESS_BY_ADDRESS_ID = "select  ADDRESSID , ADDRESSLINE1 , ADDRESSLINE2 , ADDRESSLINE3 , CITY , DISTRICT , STATE , POSTALCODE , COUNTRY , CREATEDBY , CREATEDDATE , LASTUPDATEDBY , LASTUPDATEDDATE  " +
			"from " + BASE_SCHEMA_NAME + "ADDRESS " +
			"where ADDRESSID = ? ";

	private static final String  SELECT_ADDRESS_BY_FAMILY_ID = "select  A.ADDRESSID , ADDRESSLINE1 , ADDRESSLINE2 , ADDRESSLINE3 , CITY , DISTRICT , STATE , POSTALCODE , COUNTRY , A.CREATEDBY , A.CREATEDDATE , A.LASTUPDATEDBY , A.LASTUPDATEDDATE  " +
			"from " + BASE_SCHEMA_NAME + "ADDRESS A, " + BASE_SCHEMA_NAME + "FAMILY F " +
			"where F.FAMILYID = ? AND A.ADDRESSID = F.ADDRESSID ";

	private static final String UPDATE_ADDRESS_BY_ID = "UPDATE " + BASE_SCHEMA_NAME + "ADDRESS " +
			" SET ADDRESSLINE1 = ? , ADDRESSLINE2 = ? , ADDRESSLINE3 = ? , CITY = ? , DISTRICT = ? , STATE = ? , " +
			" POSTALCODE = ? , COUNTRY = ? , LASTUPDATEDBY = ? , LASTUPDATEDDATE = ?  where ADDRESSID = ?";

	private static String DELETE_ADDRESS_BY_ADDRESS_ID = "DELETE FROM " + BASE_SCHEMA_NAME + "ADDRESS WHERE ADDRESSID = ?";

	public int addAddress (AddressEntity address) {
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

	public AddressEntity getAddressById (int addressId) {
		return getJdbcTemplate().queryForObject(SELECT_ADDRESS_BY_ADDRESS_ID, new AddressRowMapper(), addressId);
	}

	public AddressEntity getAddressByFamilyId (int familyId) {
		return getJdbcTemplate().queryForObject(SELECT_ADDRESS_BY_FAMILY_ID, new AddressRowMapper(), familyId);
	}

	private PreparedStatement buildInsertStatement(Connection aConection, AddressEntity aAddress) throws SQLException {
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


	public int deleteAddress(int addressId) {
		//TODO: Create a history record before deleting the address
		try {
			return getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection aConection) throws SQLException {
					PreparedStatement prepareStatement = aConection.prepareStatement(DELETE_ADDRESS_BY_ADDRESS_ID);
					setIntInStatement(prepareStatement, 1, addressId);
					return prepareStatement;
				}
			});
		} catch (Exception e) {
			log.error("Failed to delete the existing address with id {}", addressId, e);
			throw new InternalServerException("Failed to update address, please try again later");
		}
	}

	public PreparedStatement buildUpdateStatement(Connection aConection, AddressEntity aAddress) throws SQLException {
		PreparedStatement prepareStatement = aConection.prepareStatement(UPDATE_ADDRESS_BY_ID);
		setStringInStatement(prepareStatement, 1, aAddress.getAddressLine1());
		setStringInStatement(prepareStatement, 2, aAddress.getAddressLine2());
		setStringInStatement(prepareStatement, 3, aAddress.getAddressLine3());
		setStringInStatement(prepareStatement, 4, aAddress.getCity());
		setStringInStatement(prepareStatement, 5, aAddress.getDistrict());
		setStringInStatement(prepareStatement, 6, aAddress.getState());
		setStringInStatement(prepareStatement, 7, aAddress.getPostalCode());
		setStringInStatement(prepareStatement, 8, aAddress.getCountry());
		setIntInStatement(prepareStatement, 9, aAddress.getLastUpdatedBy());
		setTimestampInStatement(prepareStatement, 10, aAddress.getLastUpdatedDate());
		setIntInStatement(prepareStatement, 11, aAddress.getAddressId());
		return prepareStatement;
	}

	public int updateAddress(AddressEntity aAddress) {
		try {
			// TODO: Create a history record with audit details
			return getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
					return buildUpdateStatement(aCon, aAddress);
				}
			});
		} catch (Exception e) {
            log.error("Failed to update address with id {}", aAddress.getAddressId(), e);
			throw new InternalServerException("Failed to update address, please try again later");
		}

	}

}
