package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationRequest;
import java.io.Serial;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateFamilyMemberProfileRequest extends OperationRequest {

  @Serial private static final long serialVersionUID = -4292257590045882963L;

  private Integer familyId;
  private Integer memberId;
  private String firstName;
  private String firstNameInHindi;
  private String maidenLastName;
  private String nickName;
  private String nickNameInHindi;

  private String phone;
  private boolean isPhoneWhatsappRegistered;

  private String email;
  private String gender;
  private String maritalStatus;
  private String weddingDate;
  private Short birthDay;
  private String birthMonth;
  private Short birthYear;
  private String dateOfDeath;

  private String educationDetails;
  private String occupation;
  private String hobby;
  private String linkedinUrl;

  private boolean addressSameAsFamily;
  private AddressDto memberAddress;

  public void trimFields() {
    if (firstName != null) {
      firstName = firstName.trim();
    }
    if (firstNameInHindi != null) {
      firstNameInHindi = firstNameInHindi.trim();
    }
    if (maidenLastName != null) {
      maidenLastName = maidenLastName.trim();
    }
    if (nickName != null) {
      nickName = nickName.trim();
    }
    if (nickNameInHindi != null) {
      nickNameInHindi = nickNameInHindi.trim();
    }
    if (phone != null) {
      phone = phone.trim();
    }
    if (email != null) {
      email = email.trim();
      email = email.toLowerCase();
    }
    if (gender != null) {
      gender = gender.trim();
    }
    if (maritalStatus != null) {
      maritalStatus = maritalStatus.trim();
    }

    if (birthMonth != null) {
      birthMonth = birthMonth.trim();
    }
    if (educationDetails != null) {
      educationDetails = educationDetails.trim();
    }
    if (occupation != null) {
      occupation = occupation.trim();
    }
    if (hobby != null) {
      hobby = hobby.trim();
    }
    if (linkedinUrl != null) {
      linkedinUrl = linkedinUrl.trim();
    }
    if (memberAddress != null) {
      memberAddress.trimFields();
    }
  }
}
