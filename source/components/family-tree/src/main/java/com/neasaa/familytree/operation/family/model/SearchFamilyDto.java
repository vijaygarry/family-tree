package com.neasaa.familytree.operation.family.model;

import com.neasaa.familytree.entity.SearchFamilyEntity;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SearchFamilyDto {
  private int familyId;
  private String familyName;
  private String familyNameInHindi;
  private String gotra;
  private String region;
  private String phone;
  private boolean isPhoneWhatsappRegistered;
  private String familyImage;
  private String headOfFamilyFirstName;
  private String headOfFamilyFirstNameInHindi;

  public static SearchFamilyDto getSearchFamilyDtoFromEntity(SearchFamilyEntity searchFamily) {
    SearchFamilyDto dto = new SearchFamilyDto();
    dto.setFamilyId(searchFamily.getFamilyId());
    dto.setFamilyName(searchFamily.getFamilyName());
    dto.setFamilyNameInHindi(searchFamily.getFamilyNameInHindi());
    dto.setGotra(searchFamily.getGotra());
    dto.setRegion(searchFamily.getRegion());
    dto.setPhone(searchFamily.getPhone());
    dto.setPhoneWhatsappRegistered(searchFamily.isPhoneWhatsappRegistered());
    dto.setFamilyImage(searchFamily.getFamilyImage());
    dto.setHeadOfFamilyFirstName(searchFamily.getHeadOfFamilyFirstName());
    dto.setHeadOfFamilyFirstNameInHindi(searchFamily.getHeadOfFamilyFirstNameInHindi());
    return dto;
  }
}
