package com.neasaa.base.app.operation.session;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.OperationNames;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.model.EmptyOperationRequest;
import com.neasaa.base.app.operation.session.model.GetSessionDetailResponse;
import com.neasaa.base.app.service.AppSessionUser;

@Component ("GetSessionDetailOperation")
@Scope("prototype")
public class GetSessionDetailOperation extends AbstractOperation<EmptyOperationRequest, GetSessionDetailResponse>{@Override
	
	public String getOperationName() {
		return OperationNames.GET_SESSION_DETAILS;
	}

	@Override
	public void doValidate(EmptyOperationRequest opRequest) throws OperationException {
		
	}

	@Override
	public GetSessionDetailResponse doExecute(EmptyOperationRequest opRequest) throws OperationException {
		AppSessionUser appSessionUser = getContext().getAppSessionUser();
		return GetSessionDetailResponse.builder()
			.logonName(appSessionUser.getLogonName())
			.firstName(appSessionUser.getFirstName())
			.lastName(appSessionUser.getLastName())
			.emailId(appSessionUser.getEmailId())
			.sessionActive(true)
			.lastAccessTime(appSessionUser.getLastAccessTime())
			.build();
	}


}
