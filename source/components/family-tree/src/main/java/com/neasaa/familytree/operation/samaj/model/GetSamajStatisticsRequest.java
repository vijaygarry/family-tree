package com.neasaa.familytree.operation.samaj.model;

import com.neasaa.base.app.operation.model.OperationRequest;
import java.io.Serial;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetSamajStatisticsRequest extends OperationRequest {
  @Serial private static final long serialVersionUID = 1L;

  private Integer samajId;

  @Override
  public void normalize() {}
}
