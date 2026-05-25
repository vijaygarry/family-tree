package com.neasaa.familytree.operation.family.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CityFamilyCountDto {
  private String cityName;
  private String stateName;
  private String country;
  private Integer familyCount;
}
