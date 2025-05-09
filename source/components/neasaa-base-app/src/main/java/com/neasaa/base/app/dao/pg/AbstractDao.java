package com.neasaa.base.app.dao.pg;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

import org.springframework.jdbc.core.JdbcTemplate;

import com.neasaa.util.DateUtils;
import com.neasaa.util.StringUtils;

public class AbstractDao {
	private JdbcTemplate jdbcTemplate;

	public void setJdbcTemplate(JdbcTemplate aJdbcTemplate) {
		this.jdbcTemplate = aJdbcTemplate;
	}

	public JdbcTemplate getJdbcTemplate() {
		return this.jdbcTemplate;
	}

	/**
	 * This method check the specified string value. If specified string is null,
	 * then set null value for specified index else sets specified string at
	 * specified index.
	 * 
	 * @param aPreparedStatement
	 * @param aIndex
	 * @param aStringValue
	 * @throws SQLException
	 */
	public final static void setStringInStatement(PreparedStatement aPreparedStatement, int aIndex, String aStringValue)
			throws SQLException {
		if (aStringValue == null) {
			aPreparedStatement.setNull(aIndex, Types.VARCHAR);
		} else {
			aPreparedStatement.setString(aIndex, aStringValue);
		}
	}

	public final static void setTimestampInStatement(PreparedStatement aPreparedStatement, int aIndex, Date aDateValue)
			throws SQLException {
		if (aDateValue == null) {
			aPreparedStatement.setNull(aIndex, Types.TIMESTAMP);
		} else {
			aPreparedStatement.setTimestamp(aIndex, DateUtils.dateToSqlTimestamp(aDateValue),
					DateUtils.getUtcCalendarInstance());
		}
	}
	
	/**
	 * This method sets big decimal in statement..
	 * 
	 * @param aPreparedStatement
	 * @param aIndex
	 * @param aStringValue
	 * @throws SQLException
	 */
	public final static void setBigDecimalInStatement(PreparedStatement aPreparedStatement, int aIndex, BigDecimal aValue)
			throws SQLException {
		
		aPreparedStatement.setBigDecimal(aIndex, aValue);
		
	}
	
	

	public final static void setLongInStatement(PreparedStatement aPreparedStatement, int aIndex, String aStringValue)
			throws SQLException {
		long longValue = -1l;
		try {
			longValue = Long.parseLong(aStringValue);
		} catch (NumberFormatException e) {
			throw new RuntimeException("Failed to parse long value <" + aStringValue + "> at index " + aIndex
					+ ". Cause " + e.getMessage());
		}
		aPreparedStatement.setLong(aIndex, longValue);
	}

	public final static void setLongInStatement(PreparedStatement aPreparedStatement, int aIndex, long aLongValue)
			throws SQLException {
		aPreparedStatement.setLong(aIndex, aLongValue);
	}

	public final static void setIntInStatement(PreparedStatement aPreparedStatement, int aIndex, String aStringValue)
			throws SQLException {
		int intValue = -1;
		try {
			intValue = Integer.parseInt(aStringValue);
		} catch (NumberFormatException e) {
			throw new RuntimeException("Failed to parse integer value <" + aStringValue + "> at index " + aIndex
					+ ". Cause " + e.getMessage());
		}
		aPreparedStatement.setInt(aIndex, intValue);
	}

	public final static void setIntInStatement(PreparedStatement aPreparedStatement, int aIndex, int aIntValue)
			throws SQLException {
		aPreparedStatement.setInt(aIndex, aIntValue);
	}

	public final static void setSmallIntInStatement(PreparedStatement aPreparedStatement, int aIndex,
			String aStringValue) throws SQLException {
		short smallIntValue = -1;
		try {
			smallIntValue = Short.parseShort(aStringValue);
		} catch (NumberFormatException e) {
			throw new RuntimeException("Failed to parse short (smallint) value <" + aStringValue + "> at index "
					+ aIndex + ". Cause " + e.getMessage());
		}
		aPreparedStatement.setShort(aIndex, smallIntValue);
	}
	
	public final static void setSmallIntInStatement(PreparedStatement aPreparedStatement, int aIndex,
			short aValue) throws SQLException {
		aPreparedStatement.setShort(aIndex, aValue);
	}
	
	

	/**
	 * This method will be used to set float as well as real SQL data type.
	 * 
	 * @param aPreparedStatement
	 * @param aIndex
	 * @param aStringValue
	 * @throws SQLException
	 * @throws SaixException
	 */
	public final static void setFloatInStatement(PreparedStatement aPreparedStatement, int aIndex, String aStringValue)
			throws SQLException {
		float floatValue = -1;
		try {
			floatValue = Float.parseFloat(aStringValue);
		} catch (NumberFormatException e) {
			throw new RuntimeException("Failed to parse float value <" + aStringValue + "> at index " + aIndex
					+ ". Cause " + e.getMessage());
		}
		aPreparedStatement.setFloat(aIndex, floatValue);
	}

	public final static void setDoubleInStatement(PreparedStatement aPreparedStatement, int aIndex, String aStringValue)
			throws SQLException {
		double doubleValue = -1;
		try {
			doubleValue = Double.parseDouble(aStringValue);
		} catch (NumberFormatException e) {
			throw new RuntimeException("Failed to parse double value <" + aStringValue + "> at index " + aIndex
					+ ". Cause " + e.getMessage());
		}
		aPreparedStatement.setDouble(aIndex, doubleValue);
	}

	public final static void setBooleanInStatement(PreparedStatement aPreparedStatement, int aIndex,
			String aStringValue) throws SQLException {
		boolean boolValue = false;
		try {
			boolValue = StringUtils.parseBooleanValue(aStringValue);
		} catch (Exception e) {
			throw new RuntimeException("Failed to parse boolean value <" + aStringValue + "> at index " + aIndex
					+ ". Cause " + e.getMessage());
		}
		aPreparedStatement.setBoolean(aIndex, boolValue);
	}

	public final static void setBooleanInStatement(PreparedStatement aPreparedStatement, int aIndex,
			boolean aBooleanValue) throws SQLException {
		aPreparedStatement.setBoolean(aIndex, aBooleanValue);
	}

	public final static void setBooleanFlagInStatement(PreparedStatement aPreparedStatement, int aIndex,
			boolean aBooleanValue) throws SQLException {
		String flag = (aBooleanValue ? "Y" : "N");
		aPreparedStatement.setString(aIndex, flag);
	}

	public final static boolean getBooleanFlag(ResultSet aResultSet, String aColumnName) throws SQLException {
		String flag = aResultSet.getString(aColumnName);
		if ("Y".equalsIgnoreCase(flag)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method fetches the timestamp from ResultSet for specified column name.
	 * This method uses the UTC calendar instance while getting the timestamp value.
	 * 
	 * @param aResultSet
	 * @param aColumnName
	 * @return
	 * @throws SQLException
	 */
	public final static Date getTimestampFromResultSet(ResultSet aResultSet, String aColumnName) throws SQLException {
		return aResultSet.getTimestamp(aColumnName, DateUtils.getUtcCalendarInstance());
	}
}
