package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationResponse;
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
    private AddressDto familyAddress;
    private String familyImage;
    private FamilyMemberDto familyTreeRoot;
    
}
