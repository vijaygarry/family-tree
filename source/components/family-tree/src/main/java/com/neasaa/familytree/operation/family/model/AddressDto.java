package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.AuditInfo;
import com.neasaa.familytree.entity.AddressEntity;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AddressDto {

  private int addressId;
  private String addressLine1;
  private String addressLine2;
  private String addressLine3;
  private String city;
  private String district;
  private String state;
  private String postalCode;
  private String country;

  public static AddressDto getAddressDtoFromEntity(AddressEntity address) {
    if (address == null) {
      return null;
    }
    return AddressDto.builder()
        .addressLine1(address.getAddressLine1())
        .addressLine2(address.getAddressLine2())
        .addressLine3(address.getAddressLine3())
        .city(address.getCity())
        .district(address.getDistrict())
        .state(address.getState())
        .postalCode(address.getPostalCode())
        .country(address.getCountry())
        .build();
  }

  public AddressEntity getAddressEntityFromDto(AuditInfo auditInfo) {
     return AddressEntity.builder()
            .addressLine1(getAddressLine1())
            .addressLine2(getAddressLine2())
            .addressLine3(getAddressLine3())
            .city(getCity())
            .district(getDistrict())
            .state(getState())
            .postalCode(getPostalCode())
            .country(getCountry())
            .createdBy(auditInfo.getCreatedBy())
            .createdDate(auditInfo.getCreatedDate())
            .lastUpdatedBy(auditInfo.getLastUpdatedBy())
            .lastUpdatedDate(auditInfo.getLastUpdatedDate())
            .build();
  }
  public void trimFields() {
    if (addressLine1 != null) {
      addressLine1 = addressLine1.trim();
    }
    if (addressLine2 != null) {
      addressLine2 = addressLine2.trim();
    }
    if (addressLine3 != null) {
      addressLine3 = addressLine3.trim();
    }
    if (city != null) {
      city = city.trim();
    }
    if (district != null) {
      district = district.trim();
    }
    if (state != null) {
      state = state.trim();
    }
    if (postalCode != null) {
      postalCode = postalCode.trim();
    }
    if (country != null) {
      country = country.trim();
    }
  }
}
