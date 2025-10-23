package com.neasaa.familytree.utils;

import com.neasaa.base.app.operation.OperationContext;
import com.neasaa.base.app.service.AppSessionUser;
import com.neasaa.familytree.entity.FamilyMemberEntity;

import java.util.List;

public class SessionUtils {

  private static final String SESSION_MEMBER_ATTRIBUTE_KEY = "MEMBER_DETAILS";
  private static final String SESSION_OPERATION_ALLOWED_KEY = "OPERATION_ALLOWED";

  public static FamilyMemberEntity getFamilyMemberFromSession(AppSessionUser appSessionUser) {
    if (appSessionUser.getOtherAttributes() == null
        || !appSessionUser.getOtherAttributes().containsKey(SESSION_MEMBER_ATTRIBUTE_KEY)) {
      return null;
    }
    return (FamilyMemberEntity)
        appSessionUser.getOtherAttributes().get(SESSION_MEMBER_ATTRIBUTE_KEY);
  }

  public static void setFamilyMemberInSession(
      AppSessionUser appSessionUser, FamilyMemberEntity familyMemberEntity) {
    appSessionUser.addOtherAttributes(SESSION_MEMBER_ATTRIBUTE_KEY, familyMemberEntity);
  }

  public static void setOperationAllowedInSession(
          AppSessionUser appSessionUser, List<String> operationAllowed) {
    appSessionUser.addOtherAttributes(SESSION_OPERATION_ALLOWED_KEY, operationAllowed);
  }

  @SuppressWarnings("unchecked")
  public static List<String> getOperationAllowedFromSession(AppSessionUser appSessionUser) {
    if (appSessionUser.getOtherAttributes() == null
            || !appSessionUser.getOtherAttributes().containsKey(SESSION_OPERATION_ALLOWED_KEY)) {
      return null;
    }
    return (List<String>)
            appSessionUser.getOtherAttributes().get(SESSION_OPERATION_ALLOWED_KEY);
  }

  public static FamilyMemberEntity getFamilyMemberFromContext(OperationContext operationContext) {
    AppSessionUser appSessionUser = operationContext.getAppSessionUser();
    if (appSessionUser == null) {
      return null;
    }
    return getFamilyMemberFromSession(appSessionUser);
  }
}
