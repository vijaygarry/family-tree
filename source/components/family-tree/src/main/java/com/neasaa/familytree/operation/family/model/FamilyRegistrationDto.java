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
public class FamilyRegistrationDto implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  private int familyRequestId;
  private String familyName;
  private String familyNameInHindi;
  private String gotra;
  private String city;
  private String district;
  private String state;
  private String country;
  private String phone;
  private String email;
  private Date registrationDate;
  private FamilyRegistrationStatus status;
  private Integer familyId;

  public static FamilyRegistrationDto fromEntity(FamilyRegistrationRequestEntity entity) {
    return FamilyRegistrationDto.builder()
        .familyRequestId(entity.getFamilyRequestId())
        .familyName(entity.getFamilyName())
        .familyNameInHindi(entity.getFamilyNameInHindi())
        .gotra(entity.getGotra())
        .city(entity.getCity())
        .district(entity.getDistrict())
        .state(entity.getState())
        .country(entity.getCountry())
        .phone(entity.getPhone())
        .email(entity.getEmail())
        .registrationDate(entity.getCreatedDate())
        .status(entity.getStatus())
        .familyId(entity.getFamilyId())
        .build();
  }
}
