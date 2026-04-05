/*
 * Copyright (c) 2018- 2021
 */

package com.neasaa.familytree.dao.pg;

import com.neasaa.base.app.dao.pg.AbstractDao;
import com.neasaa.familytree.entity.FamilyRegistrationRequestEntity;
import com.neasaa.familytree.enums.FamilyRegistrationStatus;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class FamilyRegistrationRequestRowMapper implements RowMapper<FamilyRegistrationRequestEntity> {

  @Override
  public FamilyRegistrationRequestEntity mapRow(ResultSet aRs, int aRowNum) throws SQLException {
    FamilyRegistrationRequestEntity entity = new FamilyRegistrationRequestEntity();
    entity.setFamilyRequestId(aRs.getInt("FAMILYREQUESTID"));
    entity.setSamajId(aRs.getShort("SAMAJID"));
    entity.setFamilyName(aRs.getString("FAMILYNAME"));
    entity.setFamilyNameInHindi(aRs.getString("FAMILYNAMEINHINDI"));
    entity.setGotra(aRs.getString("GOTRA"));
    entity.setAddressLine1(aRs.getString("ADDRESSLINE1"));
    entity.setAddressLine2(aRs.getString("ADDRESSLINE2"));
    entity.setAddressLine3(aRs.getString("ADDRESSLINE3"));
    entity.setCity(aRs.getString("CITY"));
    entity.setDistrict(aRs.getString("DISTRICT"));
    entity.setState(aRs.getString("STATE"));
    entity.setPostalCode(aRs.getString("POSTALCODE"));
    entity.setCountry(aRs.getString("COUNTRY"));
    entity.setPhone(aRs.getString("PHONE"));
    entity.setEmail(aRs.getString("EMAIL"));
    entity.setCreatedDate(AbstractDao.getTimestampFromResultSet(aRs, "CREATEDDATE"));
    entity.setClientInfo(aRs.getString("CLIENTINFO"));
    String statusStr = aRs.getString("STATUS");
    if (statusStr != null) {
      entity.setStatus(FamilyRegistrationStatus.valueOf(statusStr));
    }
    entity.setFamilyAddedBy(aRs.getInt("FAMILYADDEDBY"));
    entity.setDateAdded(AbstractDao.getTimestampFromResultSet(aRs, "DATEADDED"));
    Integer familyId = aRs.getInt("FAMILYID");
    if (aRs.wasNull()) {
      familyId = null;
    }
    entity.setFamilyId(familyId);
    return entity;
  }
}