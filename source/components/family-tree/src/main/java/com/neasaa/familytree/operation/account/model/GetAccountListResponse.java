package com.neasaa.familytree.operation.account.model;

import com.neasaa.base.app.operation.model.OperationResponse;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.List;

@Getter
@Setter
public class GetAccountListResponse extends OperationResponse {
    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    public static class AccountItem {
        private String accountName;
        private int accountId;

        public AccountItem(String accountName, int accountId) {
            this.accountName = accountName;
            this.accountId = accountId;
        }
    }

    private List<AccountItem> accountList;
}
