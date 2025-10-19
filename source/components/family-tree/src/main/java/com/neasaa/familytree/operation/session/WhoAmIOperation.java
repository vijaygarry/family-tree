package com.neasaa.familytree.operation.session;

import static com.neasaa.familytree.operation.OperationNames.WHO_AM_I;

import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.model.EmptyOperationRequest;
import com.neasaa.base.app.service.AppSessionUser;
import com.neasaa.familytree.dao.pg.FamilyMemberDao;
import com.neasaa.familytree.entity.FamilyMemberEntity;
import com.neasaa.familytree.utils.SessionUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Log4j2
@Component("WhoAmIOperation")
@Scope("prototype")
public class WhoAmIOperation extends AbstractOperation<EmptyOperationRequest, WhoAmIResponse> {

  @Autowired private FamilyMemberDao familyMemberDao;

  @Override
  public String getOperationName() {
    return WHO_AM_I;
  }

  @Override
  public void doValidate(EmptyOperationRequest opRequest) throws OperationException {}

  @Override
  public WhoAmIResponse doExecute(EmptyOperationRequest opRequest) throws OperationException {
    AppSessionUser appSessionUser = getContext().getAppSessionUser();
    FamilyMemberEntity memberEntity = SessionUtils.getFamilyMemberFromSession(appSessionUser);

    // If member not in session, fetch from DB and set in session
    if (memberEntity == null) {
      String logonName = appSessionUser.getLogonName();
      memberEntity = familyMemberDao.getMemberByLogonName(logonName);
      SessionUtils.setFamilyMemberInSession(appSessionUser, memberEntity);
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
