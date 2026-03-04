/*
 * Copyright (c) 2018- 2021
 */

package com.neasaa.familytree.dao.pg;

import static com.neasaa.base.app.constant.AppConstants.UPDATE_OPERATION;

import com.neasaa.base.app.dao.pg.AbstractDao;
import com.neasaa.base.app.operation.AuditInfo;
import com.neasaa.base.app.operation.exception.InternalServerException;
import com.neasaa.familytree.entity.FamilyMemberEntity;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.neasaa.familytree.enums.Gender;
import com.neasaa.familytree.enums.MaritalStatus;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Log4j2
@Repository
public class FamilyMemberDao extends AbstractDao {

  private static final String SELECT_ALL_MEMBERS_FOR_FAMILY =
          "SELECT MEMBERID, FAMILYID, SAMAJID, LOGONNAME, HEADOFFAMILY, FIRSTNAME, FIRSTNAMEINHINDI, LASTNAME, MAIDENLASTNAME, NICKNAME, NICKNAMEINHINDI, "
                  + "GENDER, BIRTHDAY, BIRTHMONTH, BIRTHYEAR, MARITALSTATUS, WEDDINGDATE, DATEOFDEATH, PHONE, ISPHONEVERIFIED, ISPHONEWHATSAPPREGISTERED, "
                  + "EMAIL, ISEMAILVERIFIED, ADDRESSSAMEASFAMILY, MEMBERADDRESSID, EDUCATIONDETAILS, OCCUPATION, HOBBY, MEMBERSEARCHTEXT, PROFILEIMAGE, "
                  + "PROFILEIMAGETHUMBNAIL, IMAGELASTUPDATED, CREATEDBY, CREATEDDATE, LASTUPDATEDBY, LASTUPDATEDDATE "
                  + "FROM "
                  + BASE_SCHEMA_NAME + "FAMILYMEMBER "
                  + "WHERE  SAMAJID = ? and FAMILYID = ?";

  private static final String SELECT_MEMBER_BY_ID =
          "SELECT MEMBERID, FAMILYID, SAMAJID, LOGONNAME, HEADOFFAMILY, FIRSTNAME, FIRSTNAMEINHINDI, LASTNAME, MAIDENLASTNAME, NICKNAME, NICKNAMEINHINDI, "
                  + "GENDER, BIRTHDAY, BIRTHMONTH, BIRTHYEAR, MARITALSTATUS, WEDDINGDATE, DATEOFDEATH, PHONE, ISPHONEVERIFIED, ISPHONEWHATSAPPREGISTERED, "
                  + "EMAIL, ISEMAILVERIFIED, ADDRESSSAMEASFAMILY, MEMBERADDRESSID, EDUCATIONDETAILS, OCCUPATION, HOBBY, MEMBERSEARCHTEXT, PROFILEIMAGE, "
                  + "PROFILEIMAGETHUMBNAIL, IMAGELASTUPDATED, CREATEDBY, CREATEDDATE, LASTUPDATEDBY, LASTUPDATEDDATE "
                  + "FROM "
                  + BASE_SCHEMA_NAME + "FAMILYMEMBER "
                  + "WHERE  SAMAJID = ? and MEMBERID = ?";

  private static final String SELECT_MEMBER_BY_LOGON_NAME =
          "SELECT MEMBERID, FAMILYID, SAMAJID, LOGONNAME, HEADOFFAMILY, FIRSTNAME, FIRSTNAMEINHINDI, LASTNAME, MAIDENLASTNAME, NICKNAME, NICKNAMEINHINDI, "
                  + "GENDER, BIRTHDAY, BIRTHMONTH, BIRTHYEAR, MARITALSTATUS, WEDDINGDATE, DATEOFDEATH, PHONE, ISPHONEVERIFIED, ISPHONEWHATSAPPREGISTERED, "
                  + "EMAIL, ISEMAILVERIFIED, ADDRESSSAMEASFAMILY, MEMBERADDRESSID, EDUCATIONDETAILS, OCCUPATION, HOBBY, MEMBERSEARCHTEXT, PROFILEIMAGE, "
                  + "PROFILEIMAGETHUMBNAIL, IMAGELASTUPDATED, CREATEDBY, CREATEDDATE, LASTUPDATEDBY, LASTUPDATEDDATE "
                  + "FROM "
                  + BASE_SCHEMA_NAME + "FAMILYMEMBER "
                  + "where LOGONNAME = ? ";

  private static final String SELECT_MEMBER_BY_EMAIL_ID =
          "SELECT MEMBERID, FAMILYID, SAMAJID, LOGONNAME, HEADOFFAMILY, FIRSTNAME, FIRSTNAMEINHINDI, LASTNAME, MAIDENLASTNAME, NICKNAME, NICKNAMEINHINDI, "
                  + "GENDER, BIRTHDAY, BIRTHMONTH, BIRTHYEAR, MARITALSTATUS, WEDDINGDATE, DATEOFDEATH, PHONE, ISPHONEVERIFIED, ISPHONEWHATSAPPREGISTERED, "
                  + "EMAIL, ISEMAILVERIFIED, ADDRESSSAMEASFAMILY, MEMBERADDRESSID, EDUCATIONDETAILS, OCCUPATION, HOBBY, MEMBERSEARCHTEXT, PROFILEIMAGE, "
                  + "PROFILEIMAGETHUMBNAIL, IMAGELASTUPDATED, CREATEDBY, CREATEDDATE, LASTUPDATEDBY, LASTUPDATEDDATE "
                  + "FROM "
                  + BASE_SCHEMA_NAME + "FAMILYMEMBER "
                  + "where EMAIL = ? ";

  private static final String SELECT_MEMBER_BY_PHONE =
          "SELECT MEMBERID, FAMILYID, SAMAJID, LOGONNAME, HEADOFFAMILY, FIRSTNAME, FIRSTNAMEINHINDI, LASTNAME, MAIDENLASTNAME, NICKNAME, NICKNAMEINHINDI, "
                  + "GENDER, BIRTHDAY, BIRTHMONTH, BIRTHYEAR, MARITALSTATUS, WEDDINGDATE, DATEOFDEATH, PHONE, ISPHONEVERIFIED, ISPHONEWHATSAPPREGISTERED, "
                  + "EMAIL, ISEMAILVERIFIED, ADDRESSSAMEASFAMILY, MEMBERADDRESSID, EDUCATIONDETAILS, OCCUPATION, HOBBY, MEMBERSEARCHTEXT, PROFILEIMAGE, "
                  + "PROFILEIMAGETHUMBNAIL, IMAGELASTUPDATED, CREATEDBY, CREATEDDATE, LASTUPDATEDBY, LASTUPDATEDDATE "
                  + "FROM "
                  + BASE_SCHEMA_NAME + "FAMILYMEMBER "
                  + "where PHONE = ? ";

  private static final String SELECT_HEAD_OF_FAMILY_BY_FAMILY_ID =
          "SELECT MEMBERID, FAMILYID, SAMAJID, LOGONNAME, HEADOFFAMILY, FIRSTNAME, FIRSTNAMEINHINDI, LASTNAME, MAIDENLASTNAME, NICKNAME, NICKNAMEINHINDI, "
                  + "GENDER, BIRTHDAY, BIRTHMONTH, BIRTHYEAR, MARITALSTATUS, WEDDINGDATE, DATEOFDEATH, PHONE, ISPHONEVERIFIED, ISPHONEWHATSAPPREGISTERED, "
                  + "EMAIL, ISEMAILVERIFIED, ADDRESSSAMEASFAMILY, MEMBERADDRESSID, EDUCATIONDETAILS, OCCUPATION, HOBBY, MEMBERSEARCHTEXT, PROFILEIMAGE, "
                  + "PROFILEIMAGETHUMBNAIL, IMAGELASTUPDATED, CREATEDBY, CREATEDDATE, LASTUPDATEDBY, LASTUPDATEDDATE "
                  + "FROM "
                  + BASE_SCHEMA_NAME + "FAMILYMEMBER "
          + "where SAMAJID = ? and FAMILYID = ? and  HEADOFFAMILY = true";

  private static final String SEARCH_FAMILY_MEMBER =
          "SELECT MEMBERID, FAMILYID, SAMAJID, LOGONNAME, HEADOFFAMILY, FIRSTNAME, FIRSTNAMEINHINDI, LASTNAME, MAIDENLASTNAME, NICKNAME, NICKNAMEINHINDI, "
                  + "GENDER, BIRTHDAY, BIRTHMONTH, BIRTHYEAR, MARITALSTATUS, WEDDINGDATE, DATEOFDEATH, PHONE, ISPHONEVERIFIED, ISPHONEWHATSAPPREGISTERED, "
                  + "EMAIL, ISEMAILVERIFIED, ADDRESSSAMEASFAMILY, MEMBERADDRESSID, EDUCATIONDETAILS, OCCUPATION, HOBBY, MEMBERSEARCHTEXT, PROFILEIMAGE, "
                  + "PROFILEIMAGETHUMBNAIL, IMAGELASTUPDATED, CREATEDBY, CREATEDDATE, LASTUPDATEDBY, LASTUPDATEDDATE "
                  + "FROM "
                  + BASE_SCHEMA_NAME + "FAMILYMEMBER "
                  + "WHERE  SAMAJID = ? and MEMBERSEARCHTEXT ilike ? ";

  private static final String UPDATE_LOGON_NAME_FOR_MEMBER =
      "UPDATE "
          + BASE_SCHEMA_NAME
          + "FAMILYMEMBER "
          + "SET LOGONNAME = ? , ISEMAILVERIFIED = ?, ISPHONEVERIFIED = ?, LASTUPDATEDBY = ? , LASTUPDATEDDATE = ?  "
          + "where MEMBERID = ? ";

  private static final String IS_MEMBER_REGISTERED_BY_EMAIL_ID =
      "SELECT " + " EXISTS (SELECT 1 FROM " + BASE_SCHEMA_NAME + "FAMILYMEMBER WHERE EMAIL = ?)";

  private static final String IS_MEMBER_REGISTERED_BY_PHONE_NUM =
          "SELECT " + " EXISTS (SELECT 1 FROM " + BASE_SCHEMA_NAME + "FAMILYMEMBER WHERE PHONE = ?)";

  private static final String INSERT_FAMILY_MEMBER_STATEMENT =
      "INSERT INTO "
          + BASE_SCHEMA_NAME
          + "FAMILYMEMBER "
          + "(FAMILYID, SAMAJID, LOGONNAME, HEADOFFAMILY, FIRSTNAME, FIRSTNAMEINHINDI, LASTNAME, MAIDENLASTNAME, "
          + "NICKNAME, NICKNAMEINHINDI, GENDER, BIRTHDAY, BIRTHMONTH, BIRTHYEAR, MARITALSTATUS, WEDDINGDATE, DATEOFDEATH, "
          + "PHONE, ISPHONEVERIFIED, ISPHONEWHATSAPPREGISTERED, EMAIL, ISEMAILVERIFIED, ADDRESSSAMEASFAMILY, MEMBERADDRESSID, "
          + "EDUCATIONDETAILS, OCCUPATION, HOBBY, MEMBERSEARCHTEXT, PROFILEIMAGE, PROFILEIMAGETHUMBNAIL, "
          + "IMAGELASTUPDATED, CREATEDBY, CREATEDDATE, LASTUPDATEDBY, LASTUPDATEDDATE) "
          + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

  private static final String UPDATE_FAMILY_MEMBER_STATEMENT =
      "UPDATE "
          + BASE_SCHEMA_NAME
          + "FAMILYMEMBER "
          + "SET HEADOFFAMILY = ? , FIRSTNAME = ? , FIRSTNAMEINHINDI = ? , LASTNAME = ?, MAIDENLASTNAME = ?, "
          + "NICKNAME = ?, NICKNAMEINHINDI = ?, GENDER = ?, BIRTHDAY = ?, BIRTHMONTH = ?, BIRTHYEAR = ?, MARITALSTATUS = ?, WEDDINGDATE = ?, DATEOFDEATH = ?,"
          + "PHONE = ?, ISPHONEVERIFIED = ?, ISPHONEWHATSAPPREGISTERED = ?, EMAIL = ?, ISEMAILVERIFIED = ?, ADDRESSSAMEASFAMILY = ? , MEMBERADDRESSID = ?, "
          + "EDUCATIONDETAILS = ? , OCCUPATION = ? , HOBBY = ? , MEMBERSEARCHTEXT = ?, "
          + "LASTUPDATEDBY = ? , LASTUPDATEDDATE = ?  "
          + "where SAMAJID = ? and FAMILYID = ? and MEMBERID = ? ";

  private static final String UPDATE_FAMILY_MEMBER_IMAGE_STATEMENT =
      "UPDATE "
          + BASE_SCHEMA_NAME
          + "FAMILYMEMBER "
          + "SET PROFILEIMAGE = ?, PROFILEIMAGETHUMBNAIL = ? , IMAGELASTUPDATED = ? ,  LASTUPDATEDBY = ? , LASTUPDATEDDATE = ?  "
              + "where SAMAJID = ? and FAMILYID = ? and MEMBERID = ? ";

  private static final String FAMILY_MEMBER_HISTORY_INSERT_STATEMENT =
      "INSERT INTO "
          + BASE_SCHEMA_NAME
          + "familymemberhistory "
          + "(operation, memberid, familyid, samajid, logonname, headoffamily, firstname, firstnameinhindi, "
          + "lastname, maidenlastname, nickname, nicknameinhindi, gender, birthday, birthmonth, birthyear, "
          + "maritalstatus, weddingdate, dateofdeath, phone, ISPHONEVERIFIED, isphonewhatsappregistered, email, ISEMAILVERIFIED, "
          + "addresssameasfamily, memberaddressid, educationdetails, occupation, hobby, membersearchtext, "
          + "profileimage, profileimagethumbnail, imagelastupdated, createdby, createddate, "
          + "lastupdatedby, lastupdateddate) "
          + "SELECT ?, memberid, familyid, samajid, logonname, headoffamily, firstname, firstnameinhindi, "
          + "lastname, maidenlastname, nickname, nicknameinhindi, gender, birthday, birthmonth, birthyear, "
          + "maritalstatus, weddingdate, dateofdeath, phone, ISPHONEVERIFIED, isphonewhatsappregistered, email, ISEMAILVERIFIED, "
          + "addresssameasfamily, memberaddressid, educationdetails, occupation, hobby, membersearchtext, "
          + "profileimage, profileimagethumbnail, imagelastupdated, createdby, createddate, "
          + "lastupdatedby, lastupdateddate "
          + "FROM "
          + BASE_SCHEMA_NAME
          + "familymember WHERE memberid = ?";

  public List<FamilyMemberEntity> allMembersForFamily(int samajId, int familyId) {
    return getJdbcTemplate()
        .query(SELECT_ALL_MEMBERS_FOR_FAMILY, new FamilyMemberRowMapper(), samajId, familyId);
  }

  public FamilyMemberEntity getMemberById(int samajId, int memberId) {
    List<FamilyMemberEntity> memberList =
        getJdbcTemplate().query(SELECT_MEMBER_BY_ID, new FamilyMemberRowMapper(), samajId, memberId);

    if (memberList.isEmpty()) {
      return null;
    }
    if (memberList.size() > 1) {
      throw new RuntimeException("Invalid member id entry");
    }
    return memberList.get(0);
  }

  public List<FamilyMemberEntity> searchFamilyMember (
          int samajId, String searchString, Gender gender,
          MaritalStatus maritalStatus, Integer ageFrom, Integer ageTo) {
    StringBuilder searchQuery = new StringBuilder(SEARCH_FAMILY_MEMBER);
    List<Object> params = new ArrayList<>();
    params.add(samajId);
    params.add("%" + searchString + "%");
    if (gender != null) {
      searchQuery.append(" AND GENDER = ? ");
      params.add(gender.name());
    }
    if (maritalStatus != null) {
      searchQuery.append(" AND MARITALSTATUS = ? ");
      params.add(maritalStatus.name());
    }
    if(ageFrom != null && ageTo != null) {
      searchQuery.append(""" 
              AND
              make_date(birthyear, birthmonth, CASE WHEN birthday = -1 THEN 1 ELSE birthday END)
                  BETWEEN ? AND ?
              """);
        // Setting local date to first day of the month to accommodate missing birthday.
        LocalDate localStartDate = LocalDate.now().withDayOfMonth(1).minusYears(ageTo);
        java.sql.Date startDate = java.sql.Date.valueOf(localStartDate);
        java.sql.Date endDate = java.sql.Date.valueOf(LocalDate.now().minusYears(ageFrom));
        log.info("Using start date: {} and end date: {} for age range: {} - {}", startDate, endDate, ageFrom, ageTo);
        params.add(startDate);
        params.add(endDate);
    } else if (ageFrom != null) {
      searchQuery.append("""
              AND
              make_date(birthyear, birthmonth, CASE WHEN birthday = -1 THEN 1 ELSE birthday END)
                  <= ?
              """);
        params.add(java.sql.Date.valueOf(LocalDate.now().minusYears(ageFrom)));
    } else if (ageTo != null) {
      searchQuery.append("""
              AND
              make_date(birthyear, birthmonth, CASE WHEN birthday = -1 THEN 1 ELSE birthday END)
                  >= ?
              """);
      // Setting local date to first day of the month to accommodate missing birthday.
      LocalDate localStartDate = LocalDate.now().withDayOfMonth(1).minusYears(ageTo);
      params.add(java.sql.Date.valueOf(localStartDate));
    }

    return getJdbcTemplate()
            .query(searchQuery.toString(), new FamilyMemberRowMapper(), params.toArray());
  }

  public FamilyMemberEntity getHeadOfFamilyByFamilyId(int samajId, int familyId) {
    List<FamilyMemberEntity> memberList =
        getJdbcTemplate()
            .query(SELECT_HEAD_OF_FAMILY_BY_FAMILY_ID, new FamilyMemberRowMapper(), samajId, familyId);

    if (memberList.isEmpty()) {
      return null;
    }
    if (memberList.size() > 1) {
      throw new RuntimeException("Invalid member id entry");
    }
    return memberList.get(0);
  }

  public FamilyMemberEntity getMemberByLogonName(String logonName) {
    List<FamilyMemberEntity> memberList =
        getJdbcTemplate()
            .query(SELECT_MEMBER_BY_LOGON_NAME, new FamilyMemberRowMapper(), logonName);

    if (memberList.isEmpty()) {
      throw new RuntimeException("No member found with logon name " + logonName);
    }
    if (memberList.size() > 1) {
      throw new RuntimeException(
          "Logon name is not unique, multiple members found with same logon name");
    }
    return memberList.get(0);
  }

  public FamilyMemberEntity getMemberByEmail(String emailId) {
    List<FamilyMemberEntity> memberList =
        getJdbcTemplate().query(SELECT_MEMBER_BY_EMAIL_ID, new FamilyMemberRowMapper(), emailId);

    if (memberList.isEmpty()) {
      return null;
    }
    if (memberList.size() > 1) {
      throw new RuntimeException(
          "Email Id is not unique, multiple members found with same email Id");
    }
    return memberList.get(0);
  }

  public FamilyMemberEntity getMemberByPhone(String phone) {
    List<FamilyMemberEntity> memberList =
            getJdbcTemplate().query(SELECT_MEMBER_BY_PHONE, new FamilyMemberRowMapper(), phone);

    if (memberList.isEmpty()) {
      return null;
    }
    if (memberList.size() > 1) {
      throw new RuntimeException(
              "Phone number is not unique, multiple members found with same phone");
    }
    return memberList.get(0);
  }


  public void updateMemberLogonName (
      String logonName, boolean emailVerified, boolean phoneVerified, int updatedBy, Date lastUpdatedDate, int memberId) {
    try {
      getJdbcTemplate().update(new PreparedStatementCreator() {
        @Override
        public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
          PreparedStatement prepareStatement = aCon.prepareStatement(UPDATE_LOGON_NAME_FOR_MEMBER);
          setStringInStatement(prepareStatement, 1, logonName);
          setBooleanInStatement(prepareStatement, 2, emailVerified);
          setBooleanInStatement(prepareStatement, 3, phoneVerified);
          setIntInStatement(prepareStatement, 4, updatedBy);
          setTimestampInStatement(prepareStatement, 5, lastUpdatedDate);
          setIntInStatement(prepareStatement, 6, memberId);
          return prepareStatement;
        }
      });
    } catch (Exception e) {
      throw new InternalServerException(
          "Internal error while processing your request, please try again.", e);
    }
  }

  public boolean isMemberExistsForEmail(String emailId) {
    try {
      return Boolean.TRUE.equals(
          jdbcTemplate.queryForObject(IS_MEMBER_REGISTERED_BY_EMAIL_ID, Boolean.class, emailId));
    } catch (Exception e) {
      throw new InternalServerException(
          "Internal error while processing your request, please try again.", e);
    }
  }

  public boolean isMemberExistsForPhone(String phone) {
    try {
      return Boolean.TRUE.equals(
              jdbcTemplate.queryForObject(IS_MEMBER_REGISTERED_BY_PHONE_NUM, Boolean.class, phone));
    } catch (Exception e) {
      throw new InternalServerException(
              "Internal error while processing your request, please try again.", e);
    }
  }

  public void updateMemberImagePath(int samajId, int familyId, int memberId, String memberImagePath, AuditInfo auditInfo) {
    getJdbcTemplate()
        .update(
            UPDATE_FAMILY_MEMBER_IMAGE_STATEMENT,
            memberImagePath,
            memberImagePath,
            auditInfo.getLastUpdatedDate(),
            auditInfo.getLastUpdatedBy(),
            auditInfo.getLastUpdatedDate(),
                samajId, familyId,
            memberId);
    log.info("Member image path is updated for member id: {}", memberId);
  }

  public FamilyMemberEntity addFamilyMember(FamilyMemberEntity aFamilyMember) {
    KeyHolder keyHolder = new GeneratedKeyHolder();
    getJdbcTemplate()
        .update(
            new PreparedStatementCreator() {
              @Override
              public PreparedStatement createPreparedStatement(Connection aCon)
                  throws SQLException {
                return buildInsertStatement(aCon, aFamilyMember);
              }
            },
            keyHolder);

    int memberId = -1;
    Number key = keyHolder.getKey();
    if (key != null) {
      memberId = key.intValue();
    }
    log.info("New family member added with id {}", memberId);
    return getMemberById(aFamilyMember.getSamajId(), memberId);
  }

  private void addFamilyMemberHistoryRecord(int memberId, String operation) {

    getJdbcTemplate()
        .update(
            new PreparedStatementCreator() {
              @Override
              public PreparedStatement createPreparedStatement(Connection aCon)
                  throws SQLException {
                PreparedStatement prepareStatement =
                    aCon.prepareStatement(FAMILY_MEMBER_HISTORY_INSERT_STATEMENT);
                int columnIndex = 1;
                setStringInStatement(prepareStatement, 1, operation);
                setIntInStatement(prepareStatement, 2, memberId);
                return prepareStatement;
              }
            });
    log.info(
        "New family member history record added for member id {} for operation {}",
        memberId,
        operation);
  }

  private PreparedStatement buildInsertStatement(
      Connection aConection, FamilyMemberEntity aFamilyMember) throws SQLException {

    PreparedStatement prepareStatement =
        aConection.prepareStatement(INSERT_FAMILY_MEMBER_STATEMENT, new String[] {"memberid"});
    int columnIndex = 1;
    setIntInStatement(prepareStatement, columnIndex++, aFamilyMember.getFamilyId());
    setIntInStatement(prepareStatement, columnIndex++, aFamilyMember.getSamajId());
    setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getLogonName());
    setBooleanInStatement(prepareStatement, columnIndex++, aFamilyMember.isHeadOfFamily());
    setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getFirstName());
    setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getFirstNameInHindi());
    setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getLastName());
    setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getMaidenLastName());
    setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getNickName());
    setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getNickNameInHindi());

    setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getGender().name());
    setSmallIntInStatement(prepareStatement, columnIndex++, aFamilyMember.getBirthDay());

    setSmallIntInStatement(
        prepareStatement, columnIndex++, aFamilyMember.getBirthMonth().getMonthNumber());
    setSmallIntInStatement(prepareStatement, columnIndex++, aFamilyMember.getBirthYear());
    setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getMaritalStatus().name());
    setLocalDateInStatement(prepareStatement, columnIndex++, aFamilyMember.getWeddingDate());
    setLocalDateInStatement(prepareStatement, columnIndex++, aFamilyMember.getDateOfDeath());

    setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getPhone());
    setBooleanInStatement(prepareStatement, columnIndex++, aFamilyMember.isPhoneVerified());
    setBooleanInStatement(
        prepareStatement, columnIndex++, aFamilyMember.isPhoneWhatsappRegistered());
    if (aFamilyMember.getEmail() != null) {
      setStringInStatement(
          prepareStatement, columnIndex++, aFamilyMember.getEmail().toLowerCase().trim());
    } else {
      setStringInStatement(prepareStatement, columnIndex++, null);
    }
    setBooleanInStatement(prepareStatement, columnIndex++, aFamilyMember.isEmailVerified());

    setBooleanInStatement(prepareStatement, columnIndex++, aFamilyMember.isAddressSameAsFamily());
    setIntInStatement(prepareStatement, columnIndex++, aFamilyMember.getMemberAddressId());

    setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getEducationDetails());
    setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getOccupation());
    setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getHobby());
    setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getMemberSearchText());
    setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getProfileImage());
    setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getProfileImageThumbnail());
    setTimestampInStatement(prepareStatement, columnIndex++, aFamilyMember.getImageLastUpdated());
    setIntInStatement(prepareStatement, columnIndex++, aFamilyMember.getCreatedBy());
    setTimestampInStatement(prepareStatement, columnIndex++, aFamilyMember.getCreatedDate());
    setIntInStatement(prepareStatement, columnIndex++, aFamilyMember.getLastUpdatedBy());
    setTimestampInStatement(prepareStatement, columnIndex++, aFamilyMember.getLastUpdatedDate());
    return prepareStatement;
  }

  private PreparedStatement buildUpdateStatement(
      Connection aConection, FamilyMemberEntity aFamilyMember, AuditInfo auditInfo)
      throws SQLException {

    PreparedStatement prepareStatement =
        aConection.prepareStatement(UPDATE_FAMILY_MEMBER_STATEMENT);

    int columnIndex = 1;
    setBooleanInStatement(prepareStatement, columnIndex++, aFamilyMember.isHeadOfFamily());
    setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getFirstName());
    setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getFirstNameInHindi());
    setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getLastName());
    setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getMaidenLastName());
    setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getNickName());
    setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getNickNameInHindi());

    setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getGender().name());
    setSmallIntInStatement(prepareStatement, columnIndex++, aFamilyMember.getBirthDay());

    setSmallIntInStatement(
        prepareStatement, columnIndex++, aFamilyMember.getBirthMonth().getMonthNumber());
    setSmallIntInStatement(prepareStatement, columnIndex++, aFamilyMember.getBirthYear());
    setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getMaritalStatus().name());
    setLocalDateInStatement(prepareStatement, columnIndex++, aFamilyMember.getWeddingDate());
    setLocalDateInStatement(prepareStatement, columnIndex++, aFamilyMember.getDateOfDeath());

    setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getPhone());
    setBooleanInStatement(prepareStatement, columnIndex++, aFamilyMember.isPhoneVerified());
    setBooleanInStatement(
        prepareStatement, columnIndex++, aFamilyMember.isPhoneWhatsappRegistered());
    setStringInStatement(
          prepareStatement, columnIndex++, aFamilyMember.getEmail());

    setBooleanInStatement(prepareStatement, columnIndex++, aFamilyMember.isEmailVerified());

    setBooleanInStatement(prepareStatement, columnIndex++, aFamilyMember.isAddressSameAsFamily());
    setIntInStatement(prepareStatement, columnIndex++, aFamilyMember.getMemberAddressId());

    setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getEducationDetails());
    setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getOccupation());
    setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getHobby());
    setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getMemberSearchText());
    setIntInStatement(prepareStatement, columnIndex++, auditInfo.getLastUpdatedBy());
    setTimestampInStatement(prepareStatement, columnIndex++, auditInfo.getLastUpdatedDate());
    setIntInStatement(prepareStatement, columnIndex++, aFamilyMember.getSamajId());
    setIntInStatement(prepareStatement, columnIndex++, aFamilyMember.getFamilyId());
    setIntInStatement(prepareStatement, columnIndex++, aFamilyMember.getMemberId());
    log.info("Update prepared statement {}", prepareStatement);
    return prepareStatement;
  }

  public int updateFamilyMember(FamilyMemberEntity aFamilyMember, AuditInfo auditInfo) {
    try {
      addFamilyMemberHistoryRecord(aFamilyMember.getMemberId(), UPDATE_OPERATION);
      return getJdbcTemplate()
          .update(
              new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection aCon)
                    throws SQLException {
                  return buildUpdateStatement(aCon, aFamilyMember, auditInfo);
                }
              });
    } catch (Exception e) {
      log.error(
          "Error while updating family member for member id: {}", aFamilyMember.getMemberId(), e);
      throw new InternalServerException(
          "Error while updating member profile. Please try again later");
    }
  }
}
