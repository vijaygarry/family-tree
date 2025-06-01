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

	private static final String SELECT_ALL_MEMBERS_FOR_FAMILY = "select  MEMBERID , FAMILYID , HEADOFFAMILY , FIRSTNAME , LASTNAME , MAIDENLASTNAME , NICKNAME , ADDRESSSAMEASFAMILY , MEMBERADDRESSID , PHONE , ISPHONEWHATSAPPREGISTERED , EMAIL , LINKEDINURL , SEX , BIRTHDAY , BIRTHMONTH , BIRTHYEAR , MARITALSTATUS , EDUCATIONDETAILS , OCCUPATION , WORKINGAT , HOBBY , PROFILEIMAGE , PROFILEIMAGETHUMBNAIL , IMAGELASTUPDATED , CREATEDBY , CREATEDDATE , LASTUPDATEDBY , LASTUPDATEDDATE  "
			+ "from " + BASE_SCHEMA_NAME + "FAMILYMEMBER "
					+ "where FAMILYID = ? ";
	
	private static final String SELECT_MEMBER_BY_ID = "select  MEMBERID , FAMILYID , HEADOFFAMILY , FIRSTNAME , LASTNAME , MAIDENLASTNAME , NICKNAME , ADDRESSSAMEASFAMILY , MEMBERADDRESSID , PHONE , ISPHONEWHATSAPPREGISTERED , EMAIL , LINKEDINURL , SEX , BIRTHDAY , BIRTHMONTH , BIRTHYEAR , MARITALSTATUS , EDUCATIONDETAILS , OCCUPATION , WORKINGAT , HOBBY , PROFILEIMAGE , PROFILEIMAGETHUMBNAIL , IMAGELASTUPDATED , CREATEDBY , CREATEDDATE , LASTUPDATEDBY , LASTUPDATEDDATE  "
			+ "from " + BASE_SCHEMA_NAME + "FAMILYMEMBER "
					+ "where MEMBERID = ? ";
	
	public List<FamilyMember> allMembersForFamily(int familyId) {
		return getJdbcTemplate().query(SELECT_ALL_MEMBERS_FOR_FAMILY, new FamilyMemberRowMapper(), familyId);
	}
	
	public FamilyMember getMemberById(int memberId) {
		List<FamilyMember> memberList = getJdbcTemplate().query(SELECT_MEMBER_BY_ID, new FamilyMemberRowMapper(), memberId);
		
		if(memberList == null || memberList.size() == 0) {
			return null;
		}
		if(memberList.size() > 1) {
			throw new RuntimeException("Invalid member id entry");
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
		String sqlStatement = "INSERT INTO " + BASE_SCHEMA_NAME + "FAMILYMEMBER (FAMILYID, HEADOFFAMILY, FIRSTNAME, LASTNAME, MAIDENLASTNAME, NICKNAME, ADDRESSSAMEASFAMILY, MEMBERADDRESSID, PHONE, ISPHONEWHATSAPPREGISTERED, EMAIL, LINKEDINURL, SEX, BIRTHDAY, BIRTHMONTH, BIRTHYEAR, MARITALSTATUS, EDUCATIONDETAILS, OCCUPATION, WORKINGAT, HOBBY, PROFILEIMAGE, PROFILEIMAGETHUMBNAIL, IMAGELASTUPDATED, CREATEDBY, CREATEDDATE, LASTUPDATEDBY, LASTUPDATEDDATE) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		PreparedStatement prepareStatement = aConection.prepareStatement(sqlStatement, new String[] { "memberid" });
		setIntInStatement(prepareStatement, 1, aFamilyMember.getFamilyId());
		setBooleanInStatement(prepareStatement, 2, aFamilyMember.isHeadOfFamily());
		setStringInStatement(prepareStatement, 3, aFamilyMember.getFirstName());
		setStringInStatement(prepareStatement, 4, aFamilyMember.getLastName());
		setStringInStatement(prepareStatement, 5, aFamilyMember.getMaidenLastName());
		setStringInStatement(prepareStatement, 6, aFamilyMember.getNickName());
		setBooleanInStatement(prepareStatement, 7, aFamilyMember.isAddressSameAsFamily());
		setIntInStatement(prepareStatement, 8, aFamilyMember.getMemberAddressId());
		setStringInStatement(prepareStatement, 9, aFamilyMember.getPhone());
		setBooleanInStatement(prepareStatement, 10, aFamilyMember.isPhoneWhatsappRegistered());
		setStringInStatement(prepareStatement, 11, aFamilyMember.getEmail());
		setStringInStatement(prepareStatement, 12, aFamilyMember.getLinkedinUrl());
		setStringInStatement(prepareStatement, 13, aFamilyMember.getSex().name());
		setSmallIntInStatement(prepareStatement, 14, aFamilyMember.getBirthDay());
		setSmallIntInStatement(prepareStatement, 15, aFamilyMember.getBirthMonth());
		setSmallIntInStatement(prepareStatement, 16, aFamilyMember.getBirthYear());
		setStringInStatement(prepareStatement, 17, aFamilyMember.getMaritalStatus().name());
		setStringInStatement(prepareStatement, 18, aFamilyMember.getEducationDetails());
		setStringInStatement(prepareStatement, 19, aFamilyMember.getOccupation());
		setStringInStatement(prepareStatement, 20, aFamilyMember.getWorkingAt());
		setStringInStatement(prepareStatement, 21, aFamilyMember.getHobby());
		setStringInStatement(prepareStatement, 22, aFamilyMember.getProfileImage());
		setStringInStatement(prepareStatement, 23, aFamilyMember.getProfileImageThumbnail());
		setTimestampInStatement(prepareStatement, 24, aFamilyMember.getImageLastUpdated());
		setIntInStatement(prepareStatement, 25, aFamilyMember.getCreatedBy());
		setTimestampInStatement(prepareStatement, 26, aFamilyMember.getCreatedDate());
		setIntInStatement(prepareStatement, 27, aFamilyMember.getLastUpdatedBy());
		setTimestampInStatement(prepareStatement, 28, aFamilyMember.getLastUpdatedDate());
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
		String updateStatement = "UPDATE FAMILYMEMBER SET FAMILYID = ? , HEADOFFAMILY = ? , FIRSTNAME = ? , LASTNAME = ? , MAIDENLASTNAME = ? , NICKNAME = ? , ADDRESSSAMEASFAMILY = ? , MEMBERADDRESSID = ? , PHONE = ? , ISPHONEWHATSAPPREGISTERED = ? , EMAIL = ? , LINKEDINURL = ? , SEX = ? , BIRTHDAY = ? , BIRTHMONTH = ? , BIRTHYEAR = ? , MARITALSTATUS = ? , EDUCATIONDETAILS = ? , OCCUPATION = ? , WORKINGAT = ? , HOBBY = ? , PROFILEIMAGE = ? , PROFILEIMAGETHUMBNAIL = ? , IMAGELASTUPDATED = ? , CREATEDBY = ? , CREATEDDATE = ? , LASTUPDATEDBY = ? , LASTUPDATEDDATE = ?  where MEMBERID = ?";

		PreparedStatement prepareStatement = aConection.prepareStatement(updateStatement);
		setIntInStatement(prepareStatement, 1, aFamilyMember.getFamilyId());
		setBooleanInStatement(prepareStatement, 2, aFamilyMember.isHeadOfFamily());
		setStringInStatement(prepareStatement, 3, aFamilyMember.getFirstName());
		setStringInStatement(prepareStatement, 4, aFamilyMember.getLastName());
		setStringInStatement(prepareStatement, 5, aFamilyMember.getMaidenLastName());
		setStringInStatement(prepareStatement, 6, aFamilyMember.getNickName());
		setBooleanInStatement(prepareStatement, 7, aFamilyMember.isAddressSameAsFamily());
		setIntInStatement(prepareStatement, 8, aFamilyMember.getMemberAddressId());
		setStringInStatement(prepareStatement, 9, aFamilyMember.getPhone());
		setBooleanInStatement(prepareStatement, 10, aFamilyMember.isPhoneWhatsappRegistered());
		setStringInStatement(prepareStatement, 11, aFamilyMember.getEmail());
		setStringInStatement(prepareStatement, 12, aFamilyMember.getLinkedinUrl());
		setStringInStatement(prepareStatement, 13, aFamilyMember.getSex().name());
		setSmallIntInStatement(prepareStatement, 14, aFamilyMember.getBirthDay());
		setSmallIntInStatement(prepareStatement, 15, aFamilyMember.getBirthMonth());
		setSmallIntInStatement(prepareStatement, 16, aFamilyMember.getBirthYear());
		setStringInStatement(prepareStatement, 17, aFamilyMember.getMaritalStatus().name());
		setStringInStatement(prepareStatement, 18, aFamilyMember.getEducationDetails());
		setStringInStatement(prepareStatement, 19, aFamilyMember.getOccupation());
		setStringInStatement(prepareStatement, 20, aFamilyMember.getWorkingAt());
		setStringInStatement(prepareStatement, 21, aFamilyMember.getHobby());
		setStringInStatement(prepareStatement, 22, aFamilyMember.getProfileImage());
		setStringInStatement(prepareStatement, 23, aFamilyMember.getProfileImageThumbnail());
		setTimestampInStatement(prepareStatement, 24, aFamilyMember.getImageLastUpdated());
		setIntInStatement(prepareStatement, 25, aFamilyMember.getCreatedBy());
		setTimestampInStatement(prepareStatement, 26, aFamilyMember.getCreatedDate());
		setIntInStatement(prepareStatement, 27, aFamilyMember.getLastUpdatedBy());
		setTimestampInStatement(prepareStatement, 28, aFamilyMember.getLastUpdatedDate());
		setIntInStatement(prepareStatement, 29, aFamilyMember.getMemberId());
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
		String selectQuery = "select  MEMBERID , FAMILYID , HEADOFFAMILY , FIRSTNAME , LASTNAME , MAIDENLASTNAME , NICKNAME , ADDRESSSAMEASFAMILY , MEMBERADDRESSID , PHONE , ISPHONEWHATSAPPREGISTERED , EMAIL , LINKEDINURL , SEX , BIRTHDAY , BIRTHMONTH , BIRTHYEAR , MARITALSTATUS , EDUCATIONDETAILS , OCCUPATION , WORKINGAT , HOBBY , PROFILEIMAGE , PROFILEIMAGETHUMBNAIL , IMAGELASTUPDATED , CREATEDBY , CREATEDDATE , LASTUPDATEDBY , LASTUPDATEDDATE  from " + BASE_SCHEMA_NAME + "FAMILYMEMBER where MEMBERID = ? ";
		return getJdbcTemplate().queryForObject(selectQuery, new FamilyMemberRowMapper(), aFamilyMember.getMemberId());

	}

}
