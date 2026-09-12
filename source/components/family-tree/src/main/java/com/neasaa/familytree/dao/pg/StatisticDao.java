package com.neasaa.familytree.dao.pg;

import com.neasaa.base.app.cache.SimpleCache;
import com.neasaa.base.app.dao.pg.AbstractDao;
import com.neasaa.familytree.operation.family.model.CityFamilyCountDto;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import static com.neasaa.familytree.constants.CacheKeyConstants.CITY_LIST_WITH_FAMILY_COUNT;

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

  private static final String GET_FAMILY_COUNT_BY_CITY =
      "SELECT a.city, a.state, a.country, count(distinct f.familyid) family_count, count(fm.memberid) member_count "
              + "FROM " + BASE_SCHEMA_NAME + "family f "
              + "JOIN " + BASE_SCHEMA_NAME + "address a ON f.addressid = a.addressid "
              + "LEFT JOIN " + BASE_SCHEMA_NAME + "familymember fm ON fm.familyid = f.familyid AND fm.samajid = f.samajid "
              + "WHERE f.samajid = ? AND f.active = true "
              + "GROUP BY a.city, a.state, a.country "
              + "ORDER BY count(distinct f.familyid) desc";

  private static final RowMapper<CityFamilyCountDto> CITY_FAMILY_COUNT_ROW_MAPPER =
      (rs, rowNum) ->
          CityFamilyCountDto.builder()
              .cityName(rs.getString("city"))
              .stateName(rs.getString("state"))
              .country(rs.getString("country"))
              .familyCount(rs.getInt("family_count"))
              .memberCount(rs.getInt("member_count"))
              .build();

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

  public List<CityFamilyCountDto> getFamilyCountByCity(int samajId) {
    List<CityFamilyCountDto> cachedList = (List<CityFamilyCountDto>) SimpleCache.get(CITY_LIST_WITH_FAMILY_COUNT + "-" + samajId, List.class);
    if(cachedList != null) {
      return cachedList;
    }
    return getJdbcTemplate().query(GET_FAMILY_COUNT_BY_CITY, CITY_FAMILY_COUNT_ROW_MAPPER, samajId);
  }
}
