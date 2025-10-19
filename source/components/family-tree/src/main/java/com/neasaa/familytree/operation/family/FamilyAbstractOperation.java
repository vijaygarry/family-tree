package com.neasaa.familytree.operation.family;

import static com.neasaa.familytree.operation.OperationNames.ADD_MEMBER_TO_ANY_FAMILY;
import static com.neasaa.familytree.operation.OperationNames.ADD_MEMBER_TO_MY_FAMILY;
import static com.neasaa.familytree.operation.OperationNames.UPDATE_ANY_FAMILY_DETAILS;
import static com.neasaa.familytree.operation.OperationNames.UPDATE_ANY_FAMILY_MEMBER;
import static com.neasaa.familytree.operation.OperationNames.UPDATE_MY_FAMILY_DETAILS;
import static com.neasaa.familytree.operation.OperationNames.UPDATE_MY_FAMILY_MEMBER;

import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.model.OperationRequest;
import com.neasaa.base.app.operation.model.OperationResponse;
import com.neasaa.familytree.dao.pg.AddressDao;
import com.neasaa.familytree.dao.pg.FamilyDao;
import com.neasaa.familytree.dao.pg.FamilyMemberDao;
import com.neasaa.familytree.dao.pg.MemberRelationshipDao;
import com.neasaa.familytree.entity.FamilyMemberEntity;
import com.neasaa.familytree.utils.SessionUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

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
}
