package com.neasaa.base.app.operation.session;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static com.neasaa.base.app.operation.session.TestHelper.getLoginRequestBody;
import static com.neasaa.base.app.operation.session.TestHelper.logoutUser;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class LoginApiTest {
	
	// ./gradlew test --tests com.neasaa.base.app.operation.session.LoginApiTest
	// ./gradlew test --tests com.neasaa.base.app.operation.session.LoginApiTest.testLogoutSuccess
	
	@BeforeAll
    public static void setup() {
        RestAssured.baseURI = Config.BASE_APP_URL;
        RestAssured.port = Config.BASE_APP_PORT;
    }
	
	@Test
    public void testLoginSuccess() {
        given()
            .contentType(ContentType.JSON)
            .body(getLoginRequestBody(Config.SUPER_ADMIN_USER_NAME_1, Config.SUPER_ADMIN_USER_PWD_1))
            .log().all() // Logs full request
        .when()
            .post("/session/login")
        .then()
            .log().all() // Logs full response
            .statusCode(200)
            .body("firstName", equalTo("Vijay"))
            .body("lastName", equalTo("Garothaya"))
            .body("emailId", equalTo("vijay_garry@hotmail.com"))
            .body("logonName", equalTo("vijay.garry"))
            .cookie("JSESSIONID");
//            .body("token", notNullValue());
    }

    @Test
    public void testInvalidLogin() {
        given()
            .contentType(ContentType.JSON)
            .body(getLoginRequestBody("john_doe", "wrong"))
            .log().all() // Logs full request
        .when()
            .post("/session/login")
        .then()
        	.log().all() // Logs full response
            .statusCode(401)
            .body("operationMessage", containsString("Invalid"));
    }
    
    @Test
    public void testLogoutSuccess() {

    	String sessionId = TestHelper.loginAndGetJSessionId(Config.SUPER_ADMIN_USER_NAME_1, Config.SUPER_ADMIN_USER_PWD_1);
        
        given()
        	.contentType(ContentType.JSON)
        	.cookie("JSESSIONID", sessionId) 
        	.log().all() // Logs full request
        .when()
        	.post("/session/logout")
        .then()
	        .log().all() // Logs full response
	        .statusCode(200)
	        .body("operationMessage", equalTo("Logout successful!!!"));
       
        
        given()
	    	.contentType(ContentType.JSON)
	    	.cookie("JSESSIONID", sessionId) 
	    	.log().all() // Logs full request
	    .when()
    		.post("/session/logout")
    	.then()
        	.log().all() // Logs full response
        	.statusCode(200)
        	.body("operationMessage", equalTo("No session exists"));
    }
    
    @Test
    public void testGetSessionDetail() {

    	String sessionId = TestHelper.loginAndGetJSessionId(Config.SUPER_ADMIN_USER_NAME_1, Config.SUPER_ADMIN_USER_PWD_1);
        
        given()
        	.contentType(ContentType.JSON)
        	.cookie("JSESSIONID", sessionId) 
        	.log().all() // Logs full request
        .when()
        	.post("/session/getsessiondetail")
        .then()
	        .log().all() // Logs full response
	        .statusCode(200)
	        .body("firstName", equalTo("Vijay"))
            .body("lastName", equalTo("Garothaya"))
            .body("emailId", equalTo("vijay_garry@hotmail.com"))
            .body("logonName", equalTo("vijay.garry"))
            .body("sessionActive", equalTo(true));
        
        logoutUser(sessionId);
    }
    
    @Builder
    @Getter
    @Setter
    public static class LoginRequest {
    	private String loginName;
    	private String password;
    }
    
    @Builder
    @Getter
    @Setter
    public static class ChangePasswordRequest {
    	private String currentPassword;
    	private String newPassword;
    }  
    
}
