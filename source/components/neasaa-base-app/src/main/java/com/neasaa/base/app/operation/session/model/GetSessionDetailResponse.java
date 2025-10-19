package com.neasaa.base.app.operation.session.model;

import com.neasaa.base.app.operation.model.OperationResponse;
import java.util.Date;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetSessionDetailResponse extends OperationResponse {

  private static final long serialVersionUID = 3539396216177139865L;

  private String logonName;
  private String firstName;
  private String lastName;
  private String emailId;
  private boolean sessionActive;
  private Date lastAccessTime;
}
