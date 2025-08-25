package com.neasaa.familytree.operation.session;

import com.neasaa.base.app.operation.model.OperationResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class WhoAmIResponse extends OperationResponse {
    private String firstName;
    private String lastName;
    private boolean sessionActive;
    private Date lastAccessTime;
    private int memberId;
    private int familyId;
    private String profileImageThumbnail;
}
