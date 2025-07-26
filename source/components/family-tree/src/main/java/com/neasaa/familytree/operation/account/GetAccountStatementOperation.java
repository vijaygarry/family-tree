package com.neasaa.familytree.operation.account;

import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.utils.ValidationUtils;
import com.neasaa.familytree.operation.account.model.GetAccountStatementRequest;
import com.neasaa.familytree.operation.account.model.GetAccountStatementResponse;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.neasaa.familytree.operation.OperationNames.GET_ACCOUNT_STATEMENT;

@Component("GetAccountStatementOperation")
@Scope("prototype")
public class GetAccountStatementOperation extends AbstractOperation<GetAccountStatementRequest, GetAccountStatementResponse> {

    @Override
    public String getOperationName() {
        return GET_ACCOUNT_STATEMENT;
    }

    @Override
    public void doValidate(GetAccountStatementRequest opRequest) throws OperationException {
        ValidationUtils.checkObjectPresent(opRequest.getAccountId(), "Account ID");
    }

    @Override
    public GetAccountStatementResponse doExecute(GetAccountStatementRequest opRequest) throws OperationException {
        GetAccountStatementResponse response = new GetAccountStatementResponse();
        // Here you would typically fetch the account statement from a database or another service.
        // For demonstration purposes, we will return a dummy response.
        GetAccountStatementResponse.AccountDetails accountDetails = GetAccountStatementResponse.AccountDetails.builder()
                .accountId(opRequest.getAccountId())
                .accountName("Samaj Application Account")
                .currentBalance(new BigDecimal("2500.00"))
                .accountManager("Vijay Garothaya")
                .bankName("ICICI Bank")
                .bankAccountId("123456789")
                .bankAccountType("Savings")
                .build();
        response.setAccountDetails(accountDetails);
        List<GetAccountStatementResponse.AccountTransaction> transactionList = new ArrayList<>();
        BigDecimal startingBal = new BigDecimal("2500.00");
        BigDecimal runningBal = startingBal;
        transactionList.add(getAccountTransaction("TXN001", "2025-01-01", "Starting Balance", new BigDecimal("0.00"), 'C', startingBal));
        for(int i = 2; i < 22; i++) {
            BigDecimal txnAmount = new BigDecimal("100.00").multiply(new BigDecimal(i + 1));
            char txnType = (i % 2 == 0) ? 'C' : 'D'; // Alternate between credit and debit
            runningBal = txnType == 'C' ? runningBal.add(txnAmount) : runningBal.subtract(txnAmount);
            transactionList.add(getAccountTransaction("TXN00" + (i + 1), "2025-01-0" + (i + 1), "Transaction #" + (i + 1), txnAmount, txnType, runningBal));
        }
        response.setTransactions(transactionList);
        return response;
    }

    private GetAccountStatementResponse.AccountTransaction getAccountTransaction(String transactionId, String txnDate, String description, BigDecimal amount, char txnType, BigDecimal balanceAfterTransaction) {
        return GetAccountStatementResponse.AccountTransaction.builder()
                .accountId(1)
                .transactionId(transactionId)
                .txnDate(txnDate)
                .description(description)
                .amount(amount)
                .txnType(txnType)
                .balanceAfterTransaction(balanceAfterTransaction)
                .build();
    }

}
