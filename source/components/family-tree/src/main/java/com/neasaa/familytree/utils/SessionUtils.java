package com.neasaa.familytree.utils;

import com.neasaa.base.app.operation.OperationContext;
import com.neasaa.base.app.service.AppSessionUser;
import com.neasaa.familytree.entity.FamilyMemberEntity;

public class SessionUtils {

    private static final String SESSION_MEMBER_ATTRIBUTE_KEY = "MEMBER_DETAILS";

    public static FamilyMemberEntity getFamilyMemberFromSession(AppSessionUser appSessionUser) {
        if(appSessionUser.getOtherAttributes() == null || !appSessionUser.getOtherAttributes().containsKey(SESSION_MEMBER_ATTRIBUTE_KEY)) {
            return null;
        }
        return (FamilyMemberEntity) appSessionUser.getOtherAttributes().get(SESSION_MEMBER_ATTRIBUTE_KEY);
    }

    public static void setFamilyMemberInSession(AppSessionUser appSessionUser, FamilyMemberEntity familyMemberEntity) {
        appSessionUser.addOtherAttributes(SESSION_MEMBER_ATTRIBUTE_KEY, familyMemberEntity);
    }

    public static FamilyMemberEntity getFamilyMemberFromContext (OperationContext operationContext) {
        AppSessionUser appSessionUser = operationContext.getAppSessionUser();
        if(appSessionUser == null) {
            return null;
        }
        return getFamilyMemberFromSession(appSessionUser);
    }

}
