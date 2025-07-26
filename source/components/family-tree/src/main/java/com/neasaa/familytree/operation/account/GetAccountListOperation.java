package com.neasaa.familytree.operation.account;

import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.model.EmptyOperationRequest;
import com.neasaa.familytree.operation.account.model.GetAccountListResponse;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.neasaa.familytree.operation.OperationNames.GET_ACCOUNT_LIST;

@Component("GetAccountListOperation")
@Scope("prototype")
public class GetAccountListOperation extends AbstractOperation<EmptyOperationRequest, GetAccountListResponse> {

    @Override
    public String getOperationName() {
        return GET_ACCOUNT_LIST;
    }

    @Override
    public void doValidate(EmptyOperationRequest opRequest) throws OperationException {

    }

    @Override
    public GetAccountListResponse doExecute(EmptyOperationRequest opRequest) throws OperationException {
        GetAccountListResponse response = new GetAccountListResponse();
        GetAccountListResponse.AccountItem accountItem1 = new GetAccountListResponse.AccountItem("Samaj Application Account", 1);
        GetAccountListResponse.AccountItem accountItem2 = new GetAccountListResponse.AccountItem("Amravati Samaj Account", 2);
        response.setAccountList(List.of(accountItem1, accountItem2));
        return response;
    }
}
