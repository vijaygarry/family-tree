package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchFamilyMemberRequest extends OperationRequest {
    private String searchString;
    private String gender;
    private Integer ageFrom;
    private Integer ageTo;
    private String maritalStatus;

    @Override
    public void normalize() {
        if (searchString != null) {
            searchString = searchString.trim();
        }
        if (gender != null) {
            gender = gender.trim();
        }
        if (maritalStatus != null) {
            maritalStatus = maritalStatus.trim();
        }
    }
}
