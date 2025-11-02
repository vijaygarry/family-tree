package com.neasaa.familytree.operation.family;

import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.familytree.entity.SearchFamilyEntity;
import com.neasaa.familytree.operation.OperationNames;
import com.neasaa.familytree.operation.family.model.SearchFamilyDto;
import com.neasaa.familytree.operation.family.model.SearchFamilyRequest;
import com.neasaa.familytree.operation.family.model.SearchFamilyResponse;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Log4j2
@Component("SearchFamilyOperation")
@Scope("prototype")
public class SearchFamilyOperation
    extends FamilyAbstractOperation<SearchFamilyRequest, SearchFamilyResponse> {

  @Override
  public String getOperationName() {
    return OperationNames.SEARCH_FAMILY;
  }

  @Override
  public void doValidate(SearchFamilyRequest opRequest) throws OperationException {
    if (opRequest == null) {
      throw new ValidationException("Invalid request provided.");
    }
  }

  @Override
  public SearchFamilyResponse doExecute(SearchFamilyRequest opRequest) throws OperationException {
    log.info("Searching family{}", opRequest.getSearchString());
    int samajId = getSamajIdFromSession();
    List<SearchFamilyEntity> families = familyDao.searchFamily(samajId, opRequest.getSearchString());
    SearchFamilyResponse response = new SearchFamilyResponse();
    if (families == null || families.isEmpty()) {
      response.setOperationMessage(
          String.format(
              "No family found for selected search criteria: %s", opRequest.getSearchString()));
      return response;
    }
    List<SearchFamilyDto> familyDtos =
        families.stream().map(SearchFamilyDto::getSearchFamilyDtoFromEntity).toList();
    response.setFamilies(familyDtos);
    return response;
  }
}
