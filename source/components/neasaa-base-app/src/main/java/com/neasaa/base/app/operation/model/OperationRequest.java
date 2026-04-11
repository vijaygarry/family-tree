package com.neasaa.base.app.operation.model;

import java.io.Serial;
import java.io.Serializable;

public abstract class OperationRequest implements Serializable {

  @Serial
  private static final long serialVersionUID = -3732306981452292314L;

  /** This method can be used to normalize the request data before processing the operation.
   * For example, trimming strings, converting to lower case, etc.
   * */
  public abstract void normalize();

}
