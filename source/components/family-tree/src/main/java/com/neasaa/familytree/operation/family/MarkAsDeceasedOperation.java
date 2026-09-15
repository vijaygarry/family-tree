package com.neasaa.familytree.operation.family;

import static com.neasaa.base.app.utils.ValidationUtils.checkObjectPresent;
import static com.neasaa.base.app.utils.ValidationUtils.checkValuePresent;
import static com.neasaa.base.app.utils.ValidationUtils.checkValueRange;
import static com.neasaa.familytree.operation.OperationNames.MARK_AS_DECEASED;
import static com.neasaa.familytree.utils.DataFormatter.parseISODateToLocalDate;

import com.neasaa.base.app.operation.exception.AccessDeniedException;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.base.app.operation.model.EmptyOperationResponse;
import com.neasaa.familytree.entity.FamilyMemberEntity;
import com.neasaa.familytree.operation.family.model.MarkAsDeceasedRequest;
import com.neasaa.familytree.utils.FamilytreeValidationUtils;
import java.time.LocalDate;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Log4j2
@Component("MarkAsDeceasedOperation")
@Scope("prototype")
public class MarkAsDeceasedOperation
    extends FamilyAbstractOperation<MarkAsDeceasedRequest, EmptyOperationResponse> {

  @Override
  public String getOperationName() {
    return MARK_AS_DECEASED;
  }

  @Override
  public void doValidate(MarkAsDeceasedRequest opRequest) throws OperationException {
    if (opRequest == null) {
      throw new ValidationException("Invalid request provided.");
    }
    checkObjectPresent(opRequest.getMemberId(), "member Id");
    checkValueRange(opRequest.getMemberId(), 1, Integer.MAX_VALUE, "member Id");

    checkObjectPresent(opRequest.getFamilyId(), "family Id");
    checkValueRange(opRequest.getFamilyId(), 1, Integer.MAX_VALUE, "family Id");

    checkValuePresent(opRequest.getMemberName(), "member name");
    
    checkValuePresent(opRequest.getDateOfDeath(), "date of death");
    LocalDate dateOfDeath = parseISODateToLocalDate(opRequest.getDateOfDeath());
    if (dateOfDeath == null) {
      throw new ValidationException("Invalid date of death format. Expected ISO date format (YYYY-MM-DD).");
    }
    if (dateOfDeath.isAfter(LocalDate.now())) {
      throw new ValidationException("Date of death cannot be a future date.");
    }
  }

  @Override
  public EmptyOperationResponse doExecute(MarkAsDeceasedRequest opRequest) throws OperationException {
    int samajId = getSamajIdFromSession();

    FamilyMemberEntity memberEntityFromDb = familyMemberDao.getMemberById(samajId, opRequest.getMemberId());
    if (memberEntityFromDb == null) {
      log.error("Member with id {} not found.", opRequest.getMemberId());
      throw new ValidationException("No member found matching the provided details.");
    }

    if (memberEntityFromDb.getFamilyId() != opRequest.getFamilyId()) {
      log.error("Member {} does not belong to family {}.", opRequest.getMemberId(), opRequest.getFamilyId());
      throw new ValidationException("No member found matching the provided details.");
    }

    if (!memberEntityFromDb.getFirstName().equalsIgnoreCase(opRequest.getMemberName())) {
      log.error("Member name mismatch for member id {}.", opRequest.getMemberId());
      throw new ValidationException("No member found matching the provided details.");
    }
    
    // To update the member details, we need to check if the logged-in user has permission to update this member.
    if (!canLoggedInUserUpdateMember(memberEntityFromDb.getFamilyId())) {
      log.info("User is not allowed to mark member {} as deceased.", opRequest.getMemberId());
      throw new AccessDeniedException("You are not allowed to update details of this member.");
    }

    if (memberEntityFromDb.isHeadOfFamily()) {
      log.info("Member {} is the head of family, cannot be marked as deceased.", opRequest.getMemberId());
      throw new ValidationException("Head of family cannot be marked as deceased.");
    }

    FamilyMemberEntity updatedMember = memberEntityFromDb.copyOf();
    updatedMember.setDateOfDeath(parseISODateToLocalDate(opRequest.getDateOfDeath()));

    familyMemberDao.updateFamilyMember(updatedMember, getAuditInfo());
    log.info("Member {} marked as deceased.", opRequest.getMemberId());

    return new EmptyOperationResponse(
        String.format("Member %s has been marked as deceased.", memberEntityFromDb.getFirstName()));
  }
}
