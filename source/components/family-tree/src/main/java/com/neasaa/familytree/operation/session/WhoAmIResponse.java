package com.neasaa.familytree.operation.session;

import com.neasaa.base.app.operation.model.OperationResponse;
import java.util.Date;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WhoAmIResponse extends OperationResponse {
  private String firstName;
  private String lastName;
  private boolean sessionActive;
  private Date lastAccessTime;
  private int memberId;
  private int familyId;
  private List<String> operationAllowed;
  private String profileImageThumbnail;
}
