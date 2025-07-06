package com.neasaa.familytree.operation.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetMemberProfileRequest {
    private Integer memberId;

    public static GetMemberProfileRequest getGetMemberProfileRequest(Integer memberId) {
        GetMemberProfileRequest request = new GetMemberProfileRequest();
        request.setMemberId(memberId);
        return request;
    }
}
