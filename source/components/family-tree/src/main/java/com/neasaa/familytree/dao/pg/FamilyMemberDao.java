/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.familytree.dao.pg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.neasaa.base.app.dao.pg.AbstractDao;
import com.neasaa.familytree.entity.FamilyMember;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Repository
public class FamilyMemberDao extends AbstractDao {

	private static final String SELECT_ALL_MEMBERS_FOR_FAMILY = "select  MEMBERID , FAMILYID , LOGONNAME , HEADOFFAMILY , FIRSTNAME , FIRSTNAMEINHINDI , LASTNAME , MAIDENLASTNAME , NICKNAME , NICKNAMEINHINDI , ADDRESSSAMEASFAMILY , MEMBERADDRESSID , PHONE , ISPHONEWHATSAPPREGISTERED , EMAIL , LINKEDINURL , GENDER , BIRTHDAY , BIRTHMONTH , BIRTHYEAR , DATEOFDEATH , MARITALSTATUS , EDUCATIONDETAILS , OCCUPATION , WORKINGAT , HOBBY , PROFILEIMAGE , PROFILEIMAGETHUMBNAIL , IMAGELASTUPDATED , CREATEDBY , CREATEDDATE , LASTUPDATEDBY , LASTUPDATEDDATE  "
			+ "from " + BASE_SCHEMA_NAME + "FAMILYMEMBER "
					+ "where FAMILYID = ? ";
	
	private static final String SELECT_MEMBER_BY_ID = "select  MEMBERID , FAMILYID , LOGONNAME , HEADOFFAMILY , FIRSTNAME , FIRSTNAMEINHINDI , LASTNAME , MAIDENLASTNAME , NICKNAME , NICKNAMEINHINDI , ADDRESSSAMEASFAMILY , MEMBERADDRESSID , PHONE , ISPHONEWHATSAPPREGISTERED , EMAIL , LINKEDINURL , GENDER , BIRTHDAY , BIRTHMONTH , BIRTHYEAR , DATEOFDEATH , MARITALSTATUS , EDUCATIONDETAILS , OCCUPATION , WORKINGAT , HOBBY , PROFILEIMAGE , PROFILEIMAGETHUMBNAIL , IMAGELASTUPDATED , CREATEDBY , CREATEDDATE , LASTUPDATEDBY , LASTUPDATEDDATE  "
			+ "from " + BASE_SCHEMA_NAME + "FAMILYMEMBER "
					+ "where MEMBERID = ? ";

	private static final String SELECT_MEMBER_BY_LOGON_NAME = "select  MEMBERID , FAMILYID , LOGONNAME , HEADOFFAMILY , FIRSTNAME , FIRSTNAMEINHINDI , LASTNAME , MAIDENLASTNAME , NICKNAME , NICKNAMEINHINDI , ADDRESSSAMEASFAMILY , MEMBERADDRESSID , PHONE , ISPHONEWHATSAPPREGISTERED , EMAIL , LINKEDINURL , GENDER , BIRTHDAY , BIRTHMONTH , BIRTHYEAR , DATEOFDEATH , MARITALSTATUS , EDUCATIONDETAILS , OCCUPATION , WORKINGAT , HOBBY , PROFILEIMAGE , PROFILEIMAGETHUMBNAIL , IMAGELASTUPDATED , CREATEDBY , CREATEDDATE , LASTUPDATEDBY , LASTUPDATEDDATE  "
			+ "from " + BASE_SCHEMA_NAME + "FAMILYMEMBER "
			+ "where LOGONNAME = ? ";

	private static final String SELECT_HEAD_OF_FAMILY_BY_FAMILY_ID = "select  MEMBERID , FAMILYID , LOGONNAME , HEADOFFAMILY , FIRSTNAME , FIRSTNAMEINHINDI , LASTNAME , MAIDENLASTNAME , NICKNAME , NICKNAMEINHINDI , ADDRESSSAMEASFAMILY , MEMBERADDRESSID , PHONE , ISPHONEWHATSAPPREGISTERED , EMAIL , LINKEDINURL , GENDER , BIRTHDAY , BIRTHMONTH , BIRTHYEAR , DATEOFDEATH , MARITALSTATUS , EDUCATIONDETAILS , OCCUPATION , WORKINGAT , HOBBY , PROFILEIMAGE , PROFILEIMAGETHUMBNAIL , IMAGELASTUPDATED , CREATEDBY , CREATEDDATE , LASTUPDATEDBY , LASTUPDATEDDATE  "
			+ "from " + BASE_SCHEMA_NAME + "FAMILYMEMBER "
			+ "where FAMILYID = ? and HEADOFFAMILY = true";


	public List<FamilyMember> allMembersForFamily(int familyId) {
		return getJdbcTemplate().query(SELECT_ALL_MEMBERS_FOR_FAMILY, new FamilyMemberRowMapper(), familyId);
	}
	
	public FamilyMember getMemberById(int memberId) {
		List<FamilyMember> memberList = getJdbcTemplate().query(SELECT_MEMBER_BY_ID, new FamilyMemberRowMapper(), memberId);
		
		if(memberList.isEmpty()) {
			return null;
		}
		if(memberList.size() > 1) {
			throw new RuntimeException("Invalid member id entry");
		}
		return memberList.get(0);
	}

	public FamilyMember getHeadOfFamilyByFamilyId(int familyId) {
		List<FamilyMember> memberList = getJdbcTemplate().query(SELECT_HEAD_OF_FAMILY_BY_FAMILY_ID, new FamilyMemberRowMapper(), familyId);

		if(memberList.isEmpty()) {
			return null;
		}
		if(memberList.size() > 1) {
			throw new RuntimeException("Invalid member id entry");
		}
		return memberList.get(0);
	}

	public FamilyMember getMemberByLogonName(String logonName) {
		List<FamilyMember> memberList = getJdbcTemplate().query(SELECT_MEMBER_BY_LOGON_NAME, new FamilyMemberRowMapper(), logonName);

		if(memberList.isEmpty()) {
			throw new RuntimeException("No member found with logon name");
		}
		if(memberList.size() > 1) {
			throw new RuntimeException("Logon name i snot unique, multiple members found with same logon name");
		}
		return memberList.get(0);
	}

	public FamilyMember addFamilyMember(FamilyMember aFamilyMember) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
				return buildInsertStatement(aCon, aFamilyMember);
			}
		}, keyHolder);
		
		int memberId = -1;
		Number key = keyHolder.getKey();
		if (key != null) {
			memberId = key.intValue();
		}
		log.info("New family member added with id " + memberId);
		return getMemberById(memberId);
	}
	
	
	private PreparedStatement buildInsertStatement(Connection aConection, FamilyMember aFamilyMember) throws SQLException {
		String sqlStatement = "INSERT INTO " + BASE_SCHEMA_NAME + "FAMILYMEMBER (FAMILYID, LOGONNAME, HEADOFFAMILY, FIRSTNAME, FIRSTNAMEINHINDI, LASTNAME, MAIDENLASTNAME, NICKNAME, NICKNAMEINHINDI, ADDRESSSAMEASFAMILY, MEMBERADDRESSID, PHONE, ISPHONEWHATSAPPREGISTERED, EMAIL, LINKEDINURL, GENDER, BIRTHDAY, BIRTHMONTH, BIRTHYEAR, DATEOFDEATH, MARITALSTATUS, EDUCATIONDETAILS, OCCUPATION, WORKINGAT, HOBBY, PROFILEIMAGE, PROFILEIMAGETHUMBNAIL, IMAGELASTUPDATED, CREATEDBY, CREATEDDATE, LASTUPDATEDBY, LASTUPDATEDDATE) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		PreparedStatement prepareStatement = aConection.prepareStatement(sqlStatement, new String[] { "memberid" });
		setIntInStatement(prepareStatement, 1, aFamilyMember.getFamilyId());
		setStringInStatement(prepareStatement, 2, aFamilyMember.getLogonName());
		setBooleanInStatement(prepareStatement, 3, aFamilyMember.isHeadOfFamily());
		setStringInStatement(prepareStatement, 4, aFamilyMember.getFirstName());
		setStringInStatement(prepareStatement, 5, aFamilyMember.getFirstNameInHindi());
		setStringInStatement(prepareStatement, 6, aFamilyMember.getLastName());
		setStringInStatement(prepareStatement, 7, aFamilyMember.getMaidenLastName());
		setStringInStatement(prepareStatement, 8, aFamilyMember.getNickName());
		setStringInStatement(prepareStatement, 9, aFamilyMember.getNickNameInHindi());
		setBooleanInStatement(prepareStatement, 10, aFamilyMember.isAddressSameAsFamily());
		setIntInStatement(prepareStatement, 11, aFamilyMember.getMemberAddressId());
		setStringInStatement(prepareStatement, 12, aFamilyMember.getPhone());
		setBooleanInStatement(prepareStatement, 13, aFamilyMember.isPhoneWhatsappRegistered());
		setStringInStatement(prepareStatement, 14, aFamilyMember.getEmail());
		setStringInStatement(prepareStatement, 15, aFamilyMember.getLinkedinUrl());
		setStringInStatement(prepareStatement, 16, aFamilyMember.getGender().name());
		if(aFamilyMember.getBirthDay() == null) {
			setSmallIntInStatement(prepareStatement, 17, (short)-1);
		} else {
			// If birth day is not set, we set it to -1
			setSmallIntInStatement(prepareStatement, 17, aFamilyMember.getBirthDay());
		}
		setSmallIntInStatement(prepareStatement, 18, aFamilyMember.getBirthMonth().getMonthNumber());
		setSmallIntInStatement(prepareStatement, 19, aFamilyMember.getBirthYear());
		setTimestampInStatement(prepareStatement, 20, aFamilyMember.getDateOfDeath());
		setStringInStatement(prepareStatement, 21, aFamilyMember.getMaritalStatus().name());
		setStringInStatement(prepareStatement, 22, aFamilyMember.getEducationDetails());
		setStringInStatement(prepareStatement, 23, aFamilyMember.getOccupation());
		setStringInStatement(prepareStatement, 24, aFamilyMember.getWorkingAt());
		setStringInStatement(prepareStatement, 25, aFamilyMember.getHobby());
		setStringInStatement(prepareStatement, 26, aFamilyMember.getProfileImage());
		setStringInStatement(prepareStatement, 27, aFamilyMember.getProfileImageThumbnail());
		setTimestampInStatement(prepareStatement, 28, aFamilyMember.getImageLastUpdated());
		setIntInStatement(prepareStatement, 29, aFamilyMember.getCreatedBy());
		setTimestampInStatement(prepareStatement, 30, aFamilyMember.getCreatedDate());
		setIntInStatement(prepareStatement, 31, aFamilyMember.getLastUpdatedBy());
		setTimestampInStatement(prepareStatement, 32, aFamilyMember.getLastUpdatedDate());
		return prepareStatement;
	}



	public int deleteFamilyMember(FamilyMember aFamilyMember) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aConection) throws SQLException {
				String deleteSqlQuery = "DELETE FROM FAMILYMEMBER WHERE MEMBERID = ?";
				PreparedStatement prepareStatement = aConection.prepareStatement(deleteSqlQuery);
				setIntInStatement(prepareStatement, 1, aFamilyMember.getMemberId());
				return prepareStatement;
			}
		});

	}

	public PreparedStatement buildUpdateStatement(Connection aConection, FamilyMember aFamilyMember) throws SQLException {
		String updateStatement = "UPDATE FAMILYMEMBER SET FAMILYID = ? , LOGONNAME = ? , HEADOFFAMILY = ? , FIRSTNAME = ? , FIRSTNAMEINHINDI = ? , LASTNAME = ? , MAIDENLASTNAME = ? , NICKNAME = ? , NICKNAMEINHINDI = ? , ADDRESSSAMEASFAMILY = ? , MEMBERADDRESSID = ? , PHONE = ? , ISPHONEWHATSAPPREGISTERED = ? , EMAIL = ? , LINKEDINURL = ? , GENDER = ? , BIRTHDAY = ? , BIRTHMONTH = ? , BIRTHYEAR = ? , DATEOFDEATH = ? , MARITALSTATUS = ? , EDUCATIONDETAILS = ? , OCCUPATION = ? , WORKINGAT = ? , HOBBY = ? , PROFILEIMAGE = ? , PROFILEIMAGETHUMBNAIL = ? , IMAGELASTUPDATED = ? , CREATEDBY = ? , CREATEDDATE = ? , LASTUPDATEDBY = ? , LASTUPDATEDDATE = ?  where MEMBERID = ?";

		PreparedStatement prepareStatement = aConection.prepareStatement(updateStatement);
		setIntInStatement(prepareStatement, 1, aFamilyMember.getFamilyId());
		setStringInStatement(prepareStatement, 2, aFamilyMember.getLogonName());
		setBooleanInStatement(prepareStatement, 3, aFamilyMember.isHeadOfFamily());
		setStringInStatement(prepareStatement, 4, aFamilyMember.getFirstName());
		setStringInStatement(prepareStatement, 5, aFamilyMember.getFirstNameInHindi());
		setStringInStatement(prepareStatement, 6, aFamilyMember.getLastName());
		setStringInStatement(prepareStatement, 7, aFamilyMember.getMaidenLastName());
		setStringInStatement(prepareStatement, 8, aFamilyMember.getNickName());
		setStringInStatement(prepareStatement, 9, aFamilyMember.getNickNameInHindi());
		setBooleanInStatement(prepareStatement, 10, aFamilyMember.isAddressSameAsFamily());
		setIntInStatement(prepareStatement, 11, aFamilyMember.getMemberAddressId());
		setStringInStatement(prepareStatement, 12, aFamilyMember.getPhone());
		setBooleanInStatement(prepareStatement, 13, aFamilyMember.isPhoneWhatsappRegistered());
		setStringInStatement(prepareStatement, 14, aFamilyMember.getEmail());
		setStringInStatement(prepareStatement, 15, aFamilyMember.getLinkedinUrl());
		setStringInStatement(prepareStatement, 16, aFamilyMember.getGender().name());
		setSmallIntInStatement(prepareStatement, 17, aFamilyMember.getBirthDay());
		setSmallIntInStatement(prepareStatement, 18, aFamilyMember.getBirthMonth().getMonthNumber());
		setSmallIntInStatement(prepareStatement, 19, aFamilyMember.getBirthYear());
		setTimestampInStatement(prepareStatement, 20, aFamilyMember.getDateOfDeath());
		setStringInStatement(prepareStatement, 21, aFamilyMember.getMaritalStatus().name());
		setStringInStatement(prepareStatement, 22, aFamilyMember.getEducationDetails());
		setStringInStatement(prepareStatement, 23, aFamilyMember.getOccupation());
		setStringInStatement(prepareStatement, 24, aFamilyMember.getWorkingAt());
		setStringInStatement(prepareStatement, 25, aFamilyMember.getHobby());
		setStringInStatement(prepareStatement, 26, aFamilyMember.getProfileImage());
		setStringInStatement(prepareStatement, 27, aFamilyMember.getProfileImageThumbnail());
		setTimestampInStatement(prepareStatement, 28, aFamilyMember.getImageLastUpdated());
		setIntInStatement(prepareStatement, 29, aFamilyMember.getCreatedBy());
		setTimestampInStatement(prepareStatement, 30, aFamilyMember.getCreatedDate());
		setIntInStatement(prepareStatement, 31, aFamilyMember.getLastUpdatedBy());
		setTimestampInStatement(prepareStatement, 32, aFamilyMember.getLastUpdatedDate());
		setIntInStatement(prepareStatement, 33, aFamilyMember.getMemberId());
		return prepareStatement;
	}

	public int updateFamilyMember(FamilyMember aFamilyMember) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
				return buildUpdateStatement(aCon, aFamilyMember);
			}
		});

	}


}
