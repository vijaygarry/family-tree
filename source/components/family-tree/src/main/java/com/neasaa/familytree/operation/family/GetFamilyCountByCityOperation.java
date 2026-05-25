package com.neasaa.familytree.operation.family;

import com.neasaa.base.app.cache.SimpleCache;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.model.EmptyOperationRequest;
import com.neasaa.familytree.dao.pg.StatisticDao;
import com.neasaa.familytree.operation.OperationNames;
import com.neasaa.familytree.operation.family.model.CityFamilyCountDto;
import com.neasaa.familytree.operation.family.model.GetFamilyCountByCityResponse;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.neasaa.familytree.constants.CacheKeyConstants.SAMAJ_STATISTICS;

@Log4j2
@Component("GetFamilyCountByCityOperation")
@Scope("prototype")
public class GetFamilyCountByCityOperation
    extends FamilyAbstractOperation<EmptyOperationRequest, GetFamilyCountByCityResponse> {

  @Autowired
  protected StatisticDao statisticDao;

  @Override
  public String getOperationName() {
    return OperationNames.GET_FAMILY_COUNT_BY_CITY;
  }

  @Override
  public void doValidate(EmptyOperationRequest opRequest) throws OperationException {
  }

  @Override
  public GetFamilyCountByCityResponse doExecute(EmptyOperationRequest opRequest)
      throws OperationException {
    int samajId = getSamajIdFromSession();
    log.info("Fetching family count by city for samajId {}.", samajId);
    List<CityFamilyCountDto> cachedList = (List<CityFamilyCountDto>) SimpleCache.get(SAMAJ_STATISTICS, List.class);
    if(cachedList == null) {
      log.info("No cached data found for samaj statistics.");
      cachedList = statisticDao.getFamilyCountByCity(samajId);
      SimpleCache.put(SAMAJ_STATISTICS, cachedList);
    }
    log.info("Found {} cities with families.", cachedList.size());

    return GetFamilyCountByCityResponse.builder().cityFamilyCounts(cachedList).build();
  }
}
