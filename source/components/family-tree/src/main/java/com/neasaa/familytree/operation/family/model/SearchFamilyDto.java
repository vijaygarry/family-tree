package com.neasaa.familytree.operation.family.model;

import com.neasaa.familytree.entity.Family;
import lombok.Builder;
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
    private String familyDisplayName;

    public static SearchFamilyDto getSearchFamilyDtoFromEntity(Family family) {
        SearchFamilyDto dto = new SearchFamilyDto();
        dto.setFamilyId(family.getFamilyId());
        dto.setFamilyName(family.getFamilyName());
        dto.setFamilyNameInHindi(family.getFamilyNameInHindi());
        dto.setGotra(family.getGotra());
        dto.setRegion(family.getRegion());
        dto.setPhone(family.getPhone());
        dto.setFamilyDisplayName(family.getFamilyDisplayName());
        return dto;
    }
}
