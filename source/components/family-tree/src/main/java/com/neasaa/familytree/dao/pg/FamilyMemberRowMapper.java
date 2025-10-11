/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.familytree.dao.pg;

import java.sql.SQLException;

import com.neasaa.base.app.dao.pg.AbstractDao;
import com.neasaa.familytree.entity.FamilyMemberEntity;
import com.neasaa.familytree.enums.MaritalStatus;
import com.neasaa.familytree.enums.Gender;

import java.sql.ResultSet;

import com.neasaa.familytree.enums.Month;
import com.neasaa.familytree.utils.DataFormatter;
import org.springframework.jdbc.core.RowMapper;

public class FamilyMemberRowMapper implements RowMapper<FamilyMemberEntity> {

	@Override
	public FamilyMemberEntity mapRow(ResultSet aRs, int aRowNum) throws SQLException {
		FamilyMemberEntity familyMember = new FamilyMemberEntity();
		familyMember.setMemberId(aRs.getInt("MEMBERID"));
		familyMember.setFamilyId(aRs.getInt("FAMILYID"));
		familyMember.setLogonName(aRs.getString("LOGONNAME"));
		familyMember.setHeadOfFamily(aRs.getBoolean("HEADOFFAMILY"));
		familyMember.setFirstName(aRs.getString("FIRSTNAME"));
		familyMember.setFirstNameInHindi(aRs.getString("FIRSTNAMEINHINDI"));
		familyMember.setLastName(aRs.getString("LASTNAME"));
		familyMember.setMaidenLastName(aRs.getString("MAIDENLASTNAME"));
		familyMember.setNickName(aRs.getString("NICKNAME"));
		familyMember.setNickNameInHindi(aRs.getString("NICKNAMEINHINDI"));
		familyMember.setGender(Gender.getGenderByString(aRs.getString("GENDER")));
		familyMember.setBirthDay(aRs.getShort("BIRTHDAY"));
		familyMember.setBirthMonth(Month.fromNumber(aRs.getShort("BIRTHMONTH")));
		familyMember.setBirthYear(aRs.getShort("BIRTHYEAR"));
		familyMember.setMaritalStatus(MaritalStatus.getMaritalStatus(aRs.getString("MARITALSTATUS")));
		familyMember.setWeddingDate(AbstractDao.getTimestampFromResultSet(aRs, "WEDDINGDATE"));
		familyMember.setDateOfDeath(AbstractDao.getTimestampFromResultSet(aRs, "DATEOFDEATH"));
		String phone = aRs.getString("PHONE");
		familyMember.setPhone(DataFormatter.formatPhoneNumber(phone));
		familyMember.setPhoneWhatsappRegistered(aRs.getBoolean("ISPHONEWHATSAPPREGISTERED"));
		familyMember.setEmail(aRs.getString("EMAIL"));

		familyMember.setAddressSameAsFamily(aRs.getBoolean("ADDRESSSAMEASFAMILY"));
		familyMember.setMemberAddressId(aRs.getInt("MEMBERADDRESSID"));

		familyMember.setEducationDetails(aRs.getString("EDUCATIONDETAILS"));
		familyMember.setOccupation(aRs.getString("OCCUPATION"));
		familyMember.setHobby(aRs.getString("HOBBY"));
		familyMember.setMemberSearchText(aRs.getString("MEMBERSEARCHTEXT"));
		familyMember.setProfileImage(aRs.getString("PROFILEIMAGE"));
		familyMember.setProfileImageThumbnail(aRs.getString("PROFILEIMAGETHUMBNAIL"));
		familyMember.setImageLastUpdated(AbstractDao.getTimestampFromResultSet(aRs, "IMAGELASTUPDATED"));
//		familyMember.setCreatedBy(aRs.getInt("CREATEDBY"));
//		familyMember.setCreatedDate(AbstractDao.getTimestampFromResultSet(aRs, "CREATEDDATE"));
//		familyMember.setLastUpdatedBy(aRs.getInt("LASTUPDATEDBY"));
//		familyMember.setLastUpdatedDate(AbstractDao.getTimestampFromResultSet(aRs, "LASTUPDATEDDATE"));
		return familyMember;
	}

}
