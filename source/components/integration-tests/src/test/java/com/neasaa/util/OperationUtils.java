package com.neasaa.util;

import com.neasaa.base.app.operation.model.LoginRequest;
import com.neasaa.base.app.utils.Config;
import com.neasaa.familytree.operation.model.AddFamilyMemberRequest;
import com.neasaa.familytree.operation.model.AddFamilyRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static com.neasaa.util.Constants.ADD_FAMILY_MEMBER_URL;
import static com.neasaa.util.Constants.ADD_FAMILY_URL;
import static com.neasaa.util.Constants.LOGIN_URL;
import static com.neasaa.util.Constants.LOGOUT_URL;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class OperationUtils {

    private static final boolean PRINT_ALL_REQUESTS = true;

    public static String loginWithSuperUserAndGetJSessionId () {
        return given()
                .contentType(ContentType.JSON)
                .body(getLoginRequestBody(Config.SUPER_ADMIN_USER_NAME_1, Config.SUPER_ADMIN_USER_PWD_1))
                //.log().all() // Logs full request
                .when()
                .post(LOGIN_URL)
                .then()
                //.log().all() // Logs full response
                .statusCode(200)
                .body("firstName", equalTo("Vijay"))
                .body("lastName", equalTo("Garothaya"))
                .body("emailId", equalTo("vijay_garry@hotmail.com"))
                .body("logonName", equalTo("vijay"))
                .cookie("JSESSIONID")
                .extract()
                .cookie("JSESSIONID");
    }

    public static LoginRequest getLoginRequestBody (String userName, String userPassword) {
        return LoginRequest.builder().loginName(userName).password(userPassword).build();
    }

    public static Response addFamily (AddFamilyRequest request, String sessionId) {
        Response response = executeOperation(request, sessionId, ADD_FAMILY_URL);
        response.then().log().all(); // Logs full response
        return response;
    }

    public static Response addFamilyMember (AddFamilyMemberRequest request, String sessionId) {
        Response response = executeOperation(request, sessionId, ADD_FAMILY_MEMBER_URL);
        response.then().log().all(); // Logs full response
        return response;
    }

    public static Response executeOperation (Object request, String jSessionId, String operationUrl) {
        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON)
                .body(request)
                .cookie("JSESSIONID", jSessionId);
        if(PRINT_ALL_REQUESTS) {
            requestSpecification.log().all(); // Logs full request
        }
        return requestSpecification
                .when()
                .post(operationUrl);
    }

    public static Response logoutUser (String sessionId) {
        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON)
                .cookie("JSESSIONID", sessionId);
        if(PRINT_ALL_REQUESTS) {
            requestSpecification.log().all(); // Logs full request
        }
        return
                requestSpecification.when()
                .post(LOGOUT_URL);
    }
}
