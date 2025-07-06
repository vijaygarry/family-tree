package com.neasaa.base.app;

import com.neasaa.base.app.operation.model.LoginRequest;
import com.neasaa.base.app.utils.Config;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

/**
 * <pre>
 * Run all integration tests:
 *   ./gradlew test
 * Run specific test:
 *   ./gradlew test --tests com.neasaa.base.app.operation.session.LoginApiTest
 * Run specific test method:
 *   ./gradlew test --tests com.neasaa.base.app.operation.session.LoginApiTest.testLogoutSuccess
 *  </pre>
 */
public class BaseAppAbstractTest {

    public static final int VALIDATION_ERROR_CODE = 400;
    public static final int SUCCESS_HTTP_CODE = 200;

    public static void setUrl () {
        RestAssured.baseURI = Config.BASE_APP_URL;
        RestAssured.port = Config.BASE_APP_PORT;
    }

    public LoginRequest getLoginRequestBody (String userName, String userPassword) {
        return LoginRequest.builder().loginName(userName).password(userPassword).build();
    }

    public String loginWithSuperUserAndGetJSessionId () {
        return given()
                .contentType(ContentType.JSON)
                .body(getLoginRequestBody(Config.SUPER_ADMIN_USER_NAME_1, Config.SUPER_ADMIN_USER_PWD_1))
                //.log().all() // Logs full request
                .when()
                .post("/session/login")
                .then()
                //.log().all() // Logs full response
                .statusCode(200)
                .body("firstName", equalTo("Vijay"))
                .body("lastName", equalTo("Garothaya"))
                .body("emailId", equalTo("vijay_garry@hotmail.com"))
                .body("logonName", equalTo("vijay.garry"))
                .cookie("JSESSIONID")
                .extract()
                .cookie("JSESSIONID");
    }

    public Response logoutUser (String sessionId) {
        return given()
                .contentType(ContentType.JSON)
                .cookie("JSESSIONID", sessionId)
                .log().all() // Logs full request
                .when()
                .post("/session/logout");
    }

    public static Response executeOperation (Object request, String jSessionId, String operationUrl) {
        return given()
                .contentType(ContentType.JSON)
                .body(request)
                .cookie("JSESSIONID", jSessionId)
                .log().all()
                .when()
                .post(operationUrl);
    }
}
