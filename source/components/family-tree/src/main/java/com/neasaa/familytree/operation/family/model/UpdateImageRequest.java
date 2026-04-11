package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationRequest;
import java.nio.file.Path;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateImageRequest extends OperationRequest {

  private Integer familyId; // If request to update family image
  private Integer memberId; // If request to update member image
  private Path tmpUploadedFilePath;
  private String operationName;

  @Override
  public void normalize() {
  }
}
