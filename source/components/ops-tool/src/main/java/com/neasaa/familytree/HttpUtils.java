package com.neasaa.familytree;

import com.neasaa.base.app.operation.session.model.LoginResponse;
import com.neasaa.base.app.operation.session.model.LogoutRequest;
import com.neasaa.familytree.dto.OpsLoginRequest;
import com.neasaa.familytree.operation.family.model.AddFamilyMemberRequest;
import com.neasaa.familytree.operation.family.model.AddFamilyMemberResponse;
import com.neasaa.familytree.operation.family.model.ProcessFamilyRegistrationRequest;
import com.neasaa.familytree.operation.family.model.ProcessFamilyRegistrationResponse;
import com.neasaa.http.ApiClient;
import com.neasaa.http.ApiRequest;
import com.neasaa.http.ApiResponse;
import com.neasaa.util.config.BaseConfig;
import lombok.extern.log4j.Log4j2;

import static com.neasaa.http.ApiRequest.SESSION_COOKIE_NAME;

@Log4j2
public class HttpUtils {


    private static final String BASE_URL= "BASE.URL";
    private static final String ADMIN_USER_NAME= "ADMIN.USER.NAME";
    private static final String ADMIN_PASSWORD= "ADMIN.PASSWORD";

    public static final String LOGIN_URL = "/api/session/login";
    public static final String LOGOUT_URL = "/api/session/logout";
    public static final String WHO_AM_I_URL = "/api/session/whoAmI";
    public static final String CHANGE_PASSWORD_URL = "/api/session/changepassword";
    public static final String ADD_FAMILY_URL = "/api/family/addfamily";
    public static final String ADD_FAMILY_MEMBER_URL = "/api/family/addFamilyMember";
    public static final String PROCESS_FAMILY_REGISTRATION_URL = "/api/family/processFamilyRegistrationRequest";
    public static final String GET_FAMILY_DETAILS_URL = "/api/family/getfamilydetails";
    public static final String MANAGE_RELATIONSHIP_URL = "/api/family/manageRelationship";

    private final String baseUrl;
    private final String adminUserName;
    private final String adminPassword;

    private final ApiClient apiClient = new ApiClient();
    private String sessionId;

    public HttpUtils () throws Exception {
        BaseConfig.initialize("ops-tool.properties");
        this.baseUrl = BaseConfig.getProperty(BASE_URL);
        log.info("Base URL: {}", baseUrl);
        this.adminUserName = BaseConfig.getProperty(ADMIN_USER_NAME);
        this.adminPassword = BaseConfig.getProperty(ADMIN_PASSWORD);
    }

    public void createSession () throws Exception {
        OpsLoginRequest loginRequest = OpsLoginRequest.builder()
                .loginName(adminUserName)
                .password(adminPassword)
                .build();
        ApiRequest<OpsLoginRequest> request =
                ApiRequest.<OpsLoginRequest>builder().baseUrl(baseUrl)
                        .contextPath(LOGIN_URL)
                        .requestBody(loginRequest)
                        .build();
        request.addDefaultHeaders();
        ApiResponse<LoginResponse> loginResponseApiResponse = apiClient.processRequest(request, LoginResponse.class);
        if(loginResponseApiResponse.getHttpStatusCode() != 200) {
            throw new Exception("Failed to create session. HTTP Status Code: " + loginResponseApiResponse.getHttpStatusCode() + " Message: " + loginResponseApiResponse.getResponseBody());
        }
        sessionId = loginResponseApiResponse.getCookie(SESSION_COOKIE_NAME);
    }

    public void whoAmI () {
        ApiRequest<Void> request =
                ApiRequest.<Void>builder().baseUrl(baseUrl)
                        .contextPath(WHO_AM_I_URL)
                        .build();
        request.addDefaultHeaders();
        request.addSessionCookie(sessionId);
        try {
            ApiResponse<String> response = apiClient.processRequest(request, String.class);
            if(response.getHttpStatusCode() == 200) {
                log.info("Current session user details: {}", response.getResponseBody());
            } else {
                log.error("Failed to get session details. HTTP Status Code: {} Message: {}", response.getHttpStatusCode(), response.getResponseBody());
            }
        } catch (Exception e) {
            log.error("Exception while getting session details: ", e);
        }
    }

    public void logout () throws Exception {
        LogoutRequest logoutRequest = new LogoutRequest();
        ApiRequest<LogoutRequest> request =
                ApiRequest.<LogoutRequest>builder().baseUrl(baseUrl)
                        .contextPath(LOGOUT_URL)
                        .requestBody(logoutRequest)
                        .build();
        request.addDefaultHeaders();
        request.addSessionCookie(sessionId);
        ApiResponse<Void> logoutResponseApiResponse = apiClient.processRequest(request, Void.class);

        if(logoutResponseApiResponse.getHttpStatusCode() != 200) {
            throw new Exception("Failed to logout. HTTP Status Code: " + logoutResponseApiResponse.getHttpStatusCode() + " Message: " + logoutResponseApiResponse.getResponseBody());
        }
    }

    public int addFamilyMember(AddFamilyMemberRequest addFamilyMemberRequest) throws Exception {
        ApiRequest<AddFamilyMemberRequest> request =
                ApiRequest.<AddFamilyMemberRequest>builder().baseUrl(baseUrl)
                        .contextPath(ADD_FAMILY_MEMBER_URL)
                        .requestBody(addFamilyMemberRequest)
                        .build();
        request.addDefaultHeaders();
        request.addSessionCookie(sessionId);
        ApiResponse<AddFamilyMemberResponse> response = apiClient.processRequest(request, AddFamilyMemberResponse.class);
        if(response.getHttpStatusCode() != 200) {
            throw new Exception("Failed to add family member. HTTP Status Code: " + response.getHttpStatusCode() + " Message: " + response.getResponseBody());
        }
        log.info("Added member: {} with member ID: {}", addFamilyMemberRequest.getFirstName(), response.getResponse().getMemberId());
        return response.getResponse().getMemberId();
    }

    public int processFamilyRegistrationRequest (ProcessFamilyRegistrationRequest processFamilyRegistrationRequest) throws Exception {
        ApiRequest<ProcessFamilyRegistrationRequest> request =
                ApiRequest.<ProcessFamilyRegistrationRequest>builder().baseUrl(baseUrl)
                        .contextPath(PROCESS_FAMILY_REGISTRATION_URL)
                        .requestBody(processFamilyRegistrationRequest)
                        .build();

        request.addDefaultHeaders();
        request.addSessionCookie(sessionId);
        ApiResponse<ProcessFamilyRegistrationResponse> response = apiClient.processRequest(request, ProcessFamilyRegistrationResponse.class);
        if(response.getHttpStatusCode() != 200) {
            throw new Exception("Failed to process family request. HTTP Status Code: " + response.getHttpStatusCode() + " Message: " + response.getResponseBody());
        }

        return response.getResponse().getFamilyId();
    }

}
