package com.neasaa.familytree.dao.pg;

import com.neasaa.base.app.dao.pg.AbstractDao;
import org.springframework.stereotype.Repository;

@Repository
public class StatisticDao extends AbstractDao {

  private static final String COUNT_REGISTERED_FAMILIES =
      "SELECT COUNT(*) FROM " + BASE_SCHEMA_NAME + "FAMILY WHERE SAMAJID = ? AND ACTIVE = true";

  private static final String COUNT_REGISTERED_MEMBERS =
      "SELECT COUNT(*) FROM " + BASE_SCHEMA_NAME + "FAMILYMEMBER WHERE SAMAJID = ?";

  private static final String COUNT_REGISTERED_USERS =
      "SELECT COUNT(*) FROM "
          + BASE_SCHEMA_NAME
          + "APPUSER";

  private static final String COUNT_MALES =
      "SELECT COUNT(*) FROM "
          + BASE_SCHEMA_NAME
          + "FAMILYMEMBER WHERE SAMAJID = ? AND GENDER = 'Male'";

  private static final String COUNT_FEMALES =
      "SELECT COUNT(*) FROM "
          + BASE_SCHEMA_NAME
          + "FAMILYMEMBER WHERE SAMAJID = ? AND GENDER = 'Female'";

  private static final String COUNT_KIDS =
      "SELECT COUNT(*) FROM "
          + BASE_SCHEMA_NAME
          + "FAMILYMEMBER WHERE SAMAJID = ? AND " +
              " make_date(birthyear, birthmonth, CASE WHEN birthday > 0 THEN birthday ELSE 1 END) > " +
              " (CURRENT_DATE - INTERVAL '20 years')";

  private static final String COUNT_SINGLE_GIRLS =
      "SELECT COUNT(*) FROM "
          + BASE_SCHEMA_NAME
          + "FAMILYMEMBER WHERE SAMAJID = ? AND GENDER = 'Female' AND MARITALSTATUS = 'Single' " +
              "AND make_date(birthyear, birthmonth, CASE WHEN birthday > 0 THEN birthday ELSE 1 END) < " +
              "(CURRENT_DATE - INTERVAL '20 years')";

  private static final String COUNT_SINGLE_BOYS =
      "SELECT COUNT(*) FROM "
          + BASE_SCHEMA_NAME
          + "FAMILYMEMBER WHERE SAMAJID = ? AND GENDER = 'Male' AND MARITALSTATUS = 'Single' " +
              "AND make_date(birthyear, birthmonth, CASE WHEN birthday > 0 THEN birthday ELSE 1 END) < " +
              "(CURRENT_DATE - INTERVAL '20 years')";

  public int getRegisteredFamiliesCount(int samajId) {
    return getJdbcTemplate().queryForObject(COUNT_REGISTERED_FAMILIES, Integer.class, samajId);
  }

  public int getRegisteredMembersCount(int samajId) {
    return getJdbcTemplate().queryForObject(COUNT_REGISTERED_MEMBERS, Integer.class, samajId);
  }

  public int getRegisteredUsersCount(int samajId) {
    return getJdbcTemplate().queryForObject(COUNT_REGISTERED_USERS, Integer.class);
  }

  public int getMalesCount(int samajId) {
    return getJdbcTemplate().queryForObject(COUNT_MALES, Integer.class, samajId);
  }

  public int getFemalesCount(int samajId) {
    return getJdbcTemplate().queryForObject(COUNT_FEMALES, Integer.class, samajId);
  }

  public int getKidsCount(int samajId) {
    return getJdbcTemplate().queryForObject(COUNT_KIDS, Integer.class, samajId);
  }

  public int getSingleGirlsCount(int samajId) {
    return getJdbcTemplate().queryForObject(COUNT_SINGLE_GIRLS, Integer.class, samajId);
  }

  public int getSingleBoysCount(int samajId) {
    return getJdbcTemplate().queryForObject(COUNT_SINGLE_BOYS, Integer.class, samajId);
  }
}
