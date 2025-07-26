package com.neasaa.familytree.operation.account.model;

import com.neasaa.base.app.operation.model.OperationResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class GetAccountStatementResponse extends OperationResponse {

    private AccountDetails accountDetails;
    private List<AccountTransaction> transactions;

    @Getter
    @Builder
    public static class AccountDetails {
        private int accountId;
        private String accountName;
        private BigDecimal currentBalance;
        private String accountManager;
        private String bankName;
        private String bankAccountId;
        private String bankAccountType;
    }

    @Getter
    @Builder
    public static class AccountTransaction {
        private int accountId;
        private String transactionId;
        private String txnDate;
        private String description;
        private BigDecimal amount;
        private char txnType; // 'C' for credit, 'D' for debit
        private BigDecimal balanceAfterTransaction;
    }
}
