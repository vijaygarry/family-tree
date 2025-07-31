package com.neasaa.familytree.operation.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class AddFamilyRequest {

    private int familyId;
    private String familyName;
    private String familyNameInHindi;
    private String gotra;
    private String phone;
    private boolean isPhoneWhatsappRegistered;
    private String email;
    private InputAddress address;
}
