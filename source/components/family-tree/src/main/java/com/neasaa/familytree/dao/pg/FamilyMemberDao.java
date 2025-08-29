/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.familytree.dao.pg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.neasaa.base.app.operation.exception.InternalServerException;
import com.neasaa.familytree.entity.FamilyMemberEntity;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.neasaa.base.app.dao.pg.AbstractDao;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Repository
public class FamilyMemberDao extends AbstractDao {

	private static final String SELECT_ALL_MEMBERS_FOR_FAMILY =
			"select  MEMBERID, FAMILYID, LOGONNAME, HEADOFFAMILY, FIRSTNAME, FIRSTNAMEINHINDI, LASTNAME, MAIDENLASTNAME, NICKNAME, NICKNAMEINHINDI, " +
			"GENDER, BIRTHDAY, BIRTHMONTH, BIRTHYEAR, MARITALSTATUS, WEDDINGDATE, DATEOFDEATH, PHONE, ISPHONEWHATSAPPREGISTERED, EMAIL, " +
			"ADDRESSSAMEASFAMILY, MEMBERADDRESSID, EDUCATIONDETAILS, OCCUPATION, HOBBY, MEMBERSEARCHTEXT, PROFILEIMAGE , PROFILEIMAGETHUMBNAIL , " +
			"IMAGELASTUPDATED , CREATEDBY , CREATEDDATE , LASTUPDATEDBY , LASTUPDATEDDATE  " +
			"from " + BASE_SCHEMA_NAME + "FAMILYMEMBER " +
			"where FAMILYID = ? ";
	
	private static final String SELECT_MEMBER_BY_ID =
			"select  MEMBERID, FAMILYID, LOGONNAME, HEADOFFAMILY, FIRSTNAME, FIRSTNAMEINHINDI, LASTNAME, MAIDENLASTNAME, NICKNAME, NICKNAMEINHINDI, " +
			"GENDER, BIRTHDAY, BIRTHMONTH, BIRTHYEAR, MARITALSTATUS, WEDDINGDATE, DATEOFDEATH, PHONE, ISPHONEWHATSAPPREGISTERED, EMAIL, " +
			"ADDRESSSAMEASFAMILY, MEMBERADDRESSID, EDUCATIONDETAILS, OCCUPATION, HOBBY, MEMBERSEARCHTEXT, PROFILEIMAGE , PROFILEIMAGETHUMBNAIL , " +
			"IMAGELASTUPDATED , CREATEDBY , CREATEDDATE , LASTUPDATEDBY , LASTUPDATEDDATE  " +
			"from " + BASE_SCHEMA_NAME + "FAMILYMEMBER " +
			"where MEMBERID = ? ";

	private static final String SELECT_MEMBER_BY_LOGON_NAME =
			"select  MEMBERID, FAMILYID, LOGONNAME, HEADOFFAMILY, FIRSTNAME, FIRSTNAMEINHINDI, LASTNAME, MAIDENLASTNAME, NICKNAME, NICKNAMEINHINDI, " +
			"GENDER, BIRTHDAY, BIRTHMONTH, BIRTHYEAR, MARITALSTATUS, WEDDINGDATE, DATEOFDEATH, PHONE, ISPHONEWHATSAPPREGISTERED, EMAIL, " +
			"ADDRESSSAMEASFAMILY, MEMBERADDRESSID, EDUCATIONDETAILS, OCCUPATION, HOBBY, MEMBERSEARCHTEXT, PROFILEIMAGE , PROFILEIMAGETHUMBNAIL , " +
			"IMAGELASTUPDATED , CREATEDBY , CREATEDDATE , LASTUPDATEDBY , LASTUPDATEDDATE  " +
			"from " + BASE_SCHEMA_NAME + "FAMILYMEMBER " +
			"where LOGONNAME = ? ";


	private static final String SELECT_MEMBER_BY_EMAIL_ID =
			"select  MEMBERID, FAMILYID, LOGONNAME, HEADOFFAMILY, FIRSTNAME, FIRSTNAMEINHINDI, LASTNAME, MAIDENLASTNAME, NICKNAME, NICKNAMEINHINDI, " +
			"GENDER, BIRTHDAY, BIRTHMONTH, BIRTHYEAR, MARITALSTATUS, WEDDINGDATE, DATEOFDEATH, PHONE, ISPHONEWHATSAPPREGISTERED, EMAIL, " +
			"ADDRESSSAMEASFAMILY, MEMBERADDRESSID, EDUCATIONDETAILS, OCCUPATION, HOBBY, MEMBERSEARCHTEXT, PROFILEIMAGE , PROFILEIMAGETHUMBNAIL , " +
			"IMAGELASTUPDATED , CREATEDBY , CREATEDDATE , LASTUPDATEDBY , LASTUPDATEDDATE  " +
			"from " + BASE_SCHEMA_NAME + "FAMILYMEMBER " +
			"where EMAIL = ? ";

	private static final String SELECT_HEAD_OF_FAMILY_BY_FAMILY_ID =
			"select  MEMBERID, FAMILYID, LOGONNAME, HEADOFFAMILY, FIRSTNAME, FIRSTNAMEINHINDI, LASTNAME, MAIDENLASTNAME, NICKNAME, NICKNAMEINHINDI, " +
			"GENDER, BIRTHDAY, BIRTHMONTH, BIRTHYEAR, MARITALSTATUS, WEDDINGDATE, DATEOFDEATH, PHONE, ISPHONEWHATSAPPREGISTERED, EMAIL, " +
			"ADDRESSSAMEASFAMILY, MEMBERADDRESSID, EDUCATIONDETAILS, OCCUPATION, HOBBY, MEMBERSEARCHTEXT, PROFILEIMAGE , PROFILEIMAGETHUMBNAIL , " +
			"IMAGELASTUPDATED , CREATEDBY , CREATEDDATE , LASTUPDATEDBY , LASTUPDATEDDATE  " +
			"from " + BASE_SCHEMA_NAME + "FAMILYMEMBER " +
			"where FAMILYID = ? and HEADOFFAMILY = true";

	private static final String UPDATE_LOGON_NAME_FOR_MEMBER = "UPDATE "  + BASE_SCHEMA_NAME + "FAMILYMEMBER " +
			"SET LOGONNAME = ? , LASTUPDATEDBY = ? , LASTUPDATEDDATE = ?  " +
			"where MEMBERID = ?";

	private static final String IS_MEMBER_REGISTERED_BY_EMAIL_ID = "SELECT "
			+ " EXISTS (SELECT 1 FROM " + BASE_SCHEMA_NAME + "FAMILYMEMBER WHERE EMAIL = ?)";

	private static final String INSERT_FAMILY_STATEMENT = "INSERT INTO " + BASE_SCHEMA_NAME + "FAMILYMEMBER " +
			"(FAMILYID, LOGONNAME, HEADOFFAMILY, FIRSTNAME, FIRSTNAMEINHINDI, LASTNAME, MAIDENLASTNAME, " +
			"NICKNAME, NICKNAMEINHINDI, GENDER, BIRTHDAY, BIRTHMONTH, BIRTHYEAR, MARITALSTATUS, WEDDINGDATE, DATEOFDEATH, " +
			"PHONE, ISPHONEWHATSAPPREGISTERED, EMAIL, ADDRESSSAMEASFAMILY, MEMBERADDRESSID, " +
			"EDUCATIONDETAILS, OCCUPATION, HOBBY, MEMBERSEARCHTEXT, PROFILEIMAGE, PROFILEIMAGETHUMBNAIL, " +
			"IMAGELASTUPDATED, CREATEDBY, CREATEDDATE, LASTUPDATEDBY, LASTUPDATEDDATE) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String UPDATE_FAMILY_MEMBER_STATEMENT = "UPDATE "  + BASE_SCHEMA_NAME + "FAMILYMEMBER " +
			"SET FAMILYID = ? , LOGONNAME = ? , HEADOFFAMILY = ? , FIRSTNAME = ? , FIRSTNAMEINHINDI = ? , LASTNAME = ?, MAIDENLASTNAME = ?, " +
			"NICKNAME = ?, NICKNAMEINHINDI = ?, GENDER = ?, BIRTHDAY = ?, BIRTHMONTH = ?, BIRTHYEAR = ?, MARITALSTATUS = ?, WEDDINGDATE = ?, DATEOFDEATH = ?," +
			"PHONE = ?, ISPHONEWHATSAPPREGISTERED = ?, EMAIL = ?, ADDRESSSAMEASFAMILY = ? , MEMBERADDRESSID = ?, " +
			"EDUCATIONDETAILS = ? , OCCUPATION = ? , HOBBY = ? , MEMBERSEARCHTEXT = ?, PROFILEIMAGE = ? , PROFILEIMAGETHUMBNAIL = ? , " +
			"IMAGELASTUPDATED = ? , LASTUPDATEDBY = ? , LASTUPDATEDDATE = ?  " +
			"where MEMBERID = ?";


	public List<FamilyMemberEntity> allMembersForFamily(int familyId) {
		return getJdbcTemplate().query(SELECT_ALL_MEMBERS_FOR_FAMILY, new FamilyMemberRowMapper(), familyId);
	}
	
	public FamilyMemberEntity getMemberById(int memberId) {
		List<FamilyMemberEntity> memberList = getJdbcTemplate().query(SELECT_MEMBER_BY_ID, new FamilyMemberRowMapper(), memberId);
		
		if(memberList.isEmpty()) {
			return null;
		}
		if(memberList.size() > 1) {
			throw new RuntimeException("Invalid member id entry");
		}
		return memberList.get(0);
	}

	public FamilyMemberEntity getHeadOfFamilyByFamilyId(int familyId) {
		List<FamilyMemberEntity> memberList = getJdbcTemplate().query(SELECT_HEAD_OF_FAMILY_BY_FAMILY_ID, new FamilyMemberRowMapper(), familyId);

		if(memberList.isEmpty()) {
			return null;
		}
		if(memberList.size() > 1) {
			throw new RuntimeException("Invalid member id entry");
		}
		return memberList.get(0);
	}

	public FamilyMemberEntity getMemberByLogonName(String logonName) {
		List<FamilyMemberEntity> memberList = getJdbcTemplate().query(SELECT_MEMBER_BY_LOGON_NAME, new FamilyMemberRowMapper(), logonName);

		if(memberList.isEmpty()) {
			throw new RuntimeException("No member found with logon name");
		}
		if(memberList.size() > 1) {
			throw new RuntimeException("Logon name is not unique, multiple members found with same logon name");
		}
		return memberList.get(0);
	}

	public FamilyMemberEntity getMemberByEmail(String emailId) {
		List<FamilyMemberEntity> memberList = getJdbcTemplate().query(SELECT_MEMBER_BY_EMAIL_ID, new FamilyMemberRowMapper(), emailId);

		if(memberList.isEmpty()) {
			return null;
		}
		if(memberList.size() > 1) {
			throw new RuntimeException("Email Id is not unique, multiple members found with same email Id");
		}
		return memberList.get(0);
	}

	public void updateMemberLogonName(String logonName, int updatedBy, Date lastUpdatedDate, int memberId) {
		try {
			getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
					PreparedStatement prepareStatement = aCon.prepareStatement(UPDATE_LOGON_NAME_FOR_MEMBER);
					setStringInStatement(prepareStatement, 1, logonName);
					setIntInStatement(prepareStatement, 2, updatedBy);
					setTimestampInStatement(prepareStatement, 3, lastUpdatedDate);
					setIntInStatement(prepareStatement, 4, memberId);
					return prepareStatement;
				}
			});
		} catch (Exception e) {
			throw new InternalServerException("Internal error while processing your request, please try again.", e);
		}
	}

	public boolean isMemberExistsForEmail (String emailId) {
		try {
			return Boolean.TRUE.equals(jdbcTemplate.queryForObject(IS_MEMBER_REGISTERED_BY_EMAIL_ID, Boolean.class, emailId));
		} catch (Exception e) {
			throw new InternalServerException("Internal error while processing your request, please try again.", e);
		}
	}

	public FamilyMemberEntity addFamilyMember(FamilyMemberEntity aFamilyMember) {
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

	private PreparedStatement buildInsertStatement(Connection aConection, FamilyMemberEntity aFamilyMember) throws SQLException {

		PreparedStatement prepareStatement = aConection.prepareStatement(INSERT_FAMILY_STATEMENT, new String[]{"memberid"});
		int columnIndex = 1;
		setIntInStatement(prepareStatement, columnIndex++, aFamilyMember.getFamilyId());
		setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getLogonName());
		setBooleanInStatement(prepareStatement, columnIndex++, aFamilyMember.isHeadOfFamily());
		setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getFirstName());
		setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getFirstNameInHindi());
		setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getLastName());
		setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getMaidenLastName());
		setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getNickName());
		setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getNickNameInHindi());

		setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getGender().name());
		if(aFamilyMember.getBirthDay() == null) {
			setSmallIntInStatement(prepareStatement, columnIndex++, (short)-1);
		} else {
			// If birth day is not set, we set it to -1
			setSmallIntInStatement(prepareStatement, columnIndex++, aFamilyMember.getBirthDay());
		}
		setSmallIntInStatement(prepareStatement, columnIndex++, aFamilyMember.getBirthMonth().getMonthNumber());
		setSmallIntInStatement(prepareStatement, columnIndex++, aFamilyMember.getBirthYear());
		setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getMaritalStatus().name());
		setTimestampInStatement(prepareStatement, columnIndex++, aFamilyMember.getWeddingDate());
		setTimestampInStatement(prepareStatement, columnIndex++, aFamilyMember.getDateOfDeath());

		setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getPhone());
		setBooleanInStatement(prepareStatement, columnIndex++, aFamilyMember.isPhoneWhatsappRegistered());
		if (aFamilyMember.getEmail() != null) {
			setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getEmail().toLowerCase().trim());
		} else {
			setStringInStatement(prepareStatement, columnIndex++, null);
		}

		setBooleanInStatement(prepareStatement, columnIndex++, aFamilyMember.isAddressSameAsFamily());
		setIntInStatement(prepareStatement, columnIndex++, aFamilyMember.getMemberAddressId());

		setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getEducationDetails());
		setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getOccupation());
		setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getHobby());
		setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getMembersearchtext());
		setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getProfileImage());
		setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getProfileImageThumbnail());
		setTimestampInStatement(prepareStatement, columnIndex++, aFamilyMember.getImageLastUpdated());
		setIntInStatement(prepareStatement, columnIndex++, aFamilyMember.getCreatedBy());
		setTimestampInStatement(prepareStatement, columnIndex++, aFamilyMember.getCreatedDate());
		setIntInStatement(prepareStatement, columnIndex++, aFamilyMember.getLastUpdatedBy());
		setTimestampInStatement(prepareStatement, columnIndex++, aFamilyMember.getLastUpdatedDate());
		return prepareStatement;
	}



	public int deleteFamilyMemberById(int familyMemberId) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aConection) throws SQLException {
				String deleteSqlQuery = "DELETE FROM FAMILYMEMBER WHERE MEMBERID = ?";
				PreparedStatement prepareStatement = aConection.prepareStatement(deleteSqlQuery);
				setIntInStatement(prepareStatement, 1, familyMemberId);
				return prepareStatement;
			}
		});

	}

	private PreparedStatement buildUpdateStatement(Connection aConection, FamilyMemberEntity aFamilyMember) throws SQLException {

		PreparedStatement prepareStatement = aConection.prepareStatement(UPDATE_FAMILY_MEMBER_STATEMENT);

		int columnIndex = 1;
		setIntInStatement(prepareStatement, columnIndex++, aFamilyMember.getFamilyId());
		setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getLogonName());
		setBooleanInStatement(prepareStatement, columnIndex++, aFamilyMember.isHeadOfFamily());
		setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getFirstName());
		setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getFirstNameInHindi());
		setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getLastName());
		setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getMaidenLastName());
		setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getNickName());
		setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getNickNameInHindi());

		setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getGender().name());
		if(aFamilyMember.getBirthDay() == null) {
			setSmallIntInStatement(prepareStatement, columnIndex++, (short)-1);
		} else {
			// If birth day is not set, we set it to -1
			setSmallIntInStatement(prepareStatement, columnIndex++, aFamilyMember.getBirthDay());
		}
		setSmallIntInStatement(prepareStatement, columnIndex++, aFamilyMember.getBirthMonth().getMonthNumber());
		setSmallIntInStatement(prepareStatement, columnIndex++, aFamilyMember.getBirthYear());
		setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getMaritalStatus().name());
		setTimestampInStatement(prepareStatement, columnIndex++, aFamilyMember.getWeddingDate());
		setTimestampInStatement(prepareStatement, columnIndex++, aFamilyMember.getDateOfDeath());

		setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getPhone());
		setBooleanInStatement(prepareStatement, columnIndex++, aFamilyMember.isPhoneWhatsappRegistered());
		if (aFamilyMember.getEmail() != null) {
			setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getEmail().toLowerCase().trim());
		} else {
			setStringInStatement(prepareStatement, columnIndex++, null);
		}

		setBooleanInStatement(prepareStatement, columnIndex++, aFamilyMember.isAddressSameAsFamily());
		setIntInStatement(prepareStatement, columnIndex++, aFamilyMember.getMemberAddressId());

		setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getEducationDetails());
		setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getOccupation());
		setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getHobby());
		setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getMembersearchtext());
		setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getProfileImage());
		setStringInStatement(prepareStatement, columnIndex++, aFamilyMember.getProfileImageThumbnail());
		setTimestampInStatement(prepareStatement, columnIndex++, aFamilyMember.getImageLastUpdated());
		setIntInStatement(prepareStatement, columnIndex++, aFamilyMember.getLastUpdatedBy());
		setTimestampInStatement(prepareStatement, columnIndex++, aFamilyMember.getLastUpdatedDate());
		setIntInStatement(prepareStatement, columnIndex++, aFamilyMember.getMemberId());
		return prepareStatement;
	}

	public int updateFamilyMember(FamilyMemberEntity aFamilyMember) throws SQLException {
		return getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection aCon) throws SQLException {
				return buildUpdateStatement(aCon, aFamilyMember);
			}
		});

	}


}
