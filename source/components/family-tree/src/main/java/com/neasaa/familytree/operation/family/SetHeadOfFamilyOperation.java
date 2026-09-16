package com.neasaa.familytree.operation.family;

import static com.neasaa.base.app.utils.ValidationUtils.checkObjectPresent;
import static com.neasaa.base.app.utils.ValidationUtils.checkValuePresent;
import static com.neasaa.base.app.utils.ValidationUtils.checkValueRange;
import static com.neasaa.familytree.operation.OperationNames.SET_HEAD_OF_FAMILY;

import com.neasaa.base.app.operation.exception.AccessDeniedException;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.base.app.operation.model.EmptyOperationResponse;
import com.neasaa.familytree.entity.FamilyEntity;
import com.neasaa.familytree.entity.FamilyMemberEntity;
import com.neasaa.familytree.operation.family.model.SetHeadOfFamilyRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Log4j2
@Component("SetHeadOfFamilyOperation")
@Scope("prototype")
public class SetHeadOfFamilyOperation
    extends FamilyAbstractOperation<SetHeadOfFamilyRequest, EmptyOperationResponse> {

  @Override
  public String getOperationName() {
    return SET_HEAD_OF_FAMILY;
  }

  @Override
  public void doValidate(SetHeadOfFamilyRequest opRequest) throws OperationException {
    if (opRequest == null) {
      throw new ValidationException("Invalid request provided.");
    }
    checkObjectPresent(opRequest.getFamilyId(), "family Id");
    checkValueRange(opRequest.getFamilyId(), 1, Integer.MAX_VALUE, "family Id");
    checkValuePresent(opRequest.getFamilyName(), "family name");
    checkObjectPresent(opRequest.getMemberId(), "member Id");
    checkValueRange(opRequest.getMemberId(), 1, Integer.MAX_VALUE, "member Id");
    checkValuePresent(opRequest.getMemberName(), "member name");
  }

  @Override
  public EmptyOperationResponse doExecute(SetHeadOfFamilyRequest opRequest) throws OperationException {
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

    FamilyEntity familyEntityFromDb = familyDao.getFamilyByFamilyId(samajId, opRequest.getFamilyId());
    if (familyEntityFromDb == null) {
      log.error("Family with id {} not found.", opRequest.getFamilyId());
      throw new ValidationException("No family found matching the provided details.");
    }

    if (!familyEntityFromDb.getFamilyName().equalsIgnoreCase(opRequest.getFamilyName())) {
      log.error("Family name mismatch for family id {}.", opRequest.getFamilyId());
      throw new ValidationException("No family found matching the provided details.");
    }

    if (memberEntityFromDb.isHeadOfFamily()) {
      log.info("Member {} is already the head of family {}.", opRequest.getMemberId(), opRequest.getFamilyId());
      throw new ValidationException("Member is already a head of family.");
    }

    FamilyMemberEntity currentHeadOfFamily = familyMemberDao.getHeadOfFamilyByFamilyId(samajId, opRequest.getFamilyId());
    if (currentHeadOfFamily != null) {
      FamilyMemberEntity updatedCurrentHead = currentHeadOfFamily.copyOf();
      updatedCurrentHead.setHeadOfFamily(false);
      familyMemberDao.updateFamilyMember(updatedCurrentHead, getAuditInfo());
      log.info("Unset head of family for member {}.", currentHeadOfFamily.getMemberId());
    }

    FamilyMemberEntity updatedMember = memberEntityFromDb.copyOf();
    updatedMember.setHeadOfFamily(true);
    familyMemberDao.updateFamilyMember(updatedMember, getAuditInfo());
    log.info("Member {} set as head of family {}.", opRequest.getMemberId(), opRequest.getFamilyId());

    return new EmptyOperationResponse(
        String.format("Member %s has been set as head of family.", memberEntityFromDb.getFirstName()));
  }
}
