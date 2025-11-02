package com.neasaa.familytree.operation.family;

import static com.neasaa.familytree.operation.OperationNames.ADD_MEMBER_TO_ANY_FAMILY;
import static com.neasaa.familytree.operation.OperationNames.ADD_MEMBER_TO_MY_FAMILY;
import static com.neasaa.familytree.operation.OperationNames.UPDATE_ANY_FAMILY_DETAILS;
import static com.neasaa.familytree.operation.OperationNames.UPDATE_ANY_FAMILY_MEMBER;
import static com.neasaa.familytree.operation.OperationNames.UPDATE_MY_FAMILY_DETAILS;
import static com.neasaa.familytree.operation.OperationNames.UPDATE_MY_FAMILY_MEMBER;

import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.exception.InternalServerException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.base.app.operation.model.OperationRequest;
import com.neasaa.base.app.operation.model.OperationResponse;
import com.neasaa.base.app.service.AppSessionUser;
import com.neasaa.familytree.dao.pg.AddressDao;
import com.neasaa.familytree.dao.pg.FamilyDao;
import com.neasaa.familytree.dao.pg.FamilyMemberDao;
import com.neasaa.familytree.dao.pg.MemberRelationshipDao;
import com.neasaa.familytree.entity.FamilyMemberEntity;
import com.neasaa.familytree.entity.MemberRelationshipEntity;
import com.neasaa.familytree.enums.Gender;
import com.neasaa.familytree.operation.family.model.MemberSummaryDto;
import com.neasaa.familytree.utils.DataFormatter;
import com.neasaa.familytree.utils.SessionUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public abstract class FamilyAbstractOperation<
        Request extends OperationRequest, Response extends OperationResponse>
    extends AbstractOperation<Request, Response> {

  public static final String HEAD_OF_FAMILY = "Head of Family";
  public static final String WIFE_OF_MEMBER = "Wife of %s";
  public static final String HUSBAND_OF_MEMBER = "Husband of %s";
  public static final String SON_OF_MEMBER = "Son of %s";
  public static final String DAUGHTER_OF_MEMBER = "Daughter of %s";
  public static final String FATHER_OF_MEMBER = "Father of %s";
  public static final String MOTHER_OF_MEMBER = "Mother of %s";
  public static final String UNKNOWN_RELATIONSHIP = "Relationship unknown";
  public static final String SELF_RELATIONSHIP = "Self";
  public static final String BROTHER_OF_MEMBER = "Brother of %s";
  public static final String SISTER_OF_MEMBER = "Sister of %s";

  @Autowired protected AddressDao addressDao;

  @Autowired protected FamilyDao familyDao;

  @Autowired protected FamilyMemberDao familyMemberDao;

  @Autowired protected MemberRelationshipDao memberRelationshipDao;

  protected FamilyMemberEntity getMemberEntityFromSession() {
    if (getContext() == null || getContext().getAppSessionUser() == null) {
      log.info("Operation context or AppSessionUser is null, cannot get FamilyMemberEntity from session");
      return null;
    }
    AppSessionUser appSessionUser = getContext().getAppSessionUser();
    return SessionUtils.getFamilyMemberFromSession(appSessionUser);
  }

  protected int getSamajIdFromSession() {
    FamilyMemberEntity memberEntityFromSession = getMemberEntityFromSession();
    if(memberEntityFromSession == null) {
        log.info("FamilyMemberEntity not found in session, cannot get SamajId from session");
        throw new InternalServerException("Internal error occurred, please try again later.");
    }
    return memberEntityFromSession.getSamajId();
  }
  /**
   * Check if the logged in user is allowed to update family details of the given family id.
   *
   * @param familyId - Family ID to check
   * @return
   */
  protected boolean canLoggedInUserUpdateFamily(int familyId) {
    if (isOperationAllowedForUser(UPDATE_ANY_FAMILY_DETAILS)) {
      return true;
    }

    if (getContext() == null || getContext().getAppSessionUser() == null) {
      log.info(
          "Operation context or AppSessionUser is null, cannot check if family edit allowed for user");
      return false;
    }

    FamilyMemberEntity memberEntity =
        SessionUtils.getFamilyMemberFromSession(getContext().getAppSessionUser());
    if (memberEntity == null) {
      log.info("Family member not found in session, cannot check if family edit allowed for user");
      return false;
    }
    if (memberEntity.getFamilyId() != familyId) {
      // Only allowed to edit own family details.
      return false;
    }
    return isOperationAllowedForUser(UPDATE_MY_FAMILY_DETAILS);
  }

  protected boolean canLoggedInUserAddNewMember(int familyId) {
    if (isOperationAllowedForUser(ADD_MEMBER_TO_ANY_FAMILY)) {
      return true;
    }

    if (getContext() == null || getContext().getAppSessionUser() == null) {
      log.info(
          "Operation context or AppSessionUser is null, cannot check if logged in user can add new member");
      return false;
    }

    FamilyMemberEntity memberEntity =
        SessionUtils.getFamilyMemberFromSession(getContext().getAppSessionUser());
    if (memberEntity == null) {
      log.info("Family member not found in session, cannot check if logged in user can add new member");
      return false;
    }
    if (memberEntity.getFamilyId() != familyId) {
      // Only allowed to edit own family details.
      return false;
    }
    return isOperationAllowedForUser(ADD_MEMBER_TO_MY_FAMILY);
  }

  protected boolean canLoggedInUserUpdateMember(int familyId) {
    if (isOperationAllowedForUser(UPDATE_ANY_FAMILY_MEMBER)) {
      return true;
    }

    if (getContext() == null || getContext().getAppSessionUser() == null) {
      log.info(
          "Operation context or AppSessionUser is null, cannot check if family member update allowed for user");
      return false;
    }
    FamilyMemberEntity memberEntity =
        SessionUtils.getFamilyMemberFromSession(getContext().getAppSessionUser());
    if (memberEntity == null) {
      log.info(
          "Family member not found in session, cannot check if family member update allowed for user");
      return false;
    }
    if (memberEntity.getFamilyId() != familyId) {
      // Only allowed to edit own family members.
      return false;
    }
    return isOperationAllowedForUser(UPDATE_MY_FAMILY_MEMBER);
  }

  /**
   * Get children for the member and set the relationship as "Son of abc" or "Daughter of abc". This is
   * mainly to get siblings of a member by passing the parent ids.
   */
  protected List<MemberSummaryDto> getChildrenForMember(int samajId, int memberId, int spouseMemberId) {
    List<MemberSummaryDto> children = new ArrayList<>();
    List<MemberRelationshipEntity> childrenForMember =
            memberRelationshipDao.getChildrenForMemberById(memberId, spouseMemberId);
    if (childrenForMember != null) {
      for (MemberRelationshipEntity childRelation : childrenForMember) {
        MemberSummaryDto child = getMemberSummaryDtoFromDB(samajId, childRelation.getRelatedMemberId());
        if (child != null) {
          children.add(child);
        }
      }
    } else {
      log.info(
              "No children found for member with parent1: {} and parent2: {}",
              memberId,
              spouseMemberId);
      return null;
    }
    return children;
  }

  protected MemberSummaryDto getMemberSummaryDtoFromDB(int samajId, int memberId) {
    FamilyMemberEntity memberFromDb = familyMemberDao.getMemberById(samajId, memberId);
    if (memberFromDb == null) {
      return null;
    }
    return MemberSummaryDto.getMemberSummaryDto(memberFromDb, UNKNOWN_RELATIONSHIP);
  }

  protected List<MemberSummaryDto> getParentsForMember (
          int samajId, int memberId, String currentMemberName) {
    List<MemberRelationshipEntity> parents =
            memberRelationshipDao.getParentsForMemberById(memberId);

    if (parents == null || parents.isEmpty()) {
      log.info("No parents found for member: {}", memberId);
      return null;
    }

    MemberSummaryDto father = null;
    MemberSummaryDto mother = null;
    for (MemberRelationshipEntity parentRelationship : parents) {
      MemberSummaryDto parent = getMemberSummaryDtoFromDB(samajId, parentRelationship.getMemberId());
      if (parent != null) {
        String familyRelationship = null;
        if (parent.getGender() == Gender.Male) {
          father = parent;
          familyRelationship = FATHER_OF_MEMBER.formatted(currentMemberName);
          father.setFamilyRelationship(familyRelationship);
        } else {
          mother = parent;
          familyRelationship = MOTHER_OF_MEMBER.formatted(currentMemberName);
          mother.setFamilyRelationship(familyRelationship);
        }
      }
    }
    List<MemberSummaryDto> parentsList = new ArrayList<>();
    if (father != null) {
      log.info(
              "Father found for member {}: {}", currentMemberName, father.getFirstName());
      parentsList.add(father);
      if(mother != null) {
        parentsList.add(mother);
      }
    } else if (mother != null) {
      log.info(
              "Mother found for member {}: {}", currentMemberName, mother.getFirstName());
      parentsList.add(mother);
    }

    return parentsList;
  }

  protected void checkIfEmailOrPhoneExists (String inputEmail, String inputPhone, FamilyMemberEntity memberEntityFromDb) {
    //If email in request is not null
    if(inputEmail != null) {
      // If DBEntity email different from request email (In this case DB Email can be null i.e. updating first time)
      if(!inputEmail.equalsIgnoreCase(memberEntityFromDb.getEmail())) {
        boolean memberExistsForEmail = familyMemberDao.isMemberExistsForEmail(inputEmail);
        if(memberExistsForEmail) {
          throw new ValidationException("Email id " + inputEmail + " already in use, please provide other email id");
        }
      }
    }

    //If phone in request is not null
    if(inputPhone != null) {
      String normalizePhoneNumber = DataFormatter.formatPhoneNumberForDBStorage(inputPhone);
      // If DBEntity phone different from request phone (In this case DB phone can be null i.e. updating first time)
      if(!normalizePhoneNumber.equalsIgnoreCase(memberEntityFromDb.getPhone())) {
        boolean memberExistsForPhone = familyMemberDao.isMemberExistsForPhone(normalizePhoneNumber);
        if(memberExistsForPhone) {
          throw new ValidationException("Phone " + inputPhone + " already in use, please provide other phone number");
        }
      }
    }
  }

  protected void checkIfEmailOrPhoneExists (String inputEmail, String inputPhone) {
    //If email in request is not null
    if (inputEmail != null) {
      boolean memberExistsForEmail = familyMemberDao.isMemberExistsForEmail(inputEmail);
      if (memberExistsForEmail) {
        throw new ValidationException("Email id " + inputEmail + " already in use, please provide other email id");
      }
    }

    //If phone in request is not null
    if (inputPhone != null) {
      String normalizePhoneNumber = DataFormatter.formatPhoneNumberForDBStorage(inputPhone);
      boolean memberExistsForPhone = familyMemberDao.isMemberExistsForPhone(normalizePhoneNumber);
      if (memberExistsForPhone) {
        throw new ValidationException("Phone " + inputPhone + " already in use, please provide other phone number");
      }
    }
  }

}
