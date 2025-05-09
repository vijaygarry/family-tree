/*
* Copyright (c) 2018- 2021
*/

package com.neasaa.base.app.dao.pg;

import java.sql.SQLException;
import com.neasaa.base.app.entity.AppUser;
import java.sql.ResultSet;
import org.springframework.jdbc.core.RowMapper;

public class AppUserRowMapper implements RowMapper<AppUser> {

	@Override
	public AppUser mapRow(ResultSet aRs, int aRowNum) throws SQLException {
		AppUser appUser = new AppUser();
		appUser.setUserId(aRs.getInt("USERID"));
		appUser.setLogonName(aRs.getString("LOGONNAME"));
		appUser.setHashPassword(aRs.getString("HASHPASSWORD"));
		appUser.setFirstName(aRs.getString("FIRSTNAME"));
		appUser.setLastName(aRs.getString("LASTNAME"));
		appUser.setEmailId(aRs.getString("EMAILID"));
		appUser.setAuthenticationType(aRs.getString("AUTHENTICATIONTYPE"));
		appUser.setSingleSignonId(aRs.getString("SINGLESIGNONID"));
		appUser.setInvalidLoginAttempts(aRs.getInt("INVALIDLOGINATTEMPTS"));
		appUser.setLastLoginTime(AbstractDao.getTimestampFromResultSet(aRs, "LASTLOGINTIME"));
		appUser.setLastPasswordChangeTime(AbstractDao.getTimestampFromResultSet(aRs, "LASTPASSWORDCHANGETIME"));
		appUser.setStatus(aRs.getString("STATUS"));
		appUser.setCreatedBy(aRs.getInt("CREATEDBY"));
		appUser.setCreatedDate(AbstractDao.getTimestampFromResultSet(aRs, "CREATEDDATE"));
		appUser.setLastUpdatedBy(aRs.getInt("LASTUPDATEDBY"));
		appUser.setLastUpdatedDate(AbstractDao.getTimestampFromResultSet(aRs, "LASTUPDATEDDATE"));
		return appUser;
	}

}
