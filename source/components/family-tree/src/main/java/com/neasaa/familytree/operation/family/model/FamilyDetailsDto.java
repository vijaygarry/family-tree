package com.neasaa.familytree.operation.family.model;

import com.neasaa.familytree.entity.FamilyEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FamilyDetailsDto {

    private int familyId;
    private String familyName;
    private String familyNameInHindi;
    private String gotra;
    private String headOfFamilyName;
    private String phone;
    private boolean isPhoneWhatsappRegistered;
    private String email;
    private AddressDto familyAddress;
    private String familyImage;
    // Is family edit allowed
    private boolean canUpdateFamilyDetails;

    public static FamilyDetailsDto fromFamilyDBEntity (FamilyEntity family, String headOfFamilyName, boolean canUpdateFamilyDetails) {
        FamilyDetailsDto familyDetails = new FamilyDetailsDto();
        familyDetails.setFamilyId(family.getFamilyId());
        familyDetails.setFamilyName(family.getFamilyName());
        familyDetails.setFamilyNameInHindi(family.getFamilyNameInHindi());
        familyDetails.setHeadOfFamilyName(headOfFamilyName);
        familyDetails.setGotra(family.getGotra());
        familyDetails.setPhone(family.getPhone());
        familyDetails.setPhoneWhatsappRegistered(family.isPhoneWhatsappRegistered());
        familyDetails.setEmail(family.getEmail());
        if (family.getAddress() != null) {
            AddressDto addressDto = AddressDto.getAddressDtoFromEntity(family.getAddress());
            familyDetails.setFamilyAddress(addressDto);
        }
        familyDetails.setFamilyImage(family.getFamilyImage());
        familyDetails.setCanUpdateFamilyDetails(canUpdateFamilyDetails);
        return familyDetails;
    }
}
