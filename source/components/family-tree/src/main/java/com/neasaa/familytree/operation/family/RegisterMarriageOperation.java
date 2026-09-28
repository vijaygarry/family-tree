package com.neasaa.familytree.operation.family;

import static com.neasaa.familytree.operation.OperationNames.REGISTER_MARRIAGE;
import static com.neasaa.familytree.utils.DataFormatter.parseISODateToLocalDate;
import static com.neasaa.familytree.utils.RelationshipUtils.buildRelationships;

import com.neasaa.base.app.operation.AuditInfo;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.base.app.operation.model.EmptyOperationResponse;
import com.neasaa.familytree.entity.FamilyEntity;
import com.neasaa.familytree.entity.FamilyMemberEntity;
import com.neasaa.familytree.entity.MemberRelationshipEntity;
import com.neasaa.familytree.enums.Gender;
import com.neasaa.familytree.enums.MaritalStatus;
import com.neasaa.familytree.enums.RelationshipType;
import com.neasaa.familytree.operation.family.model.RegisterMarriageRequest;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Log4j2
@Component("RegisterMarriageOperation")
@Scope("prototype")
public class RegisterMarriageOperation
    extends FamilyAbstractOperation<RegisterMarriageRequest, EmptyOperationResponse> {

  @Override
  public String getOperationName() {
    return REGISTER_MARRIAGE;
  }

  @Override
  public void doValidate(RegisterMarriageRequest opRequest) throws OperationException {
    if (opRequest.getHusbandId() <= 0) {
      throw new ValidationException("Invalid husband ID provided.");
    }
    if (opRequest.getHusbandFirstName() == null || opRequest.getHusbandFirstName().isEmpty()) {
      throw new ValidationException("Husband first name is required.");
    }
    if (opRequest.getWifeId() <= 0) {
      throw new ValidationException("Invalid wife ID provided.");
    }
    if (opRequest.getWifeFirstName() == null || opRequest.getWifeFirstName().isEmpty()) {
      throw new ValidationException("Wife first name is required.");
    }
    if (opRequest.getHusbandId() == opRequest.getWifeId()) {
      throw new ValidationException("Husband ID and wife ID cannot be the same.");
    }
    if (opRequest.getWeddingDate() != null && !opRequest.getWeddingDate().isEmpty()) {
      try {
        parseISODateToLocalDate(opRequest.getWeddingDate());
      } catch (DateTimeParseException e) {
        throw new ValidationException(
            "Invalid wedding date format. Expected ISO date format (yyyy-MM-dd).");
      }
    }
  }

  @Override
  public EmptyOperationResponse doExecute(RegisterMarriageRequest opRequest)
      throws OperationException {
    int samajId = getSamajIdFromSession();

    FamilyMemberEntity husband =
        familyMemberDao.getMemberById(samajId, opRequest.getHusbandId());
    if (husband == null) {
      throw new ValidationException("Husband not found for ID: " + opRequest.getHusbandId());
    }
    if (!husband.getFirstName().equalsIgnoreCase(opRequest.getHusbandFirstName())) {
      throw new ValidationException(
          "Husband first name '" + opRequest.getHusbandFirstName()
              + "' does not match member ID " + opRequest.getHusbandId() + ".");
    }
    if (husband.getGender() != Gender.Male) {
      throw new ValidationException(
          "Member " + husband.getFirstName() + " (" + husband.getMemberId()
              + ") is not male. Husband must be male.");
    }

    FamilyMemberEntity wife = familyMemberDao.getMemberById(samajId, opRequest.getWifeId());
    if (wife == null) {
      throw new ValidationException("Wife not found for ID: " + opRequest.getWifeId());
    }
    if (!wife.getFirstName().equalsIgnoreCase(opRequest.getWifeFirstName())) {
      throw new ValidationException(
          "Wife first name '" + opRequest.getWifeFirstName()
              + "' does not match member ID " + opRequest.getWifeId() + ".");
    }
    if (wife.getGender() != Gender.Female) {
      throw new ValidationException(
          "Member " + wife.getFirstName() + " (" + wife.getMemberId()
              + ") is not female. Wife must be female.");
    }

    validateNoConflictingSpouse(husband, wife);
    validateNoConflictingSpouse(wife, husband);

    boolean alreadyMarried = isAlreadyMarried(husband, wife);

    AuditInfo auditInfo = getAuditInfo();

    if (!alreadyMarried) {
      MemberRelationshipEntity wifeRelationship =
          buildRelationships(wife, RelationshipType.Wife, husband, auditInfo);
      memberRelationshipDao.addMemberRelationship(wifeRelationship, auditInfo);
      log.info("Added spouse relationship: {} (wife) and {} (husband)",
          wife.getMemberId(), husband.getMemberId());
    }

    LocalDate weddingLocalDate = null;
    if (opRequest.getWeddingDate() != null && !opRequest.getWeddingDate().isEmpty()) {
      weddingLocalDate = parseISODateToLocalDate(opRequest.getWeddingDate());
    }

    updateMemberMarriageDetails(husband, weddingLocalDate, auditInfo);
    updateMemberMarriageDetails(wife, weddingLocalDate, auditInfo);

    if (wife.getFamilyId() != husband.getFamilyId()) {
      FamilyEntity husbandFamily =
          familyDao.getFamilyByFamilyId(samajId, husband.getFamilyId());
      if (husbandFamily == null) {
        throw new ValidationException(
            "Husband's family not found for family ID: " + husband.getFamilyId());
      }
      familyMemberDao.updateMemberFamilyId(
          samajId, wife.getMemberId(), husband.getFamilyId(),
          husbandFamily.getFamilyName(), auditInfo);
      log.info("Updated wife {} family from {} to husband's family {}",
          wife.getMemberId(), wife.getFamilyId(), husband.getFamilyId());
    }

    String message = String.format(
        "Marriage registered successfully for %s (%d) and %s (%d).",
        husband.getFirstName(), husband.getMemberId(),
        wife.getFirstName(), wife.getMemberId());
    log.info(message);
    return new EmptyOperationResponse(message);
  }

  private void validateNoConflictingSpouse(
      FamilyMemberEntity member, FamilyMemberEntity expectedSpouse) {
    MemberRelationshipEntity existingSpouse =
        memberRelationshipDao.getSpouseForMemberById(member.getMemberId());
    if (existingSpouse != null
        && existingSpouse.getRelatedMemberId() != expectedSpouse.getMemberId()) {
      throw new ValidationException(
          member.getFirstName() + " (" + member.getMemberId()
              + ") already has a different spouse with ID "
              + existingSpouse.getRelatedMemberId() + ".");
    }
  }

  private boolean isAlreadyMarried(FamilyMemberEntity husband, FamilyMemberEntity wife) {
    MemberRelationshipEntity existing =
        memberRelationshipDao.getSpouseForMemberById(husband.getMemberId());
    return existing != null && existing.getRelatedMemberId() == wife.getMemberId();
  }

  private void updateMemberMarriageDetails(
      FamilyMemberEntity member, LocalDate weddingDate, AuditInfo auditInfo) {
    if (weddingDate != null) {
      member.setWeddingDate(weddingDate);
    }
    member.setMaritalStatus(MaritalStatus.Married);
    familyMemberDao.updateFamilyMember(member, auditInfo);
    log.info("Updated marital status to Married for member {}", member.getMemberId());
  }
}
