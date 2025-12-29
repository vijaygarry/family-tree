package com.neasaa.familytree.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class ExcelFamilyMember {
    private int familyId;
    private int memberId;
    private boolean headOfFamily;
    private String firstName;
    private String firstNameInHindi;
    private String nickName;
    private boolean addressSameAsFamily;
    private String phone;
    private String email;
    private String gender;
    private Short birthDay;
    private String birthMonth;
    private Short birthYear;
    private String maritalStatus;
    private Date weddingDate;
    private String educationDetails;
    private String occupation;
    private String spouseName;
    private List<String> childrenNamesList;
//    private RelationshipDto relationship;
    private int excelRowNumber;
}
