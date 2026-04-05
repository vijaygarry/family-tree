package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessFamilyRegistrationResponse extends OperationResponse {
  private int familyRegistrationId;
  private Integer familyId;
  private String familySurname;
  private Integer memberCount;
  private String message;
}