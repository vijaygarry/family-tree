/*
 * Copyright (c) 2018- 2021
 */

package com.neasaa.base.app.dao.pg;

import com.neasaa.base.app.entity.OtpVerificationHistory;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class OtpVerificationHistoryRowMapper implements RowMapper<OtpVerificationHistory> {

  @Override
  public OtpVerificationHistory mapRow(ResultSet aRs, int aRowNum) throws SQLException {
    OtpVerificationHistory otpVerificationHistory = new OtpVerificationHistory();
    otpVerificationHistory.setSeqId(aRs.getLong("SEQID"));
    otpVerificationHistory.setEmailId(aRs.getString("EMAILID"));
    otpVerificationHistory.setOtpType(aRs.getString("OTPTYPE"));
    otpVerificationHistory.setHashOtpCode(aRs.getString("HASHOTPCODE"));
    otpVerificationHistory.setRequestId(aRs.getString("REQUESTID"));
    otpVerificationHistory.setStatus(aRs.getString("STATUS"));
    otpVerificationHistory.setVerifiedAt(AbstractDao.getTimestampFromResultSet(aRs, "VERIFIEDAT"));
    otpVerificationHistory.setExpiryDate(AbstractDao.getTimestampFromResultSet(aRs, "EXPIRYDATE"));
    otpVerificationHistory.setAttempts(aRs.getInt("ATTEMPTS"));
    otpVerificationHistory.setLastAttemptDate(
        AbstractDao.getTimestampFromResultSet(aRs, "LASTATTEMPTDATE"));
    otpVerificationHistory.setCreatedDate(
        AbstractDao.getTimestampFromResultSet(aRs, "CREATEDDATE"));
    otpVerificationHistory.setLastUpdatedDate(
        AbstractDao.getTimestampFromResultSet(aRs, "LASTUPDATEDDATE"));
    return otpVerificationHistory;
  }
}
