/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.familytree.dao.pg;

import java.sql.SQLException;

import com.neasaa.base.app.dao.pg.AbstractDao;
import com.neasaa.familytree.entity.FamilyMember;
import com.neasaa.familytree.enums.MaritalStatus;
import com.neasaa.familytree.enums.Gender;

import java.sql.ResultSet;

import com.neasaa.familytree.enums.Month;
import org.springframework.jdbc.core.RowMapper;

public class FamilyMemberRowMapper implements RowMapper<FamilyMember> {

	@Override
	public FamilyMember mapRow(ResultSet aRs, int aRowNum) throws SQLException {
		FamilyMember familyMember = new FamilyMember();
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
		familyMember.setAddressSameAsFamily(aRs.getBoolean("ADDRESSSAMEASFAMILY"));
		familyMember.setMemberAddressId(aRs.getInt("MEMBERADDRESSID"));
		familyMember.setPhone(aRs.getString("PHONE"));
		familyMember.setPhoneWhatsappRegistered(aRs.getBoolean("ISPHONEWHATSAPPREGISTERED"));
		familyMember.setEmail(aRs.getString("EMAIL"));
		familyMember.setLinkedinUrl(aRs.getString("LINKEDINURL"));
		familyMember.setGender(Gender.getGenderByString(aRs.getString("GENDER")));
		familyMember.setBirthDay(aRs.getShort("BIRTHDAY"));
		familyMember.setBirthMonth(Month.fromNumber(aRs.getShort("BIRTHMONTH")));
		familyMember.setBirthYear(aRs.getShort("BIRTHYEAR"));
		familyMember.setDateOfDeath(AbstractDao.getTimestampFromResultSet(aRs, "DATEOFDEATH"));
		familyMember.setMaritalStatus(MaritalStatus.getMaritalStatus(aRs.getString("MARITALSTATUS")));
		familyMember.setEducationDetails(aRs.getString("EDUCATIONDETAILS"));
		familyMember.setOccupation(aRs.getString("OCCUPATION"));
		familyMember.setWorkingAt(aRs.getString("WORKINGAT"));
		familyMember.setHobby(aRs.getString("HOBBY"));
		familyMember.setProfileImage(aRs.getString("PROFILEIMAGE"));
		familyMember.setProfileImageThumbnail(aRs.getString("PROFILEIMAGETHUMBNAIL"));
		familyMember.setImageLastUpdated(AbstractDao.getTimestampFromResultSet(aRs, "IMAGELASTUPDATED"));
		familyMember.setCreatedBy(aRs.getInt("CREATEDBY"));
		familyMember.setCreatedDate(AbstractDao.getTimestampFromResultSet(aRs, "CREATEDDATE"));
		familyMember.setLastUpdatedBy(aRs.getInt("LASTUPDATEDBY"));
		familyMember.setLastUpdatedDate(AbstractDao.getTimestampFromResultSet(aRs, "LASTUPDATEDDATE"));
		return familyMember;
	}

}
