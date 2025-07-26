package com.neasaa.familytree.operation.account.model;

import com.neasaa.base.app.operation.model.OperationRequest;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter
@Setter
public class GetAccountStatementRequest extends OperationRequest {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer accountId;
    private String year;
    private String month;
}
