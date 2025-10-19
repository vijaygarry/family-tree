/*
 * Copyright (c) 2018- 2021
 */

package com.neasaa.base.app.dao.pg;

import com.neasaa.base.app.entity.OtpVerificationHistory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;

@Repository
public class OtpVerificationHistoryDao extends AbstractDao {

  private PreparedStatement buildInsertStatement(
      Connection aConection, OtpVerificationHistory aOtpVerificationHistory) throws SQLException {
    String sqlStatement =
        "INSERT INTO OTPVERIFICATIONHISTORY (EMAILID, OTPTYPE, HASHOTPCODE, REQUESTID, STATUS, VERIFIEDAT, EXPIRYDATE, ATTEMPTS, LASTATTEMPTDATE, CREATEDDATE, LASTUPDATEDDATE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    PreparedStatement prepareStatement = aConection.prepareStatement(sqlStatement);
    setStringInStatement(prepareStatement, 1, aOtpVerificationHistory.getEmailId());
    setStringInStatement(prepareStatement, 2, aOtpVerificationHistory.getOtpType());
    setStringInStatement(prepareStatement, 3, aOtpVerificationHistory.getHashOtpCode());
    setStringInStatement(prepareStatement, 4, aOtpVerificationHistory.getRequestId());
    setStringInStatement(prepareStatement, 5, aOtpVerificationHistory.getStatus());
    setTimestampInStatement(prepareStatement, 6, aOtpVerificationHistory.getVerifiedAt());
    setTimestampInStatement(prepareStatement, 7, aOtpVerificationHistory.getExpiryDate());
    setIntInStatement(prepareStatement, 8, aOtpVerificationHistory.getAttempts());
    setTimestampInStatement(prepareStatement, 9, aOtpVerificationHistory.getLastAttemptDate());
    setTimestampInStatement(prepareStatement, 10, aOtpVerificationHistory.getCreatedDate());
    setTimestampInStatement(prepareStatement, 11, aOtpVerificationHistory.getLastUpdatedDate());
    return prepareStatement;
  }

  public int insertOtpVerificationHistory(OtpVerificationHistory aOtpVerificationHistory)
      throws SQLException {
    return getJdbcTemplate()
        .update(
            new PreparedStatementCreator() {
              @Override
              public PreparedStatement createPreparedStatement(Connection aCon)
                  throws SQLException {
                return buildInsertStatement(aCon, aOtpVerificationHistory);
              }
            });
  }

  public int deleteOtpVerificationHistory(OtpVerificationHistory aOtpVerificationHistory)
      throws SQLException {
    return getJdbcTemplate()
        .update(
            new PreparedStatementCreator() {
              @Override
              public PreparedStatement createPreparedStatement(Connection aConection)
                  throws SQLException {
                String deleteSqlQuery = "DELETE FROM OTPVERIFICATIONHISTORY WHERE SEQID = ?";
                PreparedStatement prepareStatement = aConection.prepareStatement(deleteSqlQuery);
                setLongInStatement(prepareStatement, 1, aOtpVerificationHistory.getSeqId());
                return prepareStatement;
              }
            });
  }

  public PreparedStatement buildUpdateStatement(
      Connection aConection, OtpVerificationHistory aOtpVerificationHistory) throws SQLException {
    String updateStatement =
        "UPDATE OTPVERIFICATIONHISTORY SET EMAILID = ? , OTPTYPE = ? , HASHOTPCODE = ? , REQUESTID = ? , STATUS = ? , VERIFIEDAT = ? , EXPIRYDATE = ? , ATTEMPTS = ? , LASTATTEMPTDATE = ? , CREATEDDATE = ? , LASTUPDATEDDATE = ?  where SEQID = ?";

    PreparedStatement prepareStatement = aConection.prepareStatement(updateStatement);
    setStringInStatement(prepareStatement, 1, aOtpVerificationHistory.getEmailId());
    setStringInStatement(prepareStatement, 2, aOtpVerificationHistory.getOtpType());
    setStringInStatement(prepareStatement, 3, aOtpVerificationHistory.getHashOtpCode());
    setStringInStatement(prepareStatement, 4, aOtpVerificationHistory.getRequestId());
    setStringInStatement(prepareStatement, 5, aOtpVerificationHistory.getStatus());
    setTimestampInStatement(prepareStatement, 6, aOtpVerificationHistory.getVerifiedAt());
    setTimestampInStatement(prepareStatement, 7, aOtpVerificationHistory.getExpiryDate());
    setIntInStatement(prepareStatement, 8, aOtpVerificationHistory.getAttempts());
    setTimestampInStatement(prepareStatement, 9, aOtpVerificationHistory.getLastAttemptDate());
    setTimestampInStatement(prepareStatement, 10, aOtpVerificationHistory.getCreatedDate());
    setTimestampInStatement(prepareStatement, 11, aOtpVerificationHistory.getLastUpdatedDate());
    setLongInStatement(prepareStatement, 12, aOtpVerificationHistory.getSeqId());
    return prepareStatement;
  }

  public int updateOtpVerificationHistory(OtpVerificationHistory aOtpVerificationHistory)
      throws SQLException {
    return getJdbcTemplate()
        .update(
            new PreparedStatementCreator() {
              @Override
              public PreparedStatement createPreparedStatement(Connection aCon)
                  throws SQLException {
                return buildUpdateStatement(aCon, aOtpVerificationHistory);
              }
            });
  }

  public OtpVerificationHistory fetchOtpVerificationHistory(
      OtpVerificationHistory aOtpVerificationHistory) throws SQLException {
    String selectQuery =
        "select  SEQID , EMAILID , OTPTYPE , HASHOTPCODE , REQUESTID , STATUS , VERIFIEDAT , EXPIRYDATE , ATTEMPTS , LASTATTEMPTDATE , CREATEDDATE , LASTUPDATEDDATE  from OTPVERIFICATIONHISTORY where SEQID = ? ";
    return getJdbcTemplate()
        .queryForObject(
            selectQuery, new OtpVerificationHistoryRowMapper(), aOtpVerificationHistory.getSeqId());
  }
}
