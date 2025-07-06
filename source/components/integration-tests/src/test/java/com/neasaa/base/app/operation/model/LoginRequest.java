package com.neasaa.base.app.operation.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class LoginRequest {
    private String loginName;
    private String password;
}