/*
 * Copyright (c) 2018- 2021
 */

package com.neasaa.familytree.dao.pg;

import static com.neasaa.base.app.constant.AppConstants.UPDATE_OPERATION;

import com.neasaa.base.app.dao.pg.AbstractDao;
import com.neasaa.base.app.operation.AuditInfo;
import com.neasaa.base.app.operation.exception.InternalServerException;
import com.neasaa.familytree.entity.FamilyEntity;
import com.neasaa.familytree.entity.FamilyMemberEntity;
import com.neasaa.familytree.entity.SearchFamilyEntity;
import com.neasaa.familytree.utils.DataFormatter;
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
public class FamilyDao extends AbstractDao {

  private static final String SELECT_FAMILY_BY_FAMILYID =
      "SELECT f.familyid, f.samajid, f.familyname, f.familynameinhindi, f.gotra, f.addressid, "
          + "f.region, f.phone, f.isphonewhatsappregistered, f.email, f.familysearchtext, "
          + "f.active, f.familyimage, f.imagelastupdated, "
          + "a.addressline1, a.addressline2, a.addressline3, a.city, a.district, a.state, a.postalcode, a.country "
          + "FROM "
          + BASE_SCHEMA_NAME
          + "FAMILY f "
          + "LEFT JOIN "
          + BASE_SCHEMA_NAME
          + "ADDRESS a on f.addressid = a.addressid "
          + "WHERE f.familyid = ? and f.samajid = ? and f.active = true";

  private static final String SEARCH_FAMILY =
      "SELECT f.familyid, f.samajid, f.familyname, f.familynameinhindi, f.gotra, "
          + "f.region, f.phone, f.isphonewhatsappregistered, f.familyimage, "
          + "m.FIRSTNAME, m. FIRSTNAMEINHINDI "
          + "FROM "
          + BASE_SCHEMA_NAME
          + "FAMILY f "
          + "LEFT JOIN "
          + BASE_SCHEMA_NAME
          + "FAMILYMEMBER m on f.familyid = m.familyid and m.HEADOFFAMILY = true "
          + "WHERE f.active = true and f.samajid = ? and f.familysearchtext ilike ? ";

  private static final String UPDATE_FAMILY_DISPLAY_NAME =
      "UPDATE "
          + BASE_SCHEMA_NAME
          + "FAMILY "
          + "SET region = ?, familysearchtext = ?, LASTUPDATEDBY = ?, lastupdateddate = ? WHERE FAMILYID = ? AND SAMAJID = ?";

  private static final String UPDATE_FAMILY_IMAGE_PATH =
      "UPDATE "
          + BASE_SCHEMA_NAME
          + "FAMILY "
          + "SET familyimage = ?, imagelastupdated = ?, LASTUPDATEDBY = ?, lastupdateddate = ? WHERE FAMILYID = ? AND SAMAJID = ?";

  private static final String FAMILY_INSERT_STATEMENT =
      "INSERT INTO "
          + BASE_SCHEMA_NAME
          + "FAMILY "
          + "(SAMAJID, FAMILYNAME, FAMILYNAMEINHINDI, GOTRA, ADDRESSID, REGION, PHONE, ISPHONEWHATSAPPREGISTERED, EMAIL, FAMILYSEARCHTEXT, "
          + "ACTIVE, FAMILYIMAGE, IMAGELASTUPDATED, CREATEDBY, CREATEDDATE, LASTUPDATEDBY, LASTUPDATEDDATE) "
          + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

  // Family image and image update date is not updated here
  private static final String UPDATE_FAMILY_BY_ID_STATEMENT =
      "UPDATE "
          + BASE_SCHEMA_NAME
          + "FAMILY "
          + "SET FAMILYNAME = ? , FAMILYNAMEINHINDI = ? , GOTRA = ? , ADDRESSID = ? , REGION = ? , "
          + "PHONE = ? , ISPHONEWHATSAPPREGISTERED = ? , EMAIL = ? , FAMILYSEARCHTEXT = ? , "
          + "ACTIVE = ? , LASTUPDATEDBY = ? , LASTUPDATEDDATE = ?  "
          + "where FAMILYID = ? AND SAMAJID = ?";

  private static final String FAMILY_HISTORY_INSERT_STATEMENT =
      "INSERT INTO "
          + BASE_SCHEMA_NAME
          + "familyhistory "
          + "(operation, familyid, samajid, familyname, familynameinhindi, gotra, addressid, region, phone, "
          + " isphonewhatsappregistered, email, familysearchtext, active, familyimage, imagelastupdated, "
          + " createdby, createddate, lastupdatedby, lastupdateddate) "
          + " SELECT ?, familyid, samajid, familyname, familynameinhindi, gotra, addressid, region, phone, "
          + " isphonewhatsappregistered, email, familysearchtext, active, familyimage, imagelastupdated, "
          + " createdby, createddate, lastupdatedby, lastupdateddate"
          + " FROM "
          + BASE_SCHEMA_NAME
          + "family WHERE familyid = ?";

	private static final String UPDATE_FAMILY_MEMBERS_LAST_NAME_BY_FAMILYID = "UPDATE "  + BASE_SCHEMA_NAME + "FAMILYMEMBER " +
			"SET LASTNAME = ? where FAMILYID = ?  AND SAMAJID = ?";


  public FamilyEntity getFamilyByFamilyId(int samajId, int familyId) {
    List<FamilyEntity> familyList =
        getJdbcTemplate().query(SELECT_FAMILY_BY_FAMILYID, new FamilyRowMapper(), familyId, samajId);

    if (familyList.isEmpty()) {
      return null;
    }
    if (familyList.size() > 1) {
      throw new RuntimeException("Invalid family id entry");
    }
    return familyList.get(0);
  }

  public List<SearchFamilyEntity> searchFamily(int samajId, String searchString) {
    return getJdbcTemplate()
        .query(SEARCH_FAMILY, new SearchFamilyRowMapper(), samajId, "%" + searchString + "%");
  }

  public void updateFamilyDisplayName(
      FamilyEntity family, FamilyMemberEntity headOfFamily, AuditInfo auditInfo) {
    String familyRegion = DataFormatter.getRegion(family.getAddress());
    String searchString =
        DataFormatter.getFamilySearchString(family, headOfFamily, family.getAddress());
    log.info(
        "Updating family search text to '{}' and region to '{}' for family id: {}",
        searchString,
        familyRegion,
        family.getFamilyId());
    getJdbcTemplate()
        .update(
            UPDATE_FAMILY_DISPLAY_NAME,
            familyRegion,
            searchString,
            auditInfo.getLastUpdatedBy(),
            auditInfo.getLastUpdatedDate(),
            family.getFamilyId(), family.getSamajId());
    log.info("Family region and search text is updated for family id: {}", family.getFamilyId());
  }

  public void updateFamilyImagePath(int samajId, int familyId, String familyImagePath, AuditInfo auditInfo) {
    getJdbcTemplate()
        .update(
            UPDATE_FAMILY_IMAGE_PATH,
            familyImagePath,
            auditInfo.getLastUpdatedDate(),
            auditInfo.getLastUpdatedBy(),
            auditInfo.getLastUpdatedDate(),
            familyId, samajId);
    log.info("Family image path is updated for family id: {}", familyId);
  }

  public int addFamily(FamilyEntity aFamily) {
    KeyHolder keyHolder = new GeneratedKeyHolder();
    getJdbcTemplate()
        .update(
            new PreparedStatementCreator() {
              @Override
              public PreparedStatement createPreparedStatement(Connection aCon)
                  throws SQLException {
                return buildInsertStatement(aCon, aFamily);
              }
            },
            keyHolder);
    int familyId = -1;
    Number key = keyHolder.getKey();
    if (key != null) {
      familyId = key.intValue();
    }
    log.info("New family added with id {}", familyId);
    return familyId;
  }

  private PreparedStatement buildInsertStatement(Connection aConection, FamilyEntity aFamily)
      throws SQLException {
    int colIndex = 1;
    PreparedStatement prepareStatement =
        aConection.prepareStatement(FAMILY_INSERT_STATEMENT, new String[] {"familyid"});
    setIntInStatement(prepareStatement, colIndex++, aFamily.getSamajId());
    setStringInStatement(prepareStatement, colIndex++, aFamily.getFamilyName());
    setStringInStatement(prepareStatement, colIndex++, aFamily.getFamilyNameInHindi());
    setStringInStatement(prepareStatement, colIndex++, aFamily.getGotra());
    setIntInStatement(prepareStatement, colIndex++, aFamily.getAddressId());
    setStringInStatement(prepareStatement, colIndex++, aFamily.getRegion());
    setStringInStatement(prepareStatement, colIndex++, aFamily.getPhone());
    setBooleanInStatement(prepareStatement, colIndex++, aFamily.isPhoneWhatsappRegistered());
    setStringInStatement(prepareStatement, colIndex++, aFamily.getEmail());
    setStringInStatement(prepareStatement, colIndex++, aFamily.getFamilysearchtext());
    setBooleanInStatement(prepareStatement, colIndex++, aFamily.isActive());
    setStringInStatement(prepareStatement, colIndex++, aFamily.getFamilyImage());
    setTimestampInStatement(prepareStatement, colIndex++, aFamily.getImageLastUpdated());
    setIntInStatement(prepareStatement, colIndex++, aFamily.getCreatedBy());
    setTimestampInStatement(prepareStatement, colIndex++, aFamily.getCreatedDate());
    setIntInStatement(prepareStatement, colIndex++, aFamily.getLastUpdatedBy());
    setTimestampInStatement(prepareStatement, colIndex++, aFamily.getLastUpdatedDate());
    return prepareStatement;
  }

    public int updateFamily(FamilyEntity aFamily, AuditInfo auditInfo, boolean isFamilyNameUpdated) {
        try {
            getJdbcTemplate()
                    .update(FAMILY_HISTORY_INSERT_STATEMENT, UPDATE_OPERATION, aFamily.getFamilyId());
            int rowsUpdated = getJdbcTemplate()
                    .update(
                            new PreparedStatementCreator() {
                                @Override
                                public PreparedStatement createPreparedStatement(Connection aCon)
                                        throws SQLException {
                                    return buildUpdateStatement(aCon, aFamily, auditInfo);
                                }
                            });
            //Update LastName in family members table
            if(isFamilyNameUpdated) {
                getJdbcTemplate().update(UPDATE_FAMILY_MEMBERS_LAST_NAME_BY_FAMILYID, aFamily.getFamilyName(), aFamily.getFamilyId(), aFamily.getSamajId());
            }
            return rowsUpdated;
        } catch (Exception e) {
            log.error("Error while updating family details for family id: {}", aFamily.getFamilyId(), e);
            throw new InternalServerException(
                    "Error while updating family details. Please try again later");
        }
    }

  public PreparedStatement buildUpdateStatement(
      Connection aConection, FamilyEntity aFamily, AuditInfo auditInfo) throws SQLException {
    PreparedStatement prepareStatement = aConection.prepareStatement(UPDATE_FAMILY_BY_ID_STATEMENT);
    setStringInStatement(prepareStatement, 1, aFamily.getFamilyName());
    setStringInStatement(prepareStatement, 2, aFamily.getFamilyNameInHindi());
    setStringInStatement(prepareStatement, 3, aFamily.getGotra());
    setIntInStatement(prepareStatement, 4, aFamily.getAddressId());
    setStringInStatement(prepareStatement, 5, aFamily.getRegion());
    setStringInStatement(prepareStatement, 6, aFamily.getPhone());
    setBooleanInStatement(prepareStatement, 7, aFamily.isPhoneWhatsappRegistered());
    setStringInStatement(prepareStatement, 8, aFamily.getEmail());
    setStringInStatement(prepareStatement, 9, aFamily.getFamilysearchtext());
    setBooleanInStatement(prepareStatement, 10, aFamily.isActive());
    setIntInStatement(prepareStatement, 11, auditInfo.getLastUpdatedBy());
    setTimestampInStatement(prepareStatement, 12, auditInfo.getLastUpdatedDate());
    setIntInStatement(prepareStatement, 13, aFamily.getFamilyId());
    setIntInStatement(prepareStatement, 14, aFamily.getSamajId());
    return prepareStatement;
  }

}
