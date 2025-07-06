package com.neasaa.familytree.operation.family;

import com.neasaa.base.app.BaseAppAbstractTest;
import com.neasaa.familytree.operation.model.GetMemberProfileRequest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;

public class GetMemberProfileTest extends BaseAppAbstractTest {
    private static final String getMemberProfileUrl = "family/getmemberprofile";

    @Test
    public void getMemberProfile_successTest () {
        String sessionId = loginWithSuperUserAndGetJSessionId();
        Response response = executeOperation(new GetMemberProfileRequest(), sessionId, getMemberProfileUrl);
        response.then().log().all(); // Logs full response
        response.then()
                .body("memberProfile.firstName", equalTo("Vijay"))
                .body("memberProfile.lastName", equalTo("Garothaya"))
                .body("memberProfile.memberId", equalTo(4));

        response = executeOperation(GetMemberProfileRequest.getGetMemberProfileRequest(10), sessionId, getMemberProfileUrl);
        response.then().log().all(); // Logs full response
        response.then()
                .body("memberProfile.firstName", equalTo("Vijay"))
                .body("memberProfile.lastName", equalTo("Garothaya"))
                .body("memberProfile.memberId", equalTo(10));

        logoutUser(sessionId);
    }
}
