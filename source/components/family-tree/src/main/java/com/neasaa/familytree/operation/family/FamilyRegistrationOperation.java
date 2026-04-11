package com.neasaa.familytree.operation.family;

import static com.neasaa.base.app.operation.BeanNames.APP_EMAIL_SENDER;
import static com.neasaa.base.app.utils.ValidationUtils.checkObjectPresent;
import static com.neasaa.base.app.utils.ValidationUtils.checkValuePresent;
import static com.neasaa.familytree.utils.Constants.CHHIPA_SAMAJ_ID;
import static com.neasaa.familytree.utils.Constants.MISSING_BIRTH_DATE_VALUE;
import static com.neasaa.familytree.utils.FamilytreeValidationUtils.validateBirthDate;

import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.base.app.utils.AppProperties;
import com.neasaa.base.app.utils.EmailValidator;
import com.neasaa.base.app.utils.email.EmailMessage;
import com.neasaa.base.app.utils.email.EmailSender;
import com.neasaa.familytree.dao.pg.FamilyRegistrationRequestDao;
import com.neasaa.familytree.entity.FamilyMemberRegistrationEntity;
import com.neasaa.familytree.entity.FamilyRegistrationRequestEntity;
import com.neasaa.familytree.enums.FamilyRegistrationStatus;
import com.neasaa.familytree.enums.Gender;
import com.neasaa.familytree.enums.MaritalStatus;
import com.neasaa.familytree.operation.OperationNames;
import com.neasaa.familytree.operation.family.model.FamilyRegistrationRequest;
import com.neasaa.familytree.operation.family.model.FamilyRegistrationResponse;
import com.neasaa.familytree.utils.FamilytreeValidationUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Component("FamilyRegistrationOperation")
@Scope("prototype")
public class FamilyRegistrationOperation extends FamilyAbstractOperation<FamilyRegistrationRequest, FamilyRegistrationResponse> {

  @Autowired
  protected FamilyRegistrationRequestDao familyRegistrationRequestDao;

  @Autowired
  private AppProperties appProperties;

  @Autowired
  @Qualifier(APP_EMAIL_SENDER)
  private EmailSender emailSender;

  @Override
  public String getOperationName() {
    return OperationNames.REGISTER_FAMILY;
  }

  @Override
  public void doValidate(FamilyRegistrationRequest opRequest) throws OperationException {
    if (opRequest == null) {
      throw new ValidationException("Invalid request provided.");
    }
    log.info("Request received for family registration: {}", opRequest);

    checkObjectPresent(opRequest.getFamilyDetails(), "family details");
    checkValuePresent(opRequest.getFamilyDetails().getSurname(), "surname");
    checkObjectPresent(opRequest.getFamilyDetails().getFamilyAddress(), "family address");
    FamilytreeValidationUtils.validateAddress(opRequest.getFamilyDetails().getFamilyAddress());
    checkObjectPresent(opRequest.getMembers(), "members");
    if (opRequest.getMembers().isEmpty()) {
      throw new ValidationException("At least one member is required.");
    }
    // Validate members
    for (FamilyRegistrationRequest.Member member : opRequest.getMembers()) {
      checkValuePresent(member.getFirstName(), "first name");
      checkValuePresent(member.getGender(), "gender");
      if (Gender.getGenderByString(member.getGender()) == null) {
        throw new ValidationException("Invalid value for field gender");
      }
      checkValuePresent(member.getMaritalStatus(), "marital status");
      if (MaritalStatus.getMaritalStatus(member.getMaritalStatus()) == null) {
        throw new ValidationException("Invalid value for field marital status");
      }
      checkValuePresent(member.getBirthMonth(), "birth month");
      EmailValidator.validateEmail(member.getEmail(), false);
      if (member.getPhoneNumber() != null) {
        FamilytreeValidationUtils.validatePhoneNumber(member.getPhoneNumber());
      }
      if (member.getBirthYear() == null) {
        throw new ValidationException("Birth year is required.");
      }

      validateBirthDate((member.getBirthDay() == MISSING_BIRTH_DATE_VALUE ? null : member.getBirthDay()),
              member.getBirthMonth(), member.getBirthYear());
      if (!member.getHeadOfFamily()) {
        if(member.getRelationship() == null) {
            throw new ValidationException("Relationship details are required for non head of family member: " + member.getFirstName());
        }
        checkValuePresent(member.getRelationship().getRelationshipType(), "relationship type");
        checkValuePresent(member.getRelationship().getMemberName(), "related member name");
      }
    }
  }

  @Override
  public FamilyRegistrationResponse doExecute(FamilyRegistrationRequest opRequest) throws OperationException {
    log.info("Registering family");

    checkIfMemberAlreadyExists(opRequest.getMembers());
    // Create family address
    FamilyRegistrationRequestEntity familyRegistrationRequestEntity = getFamilyRegistrationEntityFromRequest(opRequest);

    int familyRegistrationRequestId = familyRegistrationRequestDao.insertFamilyRegistrationRequest(familyRegistrationRequestEntity);

    // Build member map
    Map<String, FamilyMemberRegistrationEntity> memberEntityMapByName = new HashMap<>();
    Map<String, FamilyRegistrationRequest.Member> memberDtoMapByName = new HashMap<>();
    FamilyMemberRegistrationEntity headOfFamily = null;

    for (FamilyRegistrationRequest.Member member : opRequest.getMembers()) {
      FamilyMemberRegistrationEntity memberEntity = member.getFamilyMemberRegistrationEntityFromRequest(familyRegistrationRequestId);
      memberEntityMapByName.put(memberEntity.getFirstName(), memberEntity);
      memberDtoMapByName.put(member.getFirstName(), member);
      if(memberEntity.isHeadOfFamily()) {
        headOfFamily = memberEntity;
      }
    }
    memberEntityMapByName.forEach((name, memberEntity) -> {
      if (!memberEntity.isHeadOfFamily()) {
        FamilyRegistrationRequest.Member memberDto = memberDtoMapByName.get(name);
        String relatedName = memberDto.getRelationship().getMemberName();
        FamilyMemberRegistrationEntity relatedMember = memberEntityMapByName.get(relatedName);
        if (relatedMember == null) {
          throw new ValidationException("Related member not found: " + relatedName);
        }
        memberEntity.setRelatedMember(relatedMember);
      }
    });

    if(headOfFamily == null) {
        throw new ValidationException("Head of family is required");
    };

    int memberRequestId = familyRegistrationRequestDao.insertFamilyMemberRegistration(headOfFamily);
    headOfFamily.setMemberRequestId(memberRequestId);

    boolean allMemberProcessed = false;
    int iteration = 0;
    int memberAdded = 0;
    while (!allMemberProcessed) {
      allMemberProcessed = true;
      for(Map.Entry<String, FamilyMemberRegistrationEntity> entry : memberEntityMapByName.entrySet()) {
        FamilyMemberRegistrationEntity memberEntity = entry.getValue();
        if(memberEntity.getMemberRequestId() != 0) {
          continue;
        }
        FamilyMemberRegistrationEntity relatedMember = memberEntity.getRelatedMember();
        if(relatedMember.getMemberRequestId() == 0) {
          allMemberProcessed = false;
          continue;
        }
        memberRequestId = familyRegistrationRequestDao.insertFamilyMemberRegistration(memberEntity);
        memberEntity.setMemberRequestId(memberRequestId);
        memberAdded++;
      }
      iteration++;
      if(iteration > memberEntityMapByName.size()) {
          throw new ValidationException("Circular relationship detected among family members");
      }
      log.info("{} members processed in {} iteration", memberAdded, iteration);
    }

    sendEmailNotification(opRequest.getFamilyDetails().getSurname(), familyRegistrationRequestId);

    FamilyRegistrationResponse response = new FamilyRegistrationResponse();
    response.setSurname(opRequest.getFamilyDetails().getSurname());
    response.setFamilyRegistrationId(familyRegistrationRequestId);
    response.setOperationMessage("Family registered successfully with ID: " + familyRegistrationRequestId);
    return response;
  }

  private void checkIfMemberAlreadyExists (List<FamilyRegistrationRequest.Member> members) throws ValidationException {
    for (FamilyRegistrationRequest.Member member : members) {
      checkIfEmailOrPhoneExists(member.getEmail(), member.getPhoneNumber());
      // Check for duplicate email or phone in FamilyMemberRegistration table
      if (familyRegistrationRequestDao.memberExistWithEmailPhone(member.getEmail(), member.getPhoneNumber())) {
        throw new ValidationException("Member with email " + member.getEmail() + " or phone " + member.getPhoneNumber() + " is already registered");
      }
    }
  }

  private void sendEmailNotification (String familySurname, int familyRegistrationRequestId) {
    try {
      String emailSubject = "New family registration request: " + familySurname;
      String emailBody = String.format("""
              Dear Admin,
              
              A new family registration request has been submitted for family: %s. Please review and approve the request.
              
              Family Registration Request ID: %d
              
              Regards,
              Rajput Chhipa Team""", familySurname, familyRegistrationRequestId);

      String emailString = appProperties.getEmailListForFamilyRegistration();
      List<String> emailList = Arrays.asList(emailString.trim().split(";"));
      EmailMessage emailMessage =
              EmailMessage.builder()
                      .from(appProperties.getEmailSenderEmailId())
                      .fromDisplayName(appProperties.getEmailSenderDisplayName())
                      .to(emailList)
                      .subject(emailSubject)
                      .body(emailBody)
                      .type(EmailMessage.EmailType.TEXT)
                      .build();
      log.info("Sending email to: {}, Subject: {}", emailList, emailSubject);
      emailSender.sendEmail(emailMessage);
    } catch (Exception e) {
        log.error("Failed to send email notification for family registration request ID: {}", familyRegistrationRequestId, e);
    }
  }

  private FamilyRegistrationRequestEntity getFamilyRegistrationEntityFromRequest(FamilyRegistrationRequest request) {
    return FamilyRegistrationRequestEntity.builder()
            .familyName(request.getFamilyDetails().getSurname())
            .familyNameInHindi(request.getFamilyDetails().getSurnameInHindi())
            .samajId(CHHIPA_SAMAJ_ID)
            .gotra(request.getFamilyDetails().getGotra())
            .phone(request.getFamilyDetails().getPhone())
            .email(request.getFamilyDetails().getEmail())
            .addressLine1(request.getFamilyDetails().getFamilyAddress().getAddressLine1())
            .addressLine2(request.getFamilyDetails().getFamilyAddress().getAddressLine2())
            .addressLine3(request.getFamilyDetails().getFamilyAddress().getAddressLine3())
            .city(request.getFamilyDetails().getFamilyAddress().getCity())
            .district(request.getFamilyDetails().getFamilyAddress().getDistrict())
            .state(request.getFamilyDetails().getFamilyAddress().getState())
            .postalCode(request.getFamilyDetails().getFamilyAddress().getPostalCode())
            .country(request.getFamilyDetails().getFamilyAddress().getCountry())
            .createdDate(new Date())
            .clientInfo("Unknown")
            .status(FamilyRegistrationStatus.PENDING)
            .build();
  }
}