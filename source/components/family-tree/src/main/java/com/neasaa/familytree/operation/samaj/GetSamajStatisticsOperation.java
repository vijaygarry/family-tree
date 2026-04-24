package com.neasaa.familytree.operation.samaj;

import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.familytree.dao.pg.StatisticDao;
import com.neasaa.familytree.operation.OperationNames;
import com.neasaa.familytree.operation.samaj.model.GetSamajStatisticsRequest;
import com.neasaa.familytree.operation.samaj.model.GetSamajStatisticsResponse;
import com.neasaa.familytree.operation.samaj.model.StatisticDto;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Log4j2
@Component("GetSamajStatisticsOperation")
@Scope("prototype")
public class GetSamajStatisticsOperation
    extends AbstractOperation<GetSamajStatisticsRequest, GetSamajStatisticsResponse> {

  @Autowired
  protected StatisticDao statisticDao;

  @Override
  public String getOperationName() {
    return OperationNames.GET_SAMAJ_STATISTICS;
  }

  @Override
  public void doValidate(GetSamajStatisticsRequest opRequest) throws OperationException {
    if (opRequest != null && opRequest.getSamajId() != null) {
      if (opRequest.getSamajId() < 1) {
        throw new ValidationException("Invalid samaj id provided.");
      }
    } else {
      throw new ValidationException("Samaj id is required.");
    }
  }

  @Override
  public GetSamajStatisticsResponse doExecute(GetSamajStatisticsRequest opRequest)
      throws OperationException {
    int samajId = opRequest.getSamajId();

    log.info("Fetching samaj statistics for samajId {}.", samajId);

    List<StatisticDto> statistics = new ArrayList<>();

    statistics.add(
        StatisticDto.builder()
            .label("Number of registered families")
            .key("registered_families")
            .value(statisticDao.getRegisteredFamiliesCount(samajId))
            .build());

    statistics.add(
        StatisticDto.builder()
            .label("Number of registered Members")
            .key("registered_members")
            .value(statisticDao.getRegisteredMembersCount(samajId))
            .build());

    statistics.add(
        StatisticDto.builder()
            .label("Registered Users")
            .key("registered_users")
            .value(statisticDao.getRegisteredUsersCount(samajId))
            .build());

    statistics.add(
        StatisticDto.builder()
            .label("Number of Males")
            .key("males")
            .value(statisticDao.getMalesCount(samajId))
            .build());

    statistics.add(
        StatisticDto.builder()
            .label("Number of Females")
            .key("females")
            .value(statisticDao.getFemalesCount(samajId))
            .build());

    statistics.add(
        StatisticDto.builder()
            .label("Kids (Under 20)")
            .key("kids")
            .value(statisticDao.getKidsCount(samajId))
            .build());

    statistics.add(
        StatisticDto.builder()
            .label("Single Girls (Above 20)")
            .key("single_girls")
            .value(statisticDao.getSingleGirlsCount(samajId))
            .build());

    statistics.add(
        StatisticDto.builder()
            .label("Single Boys (Above 20)")
            .key("single_boys")
            .value(statisticDao.getSingleBoysCount(samajId))
            .build());

    return GetSamajStatisticsResponse.builder().statistics(statistics).build();
  }
}
