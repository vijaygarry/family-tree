package com.neasaa.base.app.operation.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ChangePasswordRequest {
    private String currentPassword;
    private String newPassword;

    public static ChangePasswordRequest getChangePasswordRequestBody (String currentPassword, String newPassword) {
        return ChangePasswordRequest.builder().currentPassword(currentPassword).newPassword(newPassword).build();
    }
}
