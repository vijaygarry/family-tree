package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateFamilyDetailsRequest extends OperationRequest {

  private Integer familyId;
  private String familyName;
  private String familyNameInHindi;
  private String gotra;
  private String phone;
  private boolean isPhoneWhatsappRegistered;
  private String email;
  private AddressDto familyAddress;

  @Override
  public void normalize() {
    if (familyName != null) {
      familyName = familyName.trim();
    }
    if (familyNameInHindi != null) {
      familyNameInHindi = familyNameInHindi.trim();
    }
    if (gotra != null) {
      gotra = gotra.trim();
    }
    if (phone != null) {
      phone = phone.trim();
    }
    if (email != null) {
      email = email.trim();
    }
    if (familyAddress != null) {
      familyAddress.normalize();
    }
  }
}
