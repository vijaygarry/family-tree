/*
 * Copyright (c) 2018- 2021
 */

package com.neasaa.familytree.dao.pg;

import com.neasaa.base.app.dao.pg.AbstractDao;
import com.neasaa.familytree.entity.FamilyMemberRegistrationEntity;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.neasaa.familytree.enums.Gender;
import com.neasaa.familytree.enums.MaritalStatus;
import com.neasaa.familytree.enums.Month;
import org.springframework.jdbc.core.RowMapper;

public class FamilyMemberRegistrationRowMapper implements RowMapper<FamilyMemberRegistrationEntity> {


  @Override
  public FamilyMemberRegistrationEntity mapRow(ResultSet aRs, int aRowNum) throws SQLException {
    FamilyMemberRegistrationEntity entity = new FamilyMemberRegistrationEntity();
    entity.setMemberRequestId(aRs.getInt("MEMBERREQUESTID"));
    entity.setFamilyRequestId(aRs.getInt("FAMILYREQUESTID"));
    entity.setSamajId(aRs.getShort("SAMAJID"));
    entity.setHeadOfFamily(aRs.getBoolean("HEADOFFAMILY"));
    entity.setFirstName(aRs.getString("FIRSTNAME"));
    entity.setFirstNameInHindi(aRs.getString("FIRSTNAMEINHINDI"));
    entity.setGender(Gender.getGenderByString(aRs.getString("GENDER")));
    Short birthDay = aRs.getShort("BIRTHDAY");
    if (aRs.wasNull()) {
      birthDay = null;
    }
    entity.setBirthDay(birthDay);
    entity.setBirthMonth(Month.fromNumber(aRs.getShort("BIRTHMONTH")));
    entity.setBirthYear(aRs.getShort("BIRTHYEAR"));
    entity.setMaritalStatus(MaritalStatus.getMaritalStatus(aRs.getString("MARITALSTATUS")));
    entity.setWeddingDate(AbstractDao.getLocalDateFromResultSet(aRs, "WEDDINGDATE"));
    entity.setPhone(aRs.getString("PHONE"));
    entity.setEmail(aRs.getString("EMAIL"));
    entity.setAddressSameAsFamily(aRs.getBoolean("ADDRESSSAMEASFAMILY"));
    entity.setEducationDetails(aRs.getString("EDUCATIONDETAILS"));
    entity.setOccupation(aRs.getString("OCCUPATION"));
    entity.setCreatedDate(AbstractDao.getTimestampFromResultSet(aRs, "CREATEDDATE"));
    Integer memberId = aRs.getInt("MEMBERID");
    if (aRs.wasNull()) {
      memberId = null;
    }
    entity.setMemberId(memberId);
    entity.setRelationshipType(aRs.getString("RELATIONSHIPTYPE"));
    entity.setRelatedMemberId(aRs.getInt("RELATEDMEMBERID"));
    return entity;
  }
}