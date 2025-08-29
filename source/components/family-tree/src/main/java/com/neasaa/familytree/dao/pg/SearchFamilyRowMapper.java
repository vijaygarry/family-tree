package com.neasaa.familytree.dao.pg;

import com.neasaa.familytree.entity.SearchFamilyEntity;
import com.neasaa.familytree.utils.DataFormatter;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

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
        family.setPhone(DataFormatter.formatPhoneNumber(phone));
        family.setPhoneWhatsappRegistered(aRs.getBoolean("ISPHONEWHATSAPPREGISTERED"));
        family.setFamilyImage(aRs.getString("FAMILYIMAGE"));
        family.setHeadOfFamilyFirstName(aRs.getString("FIRSTNAME"));
        family.setHeadOfFamilyFirstNameInHindi(aRs.getString("FIRSTNAMEINHINDI"));
        return family;
    }
}
