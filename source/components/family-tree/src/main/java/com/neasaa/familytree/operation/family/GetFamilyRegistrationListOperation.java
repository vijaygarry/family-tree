package com.neasaa.familytree.operation.family;

import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.familytree.dao.pg.FamilyRegistrationRequestDao;
import com.neasaa.familytree.entity.FamilyRegistrationRequestEntity;
import com.neasaa.familytree.enums.FamilyRegistrationStatus;
import com.neasaa.familytree.operation.OperationNames;
import com.neasaa.familytree.operation.family.model.FamilyRegistrationDto;
import com.neasaa.familytree.operation.family.model.GetFamilyRegistrationListRequest;
import com.neasaa.familytree.operation.family.model.GetFamilyRegistrationListResponse;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Log4j2
@Component("GetFamilyRegistrationListOperation")
@Scope("prototype")
public class GetFamilyRegistrationListOperation
    extends FamilyAbstractOperation<GetFamilyRegistrationListRequest, GetFamilyRegistrationListResponse> {

  @Autowired private FamilyRegistrationRequestDao familyRegistrationRequestDao;

  @Override
  public String getOperationName() {
    return OperationNames.GET_FAMILY_REGISTRATION_LIST;
  }

  @Override
  public void doValidate(GetFamilyRegistrationListRequest opRequest) throws OperationException {
    if (opRequest == null) {
      throw new ValidationException("Invalid request provided.");
    }
    if (opRequest.getStatus() != null && !opRequest.getStatus().isEmpty()) {
      FamilyRegistrationStatus resolvedStatus =
          FamilyRegistrationStatus.getStatusByString(opRequest.getStatus());
      if (resolvedStatus == null) {
        throw new ValidationException("Invalid status value: " + opRequest.getStatus()
            + ". Allowed values: PENDING, PROCESSED, DUPLICATE, INVALID.");
      }
    }
  }

  @Override
  public GetFamilyRegistrationListResponse doExecute(GetFamilyRegistrationListRequest opRequest)
      throws OperationException {
    List<FamilyRegistrationRequestEntity> entities;

    if (opRequest.getStatus() != null && !opRequest.getStatus().isEmpty()) {
      FamilyRegistrationStatus status =
          FamilyRegistrationStatus.getStatusByString(opRequest.getStatus());
      log.info("Fetching family registrations with status={}", status);
      entities = familyRegistrationRequestDao.getFamilyRegistrationRequestsByStatus(status);
    } else {
      log.info("Fetching all family registrations");
      entities = familyRegistrationRequestDao.getAllFamilyRegistrationRequests();
    }

    GetFamilyRegistrationListResponse response = new GetFamilyRegistrationListResponse();
    response.setRegistrations(entities.stream().map(FamilyRegistrationDto::fromEntity).toList());
    response.setTotalCount(entities.size());
    return response;
  }
}
