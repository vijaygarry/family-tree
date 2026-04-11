package com.neasaa.base.app.operation.session.model;

import com.neasaa.base.app.enums.SessionExitCode;
import com.neasaa.base.app.operation.model.OperationRequest;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

public class LogoutRequest extends OperationRequest {

  @Serial
  private static final long serialVersionUID = 195876741083906785L;

  @Setter @Getter private SessionExitCode sessionExitCode;

  @Override
  public void normalize() {
  }
}
