package com.neasaa.familytree.dao.pg;

import com.neasaa.familytree.entity.SearchFamilyEntity;
import com.neasaa.familytree.utils.DataFormatter;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SearchFamilyRowMapper implements RowMapper<SearchFamilyEntity> {

  @Override
  public SearchFamilyEntity mapRow(ResultSet aRs, int aRowNum) throws SQLException {
    SearchFamilyEntity family = new SearchFamilyEntity();
    family.setFamilyId(aRs.getInt("FAMILYID"));
    family.setFamilyName(aRs.getString("FAMILYNAME"));
    family.setFamilyNameInHindi(aRs.getString("FAMILYNAMEINHINDI"));
    family.setGotra(aRs.getString("GOTRA"));
    family.setRegion(aRs.getString("REGION"));
    String phone = aRs.getString("PHONE");
    family.setPhone(DataFormatter.formatPhoneNumberForUX(phone));
    family.setPhoneWhatsappRegistered(aRs.getBoolean("ISPHONEWHATSAPPREGISTERED"));
    family.setFamilyImage(aRs.getString("FAMILYIMAGE"));
    String firstName = aRs.getString("FIRSTNAME");
    if (firstName == null || firstName.trim().isEmpty()) {
      firstName = "No Head Of Family";
    }
    family.setHeadOfFamilyFirstName(firstName);
    family.setHeadOfFamilyFirstNameInHindi(aRs.getString("FIRSTNAMEINHINDI"));
    return family;
  }
}
