package com.neasaa.familytree.operation.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class AddFamilyRequest {
    private String familyName;
    private String gotra;
    private String phone;
    private boolean isPhoneWhatsappRegistered;
    private String email;
    private InputAddress address;
}
