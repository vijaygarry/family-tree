package com.neasaa.familytree.operation.family;

import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.familytree.entity.FamilyMemberEntity;
import com.neasaa.familytree.enums.Gender;
import com.neasaa.familytree.enums.MaritalStatus;
import com.neasaa.familytree.operation.OperationNames;
import com.neasaa.familytree.operation.family.model.SearchFamilyMemberRequest;
import com.neasaa.familytree.operation.family.model.SearchFamilyMemberResponse;
import com.neasaa.familytree.operation.family.model.SearchMemberDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.neasaa.base.app.utils.ValidationUtils.checkValueRange;

@Log4j2
@Component("SearchFamilyMemberOperation")
@Scope("prototype")
public class SearchFamilyMemberOperation extends FamilyAbstractOperation<SearchFamilyMemberRequest, SearchFamilyMemberResponse> {

  @Override
  public String getOperationName() {
    return OperationNames.SEARCH_FAMILY_MEMBER;
  }

  @Override
  public void doValidate(SearchFamilyMemberRequest opRequest) {
      if (opRequest == null) {
          throw new ValidationException("Invalid request provided.");
      }
      opRequest.trimFields();

      if (opRequest.getGender() != null && !opRequest.getGender().isEmpty()) {
          if (Gender.getGenderByString(opRequest.getGender()) == null) {
              throw new ValidationException("Invalid value for field gender");
          }
      }

      if (opRequest.getMaritalStatus() != null && !opRequest.getMaritalStatus().isEmpty()) {
          if (MaritalStatus.getMaritalStatus(opRequest.getMaritalStatus()) == null) {
              throw new ValidationException("Invalid value for field marital status");
          }
      }

      if (opRequest.getAgeFrom() != null) {
          checkValueRange(opRequest.getAgeFrom(), 0, 100, "age from");
      }
      if (opRequest.getAgeTo() != null) {
          checkValueRange(opRequest.getAgeTo(), 1, 150, "age to");
      }
      if (opRequest.getAgeFrom() != null && opRequest.getAgeTo() != null) {
          if (opRequest.getAgeFrom() > opRequest.getAgeTo()) {
              throw new ValidationException("Age from cannot be greater than age to");
          }
      }
  }

  @Override
  public SearchFamilyMemberResponse doExecute(SearchFamilyMemberRequest opRequest) {
    log.info("Searching family members with criteria: {}", opRequest.getSearchString());
      int samajId = getSamajIdFromSession();
      Gender gender = null;
      if(opRequest.getGender() != null && !opRequest.getGender().isEmpty()) {
          gender = Gender.getGenderByString(opRequest.getGender());
      }

      MaritalStatus maritalStatus = null;
      if(opRequest.getMaritalStatus() != null && !opRequest.getMaritalStatus().isEmpty()) {
          maritalStatus = MaritalStatus.getMaritalStatus(opRequest.getMaritalStatus());
      }

      List<FamilyMemberEntity> members = familyMemberDao.searchFamilyMember(
              samajId, opRequest.getSearchString(), gender, maritalStatus,
              opRequest.getAgeFrom(), opRequest.getAgeTo());
      SearchFamilyMemberResponse response = new SearchFamilyMemberResponse();
      if (members == null || members.isEmpty()) {
          response.setOperationMessage("No family member for selected search criteria");
          return response;
      }
      List<SearchMemberDto> memberDtos =
              members.stream().map(SearchMemberDto::getSearchMemberDtoFromEntity).toList();
      response.setMembers(memberDtos);
      return response;
  }
}