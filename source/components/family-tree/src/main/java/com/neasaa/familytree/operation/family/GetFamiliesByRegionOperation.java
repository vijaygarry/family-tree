package com.neasaa.familytree.operation.family;

import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.familytree.entity.SearchFamilyEntity;
import com.neasaa.familytree.operation.OperationNames;
import com.neasaa.familytree.operation.family.model.GetFamiliesByRegionRequest;
import com.neasaa.familytree.operation.family.model.GetFamiliesByRegionResponse;
import com.neasaa.familytree.operation.family.model.SearchFamilyDto;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Log4j2
@Component("GetFamiliesByRegionOperation")
@Scope("prototype")
public class GetFamiliesByRegionOperation
    extends FamilyAbstractOperation<GetFamiliesByRegionRequest, GetFamiliesByRegionResponse> {

  @Override
  public String getOperationName() {
    return OperationNames.GET_FAMILIES_BY_REGION;
  }

  @Override
  public void doValidate(GetFamiliesByRegionRequest opRequest) throws OperationException {
    if (opRequest == null) {
      throw new ValidationException("Invalid request provided.");
    }
    if (isBlank(opRequest.getCity()) && isBlank(opRequest.getState()) && isBlank(opRequest.getCountry())) {
      throw new ValidationException("At least one of city, state, or country must be provided.");
    }
  }

  @Override
  public GetFamiliesByRegionResponse doExecute(GetFamiliesByRegionRequest opRequest)
      throws OperationException {
    int samajId = getSamajIdFromSession();
    log.info(
        "Fetching families by region for samajId={}, city={}, state={}, country={}",
        samajId, opRequest.getCity(), opRequest.getState(), opRequest.getCountry());

    List<SearchFamilyEntity> families =
        familyDao.getFamiliesByRegion(
            samajId, opRequest.getCity(), opRequest.getState(), opRequest.getCountry());

    GetFamiliesByRegionResponse response = new GetFamiliesByRegionResponse();
    response.setFamilies(families.stream().map(SearchFamilyDto::getSearchFamilyDtoFromEntity).toList());
    response.setTotalCount(families.size());
    return response;
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}
