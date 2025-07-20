package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationResponse;
import com.neasaa.familytree.entity.Family;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter
@Setter
public class GetFamilyDetailsResponse extends OperationResponse {
    @Serial
    private static final long serialVersionUID = 1L;

    private int familyId;
    private String familyName;
    private String familyNameInHindi;
    private String gotra;
    private String phone;
    private boolean isPhoneWhatsappRegistered;
    private String email;
    private int addressId;
    private AddressDto familyAddress;
    private String familyImage;
    private FamilyMemberDto familyTreeRoot;

    public static GetFamilyDetailsResponse fromFamilyDBEntity(Family family, FamilyMemberDto familyTreeRoot) {
        GetFamilyDetailsResponse response = new GetFamilyDetailsResponse();
        response.setFamilyId(family.getFamilyId());
        response.setFamilyName(family.getFamilyName());
        response.setFamilyNameInHindi(family.getFamilyNameInHindi());
        response.setGotra(family.getGotra());
        response.setPhone(family.getPhone());
        response.setPhoneWhatsappRegistered(family.isPhoneWhatsappRegistered());
        response.setEmail(family.getEmail());
        response.setAddressId(family.getAddressId());
        if (family.getAddress() != null) {
            AddressDto addressDto = AddressDto.getAddressDtoFromEntity(family.getAddress());
            response.setFamilyAddress(addressDto);
        }
        response.setFamilyImage(family.getFamilyImage());
        response.setFamilyTreeRoot(familyTreeRoot);
        return response;
    }
}
