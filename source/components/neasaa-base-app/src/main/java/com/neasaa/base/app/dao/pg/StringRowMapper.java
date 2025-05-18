package com.neasaa.base.app.dao.pg;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class StringRowMapper implements RowMapper<String> {
	
	@Override
	public String mapRow(ResultSet aRs, int aRowNum) throws SQLException {
		return aRs.getString(1);
	}
}
