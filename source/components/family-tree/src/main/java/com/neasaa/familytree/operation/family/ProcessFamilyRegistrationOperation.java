package com.neasaa.familytree.operation.family;

import static com.neasaa.base.app.utils.ValidationUtils.checkObjectPresent;
import static com.neasaa.base.app.utils.ValidationUtils.checkValueRange;

import com.neasaa.base.app.operation.AuditInfo;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.familytree.dao.pg.FamilyRegistrationRequestDao;
import com.neasaa.familytree.entity.FamilyRegistrationRequestEntity;
import com.neasaa.familytree.enums.FamilyRegistrationStatus;
import com.neasaa.familytree.operation.OperationNames;
import com.neasaa.familytree.operation.family.model.ProcessFamilyRegistrationRequest;
import com.neasaa.familytree.operation.family.model.ProcessFamilyRegistrationResponse;

import java.util.Arrays;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Log4j2
@Component("ProcessFamilyRegistrationOperation")
@Scope("prototype")
public class ProcessFamilyRegistrationOperation extends FamilyAbstractOperation<ProcessFamilyRegistrationRequest, ProcessFamilyRegistrationResponse> {

  @Autowired
  private FamilyRegistrationRequestDao familyRegistrationRequestDao;

  @Autowired
  private FamilyRegistrationApprovalUtil familyRegistrationApprovalUtil;

  @Override
  public String getOperationName() {
    return OperationNames.PROCESS_FAMILY_REGISTRATION;
  }

  @Override
  public void doValidate(ProcessFamilyRegistrationRequest opRequest) throws OperationException {
    if (opRequest == null) {
      throw new ValidationException("Invalid request provided.");
    }
    checkValueRange(opRequest.getFamilyRegistrationId(), 1, Integer.MAX_VALUE, "family registration id");
    checkObjectPresent(opRequest.getAction(), "action");
    RegistrationAction action = RegistrationAction.getActionByString(opRequest.getAction());
    if (action == null) {
      throw new ValidationException("Invalid action provided. Allowed values are: " + Arrays.toString(RegistrationAction.values()));
    }
  }

  @Override
  public ProcessFamilyRegistrationResponse doExecute(ProcessFamilyRegistrationRequest opRequest) throws OperationException {
    log.info("Processing family registration request with id: {} and action: {}",
        opRequest.getFamilyRegistrationId(), opRequest.getAction());

    FamilyRegistrationRequestEntity familyRequest = familyRegistrationRequestDao.getFamilyRegistrationRequestById(opRequest.getFamilyRegistrationId());
    if (familyRequest == null) {
      throw new ValidationException("Family RegistrationRequest not found");
    }

    if (familyRequest.getStatus() != FamilyRegistrationStatus.PENDING) {
      throw new ValidationException("Family registration request already processed with status: " + familyRequest.getStatus());
    }

    AuditInfo auditInfo = getAuditInfo();
    RegistrationAction action = RegistrationAction.getActionByString(opRequest.getAction());
    switch (action) {
      case APPROVE:
        return familyRegistrationApprovalUtil.approveRegistration(familyRequest, auditInfo);
      case DENY:
        return updateRegistrationStatus(familyRequest, FamilyRegistrationStatus.INVALID, "Registration request denied");
      case DUPLICATE:
        return updateRegistrationStatus(familyRequest, FamilyRegistrationStatus.DUPLICATE, "Registration request marked as duplicate");
      case INVALID:
        return updateRegistrationStatus(familyRequest, FamilyRegistrationStatus.INVALID, "Registration request marked as invalid");
      default:
        throw new ValidationException("Invalid action provided");
    }
  }

  private ProcessFamilyRegistrationResponse updateRegistrationStatus(
      FamilyRegistrationRequestEntity familyRequest,
      FamilyRegistrationStatus status,
      String message) {
    familyRequest.setStatus(status);
    AuditInfo auditInfo = getAuditInfo();
    familyRegistrationRequestDao.updateFamilyRegistrationRequest(familyRequest, auditInfo);
    log.info("Family registration request {} updated to status: {}",
        familyRequest.getFamilyRequestId(), status);

    return ProcessFamilyRegistrationResponse.builder()
        .familyRegistrationId(familyRequest.getFamilyRequestId())
        .message(message)
        .build();
  }

  public enum RegistrationAction {
    APPROVE,
    DENY,
    DUPLICATE,
    INVALID;

    public static RegistrationAction getActionByString(String input) {
      for (RegistrationAction action : RegistrationAction.values()) {
        if (action.name().equalsIgnoreCase(input)) {
          return action;
        }
      }
      return null;
    }
  }
}
