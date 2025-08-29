package com.neasaa.familytree.entity;

import com.neasaa.base.app.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter
@Setter
public class SearchFamilyEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1;

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

}
