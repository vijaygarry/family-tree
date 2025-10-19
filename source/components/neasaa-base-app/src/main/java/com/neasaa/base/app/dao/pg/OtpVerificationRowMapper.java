/*
 * Copyright (c) 2018- 2021
 */

package com.neasaa.base.app.dao.pg;

import com.neasaa.base.app.entity.OtpVerification;
import com.neasaa.base.app.enums.OTPStatus;
import com.neasaa.base.app.enums.OTPType;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class OtpVerificationRowMapper implements RowMapper<OtpVerification> {

  @Override
  public OtpVerification mapRow(ResultSet aRs, int aRowNum) throws SQLException {
    OtpVerification otpVerification = new OtpVerification();
    otpVerification.setEmailId(aRs.getString("EMAILID"));
    otpVerification.setOtpType(OTPType.getOTPType(aRs.getString("OTPTYPE")));
    otpVerification.setHashOtpCode(aRs.getString("HASHOTPCODE"));
    otpVerification.setRequestId(aRs.getString("REQUESTID"));
    otpVerification.setStatus(OTPStatus.getOTPStatus(aRs.getString("STATUS")));
    otpVerification.setVerifiedAt(AbstractDao.getTimestampFromResultSet(aRs, "VERIFIEDAT"));
    otpVerification.setExpiryDate(AbstractDao.getTimestampFromResultSet(aRs, "EXPIRYDATE"));
    otpVerification.setAttempts(aRs.getInt("ATTEMPTS"));
    otpVerification.setLastAttemptDate(
        AbstractDao.getTimestampFromResultSet(aRs, "LASTATTEMPTDATE"));
    otpVerification.setCreatedDate(AbstractDao.getTimestampFromResultSet(aRs, "CREATEDDATE"));
    otpVerification.setLastUpdatedDate(
        AbstractDao.getTimestampFromResultSet(aRs, "LASTUPDATEDDATE"));
    return otpVerification;
  }
}
