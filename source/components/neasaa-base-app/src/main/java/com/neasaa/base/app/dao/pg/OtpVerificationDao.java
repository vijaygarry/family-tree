/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.base.app.dao.pg;

import com.neasaa.base.app.entity.AppUser;
import com.neasaa.base.app.entity.OtpVerification;
import java.sql.SQLException;
import java.sql.Connection;

import com.neasaa.base.app.enums.OTPType;
import com.neasaa.base.app.operation.exception.InternalServerException;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.Date;
import java.util.List;

@Log4j2
@Repository
public class OtpVerificationDao extends AbstractDao {

	private static final String GET_OTP_INFORMATION_BY_EMAIL_AND_TYPE = "select  EMAILID , OTPTYPE , HASHOTPCODE , REQUESTID , STATUS , VERIFIEDAT , EXPIRYDATE , ATTEMPTS , LASTATTEMPTDATE , CREATEDDATE , LASTUPDATEDDATE  " +
			" from " + BASE_SCHEMA_NAME + "OTPVERIFICATION where EMAILID = ?  and OTPTYPE = ? ";

	private static final String INSERT_INTO_HISTORY_FROM_MAIN = "INSERT INTO " + BASE_SCHEMA_NAME + "OTPVERIFICATIONHISTORY (EMAILID, OTPTYPE, HASHOTPCODE, REQUESTID, STATUS, VERIFIEDAT, EXPIRYDATE, ATTEMPTS, LASTATTEMPTDATE, CREATEDDATE, LASTUPDATEDDATE)"
			+ " SELECT EMAILID, OTPTYPE, HASHOTPCODE, REQUESTID, ?, ?, EXPIRYDATE, ?, ?, CREATEDDATE, ? FROM "  + BASE_SCHEMA_NAME +  "OTPVERIFICATION "
			+ " WHERE EMAILID = ?  and OTPTYPE = ?";

	private static final String DELETE_OTP = "DELETE FROM " + BASE_SCHEMA_NAME + "OTPVERIFICATION WHERE EMAILID = ?  and OTPTYPE = ?";

	public OtpVerification getOtpInformation(String emailId, OTPType otpType) {
		try {
			List<OtpVerification> otpVerificationList= getJdbcTemplate().query(GET_OTP_INFORMATION_BY_EMAIL_AND_TYPE, new OtpVerificationRowMapper(), emailId, otpType.name());
			if(otpVerificationList.isEmpty()) {
				return null;
			}
			return otpVerificationList.get(0);
		} catch (Exception e) {
			throw new InternalServerException("Internal server exception. Please try again later.", e);
		}
	}

	public int insertOtpVerification(OtpVerification aOtpVerification) {
		try {
			return getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
					return buildInsertStatement(aCon, aOtpVerification);
				}
			});
		} catch (Exception e) {
			throw new InternalServerException("Internal server exception. Please try again later.", e);
		}
	}

	public void moveOtpToHistory(OtpVerification aOtpVerification) {
		try {
			// 1. Insert into OTPHistory from OTP
			Date currentTimestamp = new Date();
			log.info("Inserting OTP to history for emailId: {} and otpType: {}", aOtpVerification.getEmailId(), aOtpVerification.getOtpType().name());
			int recordInserted = getJdbcTemplate().update(INSERT_INTO_HISTORY_FROM_MAIN, aOtpVerification.getStatus().name(), aOtpVerification.getVerifiedAt(),
					aOtpVerification.getAttempts(), aOtpVerification.getLastAttemptDate(),
					currentTimestamp,
					aOtpVerification.getEmailId(), aOtpVerification.getOtpType().name());
			log.info("Records inserted into OTP history: {}", recordInserted);
			// 2. Delete from OTP
			int recordsDeleted = getJdbcTemplate().update(DELETE_OTP, aOtpVerification.getEmailId(), aOtpVerification.getOtpType().name());
			log.info("Records deleted from OTP table: {}", recordsDeleted);
		} catch (Exception e) {
			throw new InternalServerException("Internal server exception. Please try again later.", e);
		}
	}

	private PreparedStatement buildInsertStatement(Connection aConection, OtpVerification aOtpVerification) throws SQLException {
		String sqlStatement = "INSERT INTO " + BASE_SCHEMA_NAME + "OTPVERIFICATION (EMAILID, OTPTYPE, HASHOTPCODE, REQUESTID, STATUS, VERIFIEDAT, EXPIRYDATE, ATTEMPTS, LASTATTEMPTDATE, CREATEDDATE, LASTUPDATEDDATE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		PreparedStatement prepareStatement = aConection.prepareStatement(sqlStatement);
		setStringInStatement(prepareStatement, 1, aOtpVerification.getEmailId().toLowerCase().trim());
		setStringInStatement(prepareStatement, 2, aOtpVerification.getOtpType().name());
		setStringInStatement(prepareStatement, 3, aOtpVerification.getHashOtpCode());
		setStringInStatement(prepareStatement, 4, aOtpVerification.getRequestId());
		setStringInStatement(prepareStatement, 5, aOtpVerification.getStatus().name());
		setTimestampInStatement(prepareStatement, 6, aOtpVerification.getVerifiedAt());
		setTimestampInStatement(prepareStatement, 7, aOtpVerification.getExpiryDate());
		setIntInStatement(prepareStatement, 8, aOtpVerification.getAttempts());
		setTimestampInStatement(prepareStatement, 9, aOtpVerification.getLastAttemptDate());
		setTimestampInStatement(prepareStatement, 10, aOtpVerification.getCreatedDate());
		setTimestampInStatement(prepareStatement, 11, aOtpVerification.getLastUpdatedDate());
		return prepareStatement;
	}



	public int deleteOtpVerification(OtpVerification aOtpVerification) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aConection) throws SQLException {
				String deleteSqlQuery = "DELETE FROM OTPVERIFICATION WHERE EMAILID = ? and OTPTYPE = ?";
				PreparedStatement prepareStatement = aConection.prepareStatement(deleteSqlQuery);
				setStringInStatement(prepareStatement, 1, aOtpVerification.getEmailId());
				setStringInStatement(prepareStatement, 2, aOtpVerification.getOtpType().name());
				return prepareStatement;
			}
		});

	}

	public PreparedStatement buildUpdateStatement(Connection aConection, OtpVerification aOtpVerification) throws SQLException {
		String updateStatement = "UPDATE OTPVERIFICATION SET HASHOTPCODE = ? , REQUESTID = ? , STATUS = ? , VERIFIEDAT = ? , EXPIRYDATE = ? , ATTEMPTS = ? , LASTATTEMPTDATE = ? , CREATEDDATE = ? , LASTUPDATEDDATE = ?  where EMAILID = ? and OTPTYPE = ?";

		PreparedStatement prepareStatement = aConection.prepareStatement(updateStatement);
		setStringInStatement(prepareStatement, 1, aOtpVerification.getHashOtpCode());
		setStringInStatement(prepareStatement, 2, aOtpVerification.getRequestId());
		setStringInStatement(prepareStatement, 3, aOtpVerification.getStatus().name());
		setTimestampInStatement(prepareStatement, 4, aOtpVerification.getVerifiedAt());
		setTimestampInStatement(prepareStatement, 5, aOtpVerification.getExpiryDate());
		setIntInStatement(prepareStatement, 6, aOtpVerification.getAttempts());
		setTimestampInStatement(prepareStatement, 7, aOtpVerification.getLastAttemptDate());
		setTimestampInStatement(prepareStatement, 8, aOtpVerification.getCreatedDate());
		setTimestampInStatement(prepareStatement, 9, aOtpVerification.getLastUpdatedDate());
		setStringInStatement(prepareStatement, 10, aOtpVerification.getEmailId());
		setStringInStatement(prepareStatement, 11, aOtpVerification.getOtpType().name());
		return prepareStatement;
	}

	public int updateOtpVerification(OtpVerification aOtpVerification) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
				return buildUpdateStatement(aCon, aOtpVerification);
			}
		});

	}



}
