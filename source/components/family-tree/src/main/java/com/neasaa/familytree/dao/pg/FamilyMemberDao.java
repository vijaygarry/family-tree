/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.familytree.dao.pg;

import java.sql.SQLException;

import com.neasaa.base.app.dao.pg.AbstractDao;
import com.neasaa.familytree.entity.FamilyMember;
import java.sql.Connection;
import org.springframework.jdbc.core.PreparedStatementCreator;
import java.sql.PreparedStatement;

public class FamilyMemberDao extends AbstractDao {

	private PreparedStatement buildInsertStatement(Connection aConection, FamilyMember aFamilyMember) throws SQLException {
		String sqlStatement = "INSERT INTO FAMILYMEMBER (FAMILYID, HEADOFFAMILY, FIRSTNAME, LASTNAME, NICKNAME, MEMBERADDRESSID, PHONE, ISPHONEWHATSAPPREGISTERED, EMAIL, LINKEDINURL, SEX, BIRTHDAY, BIRTHMONTH, BIRTHYEAR, MARITALSTATUS, EDUCATIONDETAILS, OCCUPATION, WORKINGAT, HOBBY, PROFILEIMAGE, PROFILEIMAGETHUMBNAIL, IMAGELASTUPDATED, CREATEDBY, CREATEDDATE, LASTUPDATEDBY, LASTUPDATEDDATE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		PreparedStatement prepareStatement = aConection.prepareStatement(sqlStatement);
		setIntInStatement(prepareStatement, 1, aFamilyMember.getFamilyId());
		setBooleanInStatement(prepareStatement, 2, aFamilyMember.isHeadOfFamily());
		setStringInStatement(prepareStatement, 3, aFamilyMember.getFirstName());
		setStringInStatement(prepareStatement, 4, aFamilyMember.getLastName());
		setStringInStatement(prepareStatement, 5, aFamilyMember.getNickName());
		setIntInStatement(prepareStatement, 6, aFamilyMember.getMemberAddressId());
		setStringInStatement(prepareStatement, 7, aFamilyMember.getPhone());
		setBooleanInStatement(prepareStatement, 8, aFamilyMember.isPhoneWhatsappRegistered());
		setStringInStatement(prepareStatement, 9, aFamilyMember.getEmail());
		setStringInStatement(prepareStatement, 10, aFamilyMember.getLinkedinUrl());
		setStringInStatement(prepareStatement, 11, aFamilyMember.getSex());
		setSmallIntInStatement(prepareStatement, 12, aFamilyMember.getBirthDay());
		setSmallIntInStatement(prepareStatement, 13, aFamilyMember.getBirthMonth());
		setSmallIntInStatement(prepareStatement, 14, aFamilyMember.getBirthYear());
		setStringInStatement(prepareStatement, 15, aFamilyMember.getMaritalStatus());
		setStringInStatement(prepareStatement, 16, aFamilyMember.getEducationDetails());
		setStringInStatement(prepareStatement, 17, aFamilyMember.getOccupation());
		setStringInStatement(prepareStatement, 18, aFamilyMember.getWorkingAt());
		setStringInStatement(prepareStatement, 19, aFamilyMember.getHobby());
		setStringInStatement(prepareStatement, 20, aFamilyMember.getProfileImage());
		setStringInStatement(prepareStatement, 21, aFamilyMember.getProfileImageThumbnail());
		setTimestampInStatement(prepareStatement, 22, aFamilyMember.getImageLastUpdated());
		setIntInStatement(prepareStatement, 23, aFamilyMember.getCreatedBy());
		setTimestampInStatement(prepareStatement, 24, aFamilyMember.getCreatedDate());
		setIntInStatement(prepareStatement, 25, aFamilyMember.getLastUpdatedBy());
		setTimestampInStatement(prepareStatement, 26, aFamilyMember.getLastUpdatedDate());
		return prepareStatement;
	}

	public int insertFamilyMember(FamilyMember aFamilyMember) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
				return buildInsertStatement(aCon, aFamilyMember);
			}
		});

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
		String updateStatement = "UPDATE FAMILYMEMBER SET FAMILYID = ? , HEADOFFAMILY = ? , FIRSTNAME = ? , LASTNAME = ? , NICKNAME = ? , MEMBERADDRESSID = ? , PHONE = ? , ISPHONEWHATSAPPREGISTERED = ? , EMAIL = ? , LINKEDINURL = ? , SEX = ? , BIRTHDAY = ? , BIRTHMONTH = ? , BIRTHYEAR = ? , MARITALSTATUS = ? , EDUCATIONDETAILS = ? , OCCUPATION = ? , WORKINGAT = ? , HOBBY = ? , PROFILEIMAGE = ? , PROFILEIMAGETHUMBNAIL = ? , IMAGELASTUPDATED = ? , CREATEDBY = ? , CREATEDDATE = ? , LASTUPDATEDBY = ? , LASTUPDATEDDATE = ?  where MEMBERID = ?";

		PreparedStatement prepareStatement = aConection.prepareStatement(updateStatement);
		setIntInStatement(prepareStatement, 1, aFamilyMember.getFamilyId());
		setBooleanInStatement(prepareStatement, 2, aFamilyMember.isHeadOfFamily());
		setStringInStatement(prepareStatement, 3, aFamilyMember.getFirstName());
		setStringInStatement(prepareStatement, 4, aFamilyMember.getLastName());
		setStringInStatement(prepareStatement, 5, aFamilyMember.getNickName());
		setIntInStatement(prepareStatement, 6, aFamilyMember.getMemberAddressId());
		setStringInStatement(prepareStatement, 7, aFamilyMember.getPhone());
		setBooleanInStatement(prepareStatement, 8, aFamilyMember.isPhoneWhatsappRegistered());
		setStringInStatement(prepareStatement, 9, aFamilyMember.getEmail());
		setStringInStatement(prepareStatement, 10, aFamilyMember.getLinkedinUrl());
		setStringInStatement(prepareStatement, 11, aFamilyMember.getSex());
		setSmallIntInStatement(prepareStatement, 12, aFamilyMember.getBirthDay());
		setSmallIntInStatement(prepareStatement, 13, aFamilyMember.getBirthMonth());
		setSmallIntInStatement(prepareStatement, 14, aFamilyMember.getBirthYear());
		setStringInStatement(prepareStatement, 15, aFamilyMember.getMaritalStatus());
		setStringInStatement(prepareStatement, 16, aFamilyMember.getEducationDetails());
		setStringInStatement(prepareStatement, 17, aFamilyMember.getOccupation());
		setStringInStatement(prepareStatement, 18, aFamilyMember.getWorkingAt());
		setStringInStatement(prepareStatement, 19, aFamilyMember.getHobby());
		setStringInStatement(prepareStatement, 20, aFamilyMember.getProfileImage());
		setStringInStatement(prepareStatement, 21, aFamilyMember.getProfileImageThumbnail());
		setTimestampInStatement(prepareStatement, 22, aFamilyMember.getImageLastUpdated());
		setIntInStatement(prepareStatement, 23, aFamilyMember.getCreatedBy());
		setTimestampInStatement(prepareStatement, 24, aFamilyMember.getCreatedDate());
		setIntInStatement(prepareStatement, 25, aFamilyMember.getLastUpdatedBy());
		setTimestampInStatement(prepareStatement, 26, aFamilyMember.getLastUpdatedDate());
		setIntInStatement(prepareStatement, 27, aFamilyMember.getMemberId());
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

	public FamilyMember fetchFamilyMember(FamilyMember aFamilyMember) throws SQLException {
		String selectQuery = "select  MEMBERID , FAMILYID , HEADOFFAMILY , FIRSTNAME , LASTNAME , NICKNAME , MEMBERADDRESSID , PHONE , ISPHONEWHATSAPPREGISTERED , EMAIL , LINKEDINURL , SEX , BIRTHDAY , BIRTHMONTH , BIRTHYEAR , MARITALSTATUS , EDUCATIONDETAILS , OCCUPATION , WORKINGAT , HOBBY , PROFILEIMAGE , PROFILEIMAGETHUMBNAIL , IMAGELASTUPDATED , CREATEDBY , CREATEDDATE , LASTUPDATEDBY , LASTUPDATEDDATE  from FAMILYMEMBER where MEMBERID = ? ";
		return getJdbcTemplate().queryForObject(selectQuery, new FamilyMemberRowMapper(), aFamilyMember.getMemberId());

	}

}
