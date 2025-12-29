package com.neasaa.familytree.dto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Request model for OPS tool login
 * Not using the common LoginRequest from neasaa-base-app as password won't be added to json because of configuration
 */
@Getter
@Setter
@Builder
public class OpsLoginRequest {
    private String loginName;
    private String password;
}
