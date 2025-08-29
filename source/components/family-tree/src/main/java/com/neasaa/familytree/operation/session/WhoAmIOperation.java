package com.neasaa.familytree.operation.session;

import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.model.EmptyOperationRequest;
import com.neasaa.base.app.service.AppSessionUser;
import com.neasaa.familytree.dao.pg.FamilyMemberDao;
import com.neasaa.familytree.entity.FamilyMemberEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.neasaa.familytree.operation.OperationNames.WHO_AM_I;

@Log4j2
@Component("WhoAmIOperation")
@Scope("prototype")
public class WhoAmIOperation extends AbstractOperation<EmptyOperationRequest, WhoAmIResponse> {

    private static final String SESSION_MEMBER_ATTRIBUTE_KEY = "MEMBER_DETAILS";

    @Autowired
    private FamilyMemberDao familyMemberDao;

    @Override
    public String getOperationName() {
        return WHO_AM_I;
    }

    @Override
    public void doValidate(EmptyOperationRequest opRequest) throws OperationException {

    }

    @Override
    public WhoAmIResponse doExecute(EmptyOperationRequest opRequest) throws OperationException {
        AppSessionUser appSessionUser = getContext().getAppSessionUser();
        FamilyMemberEntity memberEntity = null;
        if(appSessionUser.getOtherAttributes() == null || !appSessionUser.getOtherAttributes().containsKey(SESSION_MEMBER_ATTRIBUTE_KEY)) {
            String logonName = appSessionUser.getLogonName();
            memberEntity = familyMemberDao.getMemberByLogonName(logonName);
            appSessionUser.addOtherAttributes(SESSION_MEMBER_ATTRIBUTE_KEY, memberEntity);
        } else {
            memberEntity = (FamilyMemberEntity) appSessionUser.getOtherAttributes().get(SESSION_MEMBER_ATTRIBUTE_KEY);
        }

        return WhoAmIResponse.builder()
                .firstName(appSessionUser.getFirstName())
                .lastName(appSessionUser.getLastName())
                .sessionActive(true)
                .lastAccessTime(appSessionUser.getLastAccessTime())
                .memberId(memberEntity.getMemberId())
                .familyId(memberEntity.getFamilyId())
                .profileImageThumbnail(memberEntity.getProfileImageThumbnail())
                .build();
    }


}
