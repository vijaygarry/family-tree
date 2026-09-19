package com.neasaa.familytree.operation.family.model;

import com.neasaa.familytree.entity.FamilyRegistrationRequestEntity;
import com.neasaa.familytree.enums.FamilyRegistrationStatus;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistrationDetailsDto implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  private int familyRequestId;
  private String familyName;
  private String familyNameInHindi;
  private String gotra;
  private String headOfFamilyName;
  private String addressLine1;
  private String addressLine2;
  private String addressLine3;
  private String city;
  private String district;
  private String state;
  private String postalCode;
  private String country;
  private String phone;
  private String email;
  private Date registrationDate;
  private FamilyRegistrationStatus status;
  private Integer familyId;

  public static RegistrationDetailsDto fromEntity(
      FamilyRegistrationRequestEntity entity, String headOfFamilyName) {
    return RegistrationDetailsDto.builder()
        .familyRequestId(entity.getFamilyRequestId())
        .familyName(entity.getFamilyName())
        .familyNameInHindi(entity.getFamilyNameInHindi())
        .gotra(entity.getGotra())
        .headOfFamilyName(headOfFamilyName)
        .addressLine1(entity.getAddressLine1())
        .addressLine2(entity.getAddressLine2())
        .addressLine3(entity.getAddressLine3())
        .city(entity.getCity())
        .district(entity.getDistrict())
        .state(entity.getState())
        .postalCode(entity.getPostalCode())
        .country(entity.getCountry())
        .phone(entity.getPhone())
        .email(entity.getEmail())
        .registrationDate(entity.getCreatedDate())
        .status(entity.getStatus())
        .familyId(entity.getFamilyId())
        .build();
  }
}
