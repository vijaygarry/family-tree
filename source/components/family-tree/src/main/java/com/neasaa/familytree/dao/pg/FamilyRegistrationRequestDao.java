/*
 * Copyright (c) 2018- 2021
 */

package com.neasaa.familytree.dao.pg;

import com.neasaa.base.app.dao.pg.AbstractDao;
import com.neasaa.base.app.operation.AuditInfo;
import com.neasaa.familytree.entity.FamilyMemberRegistrationEntity;
import com.neasaa.familytree.entity.FamilyRegistrationRequestEntity;
import com.neasaa.familytree.enums.FamilyRegistrationStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Log4j2
@Repository
public class FamilyRegistrationRequestDao extends AbstractDao {

  private static final String INSERT_FAMILY_REGISTRATION_REQUEST =
      "INSERT INTO " + BASE_SCHEMA_NAME + "FAMILYREGISTRATIONREQUEST "
          + "(SAMAJID, FAMILYNAME, FAMILYNAMEINHINDI, GOTRA, ADDRESSLINE1, ADDRESSLINE2, ADDRESSLINE3, "
          + "CITY, DISTRICT, STATE, POSTALCODE, COUNTRY, PHONE, EMAIL, CREATEDDATE, CLIENTINFO, STATUS) "
          + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

  private static final String UPDATE_FAMILY_REGISTRATION_STATUS =
      "UPDATE " + BASE_SCHEMA_NAME + "FAMILYREGISTRATIONREQUEST "
          + "SET STATUS = ?, familyaddedby=?, dateadded=?, familyid=? WHERE FAMILYREQUESTID = ?";

  private static final String SELECT_FAMILY_REGISTRATION_REQUEST_BY_STATUS =
      "SELECT FAMILYREQUESTID, SAMAJID, FAMILYNAME, FAMILYNAMEINHINDI, GOTRA, ADDRESSLINE1, ADDRESSLINE2, "
          + "ADDRESSLINE3, CITY, DISTRICT, STATE, POSTALCODE, COUNTRY, PHONE, EMAIL, CREATEDDATE, CLIENTINFO, "
          + "STATUS, FAMILYADDEDBY, DATEADDED, FAMILYID "
          + "FROM " + BASE_SCHEMA_NAME + "FAMILYREGISTRATIONREQUEST "
          + "WHERE STATUS = ? ORDER BY CREATEDDATE DESC";

  private static final String INSERT_FAMILY_MEMBER_REGISTRATION =
      "INSERT INTO " + BASE_SCHEMA_NAME + "FAMILYMEMBERREGISTRATION "
          + "(FAMILYREQUESTID, SAMAJID, HEADOFFAMILY, FIRSTNAME, FIRSTNAMEINHINDI, GENDER, BIRTHDAY, "
          + "BIRTHMONTH, BIRTHYEAR, MARITALSTATUS, WEDDINGDATE, PHONE, EMAIL, ADDRESSSAMEASFAMILY, "
          + "EDUCATIONDETAILS, OCCUPATION, CREATEDDATE, RELATIONSHIPTYPE, RELATEDMEMBERID) "
          + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

  private static final String UPDATE_FAMILY_MEMBER_REGISTRATION =
      "UPDATE " + BASE_SCHEMA_NAME + "FAMILYMEMBERREGISTRATION "
          + "SET FIRSTNAME = ?, FIRSTNAMEINHINDI = ?, GENDER = ?, BIRTHDAY = ?, BIRTHMONTH = ?, BIRTHYEAR = ?, "
          + "MARITALSTATUS = ?, WEDDINGDATE = ?, PHONE = ?, EMAIL = ?, ADDRESSSAMEASFAMILY = ?, "
          + "EDUCATIONDETAILS = ?, OCCUPATION = ?, RELATIONSHIPTYPE = ? WHERE MEMBERREQUESTID = ?";

  private static final String SELECT_FAMILY_REGISTRATION_BY_FAMILY_REQUEST_ID =
          "SELECT FAMILYREQUESTID, SAMAJID, FAMILYNAME, FAMILYNAMEINHINDI, GOTRA, ADDRESSLINE1, ADDRESSLINE2, "
          + "ADDRESSLINE3, CITY, DISTRICT, STATE, POSTALCODE, COUNTRY, PHONE, EMAIL, CREATEDDATE, CLIENTINFO, "
          + "STATUS, FAMILYADDEDBY, DATEADDED, FAMILYID "
          + "FROM " + BASE_SCHEMA_NAME + "FAMILYREGISTRATIONREQUEST "
          + "WHERE FAMILYREQUESTID = ?";

  private static final String SELECT_FAMILY_MEMBER_REGISTRATION_BY_FAMILY_REQUEST_ID =
      "SELECT MEMBERREQUESTID, FAMILYREQUESTID, SAMAJID, HEADOFFAMILY, FIRSTNAME, FIRSTNAMEINHINDI, GENDER, "
          + "BIRTHDAY, BIRTHMONTH, BIRTHYEAR, MARITALSTATUS, WEDDINGDATE, PHONE, EMAIL, ADDRESSSAMEASFAMILY, "
          + "EDUCATIONDETAILS, OCCUPATION, CREATEDDATE, MEMBERID, RELATIONSHIPTYPE, RELATEDMEMBERID "
          + "FROM " + BASE_SCHEMA_NAME + "FAMILYMEMBERREGISTRATION "
          + "WHERE FAMILYREQUESTID = ?";

  private static final String MEMBER_EXISTS_WITH_EMAIL_PHONE = "SELECT COUNT(1) FROM " + BASE_SCHEMA_NAME + "FAMILYMEMBERREGISTRATION m " +
          "JOIN " + BASE_SCHEMA_NAME + "FAMILYREGISTRATIONREQUEST r ON m.FAMILYREQUESTID = r.FAMILYREQUESTID " +
          "WHERE (r.STATUS = ? OR r.STATUS = ?) ";

  public int insertFamilyRegistrationRequest(FamilyRegistrationRequestEntity entity) {
    KeyHolder keyHolder = new GeneratedKeyHolder();
    getJdbcTemplate().update(new PreparedStatementCreator() {
      @Override
      public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        PreparedStatement ps = con.prepareStatement(INSERT_FAMILY_REGISTRATION_REQUEST, new String[]{"familyrequestid"});
        ps.setShort(1, entity.getSamajId());
        AbstractDao.setStringInStatement(ps, 2, entity.getFamilyName());
        AbstractDao.setStringInStatement(ps, 3, entity.getFamilyNameInHindi());
        AbstractDao.setStringInStatement(ps, 4, entity.getGotra());
        AbstractDao.setStringInStatement(ps, 5, entity.getAddressLine1());
        AbstractDao.setStringInStatement(ps, 6, entity.getAddressLine2());
        AbstractDao.setStringInStatement(ps, 7, entity.getAddressLine3());
        AbstractDao.setStringInStatement(ps, 8, entity.getCity());
        AbstractDao.setStringInStatement(ps, 9, entity.getDistrict());
        AbstractDao.setStringInStatement(ps, 10, entity.getState());
        AbstractDao.setStringInStatement(ps, 11, entity.getPostalCode());
        AbstractDao.setStringInStatement(ps, 12, entity.getCountry());
        AbstractDao.setStringInStatement(ps, 13, entity.getPhone());
        AbstractDao.setStringInStatement(ps, 14, entity.getEmail());
        AbstractDao.setTimestampInStatement(ps, 15, entity.getCreatedDate());
        AbstractDao.setStringInStatement(ps, 16, entity.getClientInfo());
        AbstractDao.setStringInStatement(ps, 17, entity.getStatus() != null ? entity.getStatus().name() : null);
        return ps;
      }
    }, keyHolder);
    int familyRegistrationId = -1;
    Number key = keyHolder.getKey();
    if (key != null) {
      familyRegistrationId = key.intValue();
    }
    log.info("Family Registration id {} for family {}", familyRegistrationId, entity.getFamilyName());
    return keyHolder.getKey().intValue();
  }

  public void updateFamilyRegistrationRequest(FamilyRegistrationRequestEntity entity, AuditInfo auditInfo) {
    getJdbcTemplate().update(UPDATE_FAMILY_REGISTRATION_STATUS,
        entity.getStatus().name(), auditInfo.getLastUpdatedBy(), auditInfo.getLastUpdatedDate(), entity.getFamilyId(), entity.getFamilyRequestId());
  }

  public List<FamilyRegistrationRequestEntity> getFamilyRegistrationRequestsByStatus(FamilyRegistrationStatus status) {
    return getJdbcTemplate().query(SELECT_FAMILY_REGISTRATION_REQUEST_BY_STATUS,
        new FamilyRegistrationRequestRowMapper(), status.name());
  }

  public int insertFamilyMemberRegistration(FamilyMemberRegistrationEntity entity) {
    KeyHolder keyHolder = new GeneratedKeyHolder();
    getJdbcTemplate().update(new PreparedStatementCreator() {
      @Override
      public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        PreparedStatement ps = con.prepareStatement(INSERT_FAMILY_MEMBER_REGISTRATION, new String[]{"memberrequestid"});
        ps.setInt(1, entity.getFamilyRequestId());
        ps.setShort(2, entity.getSamajId());
        ps.setBoolean(3, entity.isHeadOfFamily());
        AbstractDao.setStringInStatement(ps, 4, entity.getFirstName());
        AbstractDao.setStringInStatement(ps, 5, entity.getFirstNameInHindi());
        AbstractDao.setStringInStatement(ps, 6, entity.getGender().name());
        AbstractDao.setSmallIntInStatement(ps, 7, entity.getBirthDay());
        ps.setShort(8, entity.getBirthMonth().getMonthNumber());
        ps.setShort(9, entity.getBirthYear());
        AbstractDao.setStringInStatement(ps, 10, entity.getMaritalStatus().name());
        AbstractDao.setLocalDateInStatement(ps, 11, entity.getWeddingDate());
        AbstractDao.setStringInStatement(ps, 12, entity.getPhone());
        AbstractDao.setStringInStatement(ps, 13, entity.getEmail());
        ps.setBoolean(14, entity.isAddressSameAsFamily());
        AbstractDao.setStringInStatement(ps, 15, entity.getEducationDetails());
        AbstractDao.setStringInStatement(ps, 16, entity.getOccupation());
        AbstractDao.setTimestampInStatement(ps, 17, entity.getCreatedDate());
        AbstractDao.setStringInStatement(ps, 18, entity.getRelationshipType());
        if(entity.isHeadOfFamily()) {
          ps.setInt(19, -1);
        } else {
          ps.setInt(19, entity.getRelatedMember().getMemberRequestId());
        }
        return ps;
      }
    }, keyHolder);

    int memberId = -1;
    Number key = keyHolder.getKey();
    if (key != null) {
      memberId = key.intValue();
    }
    return memberId;
  }


  public boolean memberExistWithEmailPhone (String email, String phone) {
    if ((email == null || email.isEmpty()) && (phone == null || phone.isEmpty())) {
      return false;
    }

    String sql = MEMBER_EXISTS_WITH_EMAIL_PHONE;
    Integer count;
    if (email != null && !email.isEmpty() && phone != null && !phone.isEmpty()) {
      sql += " and (m.EMAIL = ? OR m.PHONE = ?)";
      count = getJdbcTemplate().queryForObject(sql, Integer.class,
              FamilyRegistrationStatus.PENDING.name(), FamilyRegistrationStatus.PROCESSED.name(),
              email, phone);
    } else if (email != null && !email.isEmpty()) {
      sql += "and m.EMAIL = ?";
      count = getJdbcTemplate().queryForObject(sql, Integer.class,
              FamilyRegistrationStatus.PENDING.name(), FamilyRegistrationStatus.PROCESSED.name(),
              email);
    } else {
      sql += "and m.PHONE = ?";
      count = getJdbcTemplate().queryForObject(sql, Integer.class,
              FamilyRegistrationStatus.PENDING.name(), FamilyRegistrationStatus.PROCESSED.name(),
              phone);
    }
    return count != null && count > 0;
  }


  public void updateFamilyMemberRegistration(FamilyMemberRegistrationEntity entity) {
    getJdbcTemplate().update(UPDATE_FAMILY_MEMBER_REGISTRATION,
        entity.getFirstName(), entity.getFirstNameInHindi(), entity.getGender(),
        entity.getBirthDay(), entity.getBirthMonth(), entity.getBirthYear(),
        entity.getMaritalStatus(), entity.getWeddingDate(), entity.getPhone(),
        entity.getEmail(), entity.isAddressSameAsFamily(), entity.getEducationDetails(),
        entity.getOccupation(), entity.getRelationshipType(), entity.getMemberRequestId());
  }

  public List<FamilyMemberRegistrationEntity> getFamilyMemberRegistrationsByFamilyRequestId(int familyRequestId) {
    return getJdbcTemplate().query(SELECT_FAMILY_MEMBER_REGISTRATION_BY_FAMILY_REQUEST_ID,
        new FamilyMemberRegistrationRowMapper(), familyRequestId);
  }

  public FamilyRegistrationRequestEntity getFamilyRegistrationRequestById(int familyRequestId) {
    List<FamilyRegistrationRequestEntity> results = getJdbcTemplate().query(
        SELECT_FAMILY_REGISTRATION_BY_FAMILY_REQUEST_ID,
        new FamilyRegistrationRequestRowMapper(), familyRequestId);
    return results.isEmpty() ? null : results.get(0);
  }
}