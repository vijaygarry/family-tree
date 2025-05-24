package com.neasaa.base.app.operation.session;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

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
	
	private static LoginRequest getLoginRequestBody (String userName, String userPassword) {
		return LoginRequest.builder().loginName(userName).password(userPassword).build();
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
        String sessionId = given()
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
            .cookie("JSESSIONID")
            .extract()
            .cookie("JSESSIONID");
        
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
    
    @Builder
    @Getter
    @Setter
    private static class LoginRequest {
    	private String loginName;
    	private String password;
    }    
    
}
