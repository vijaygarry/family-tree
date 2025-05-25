package com.neasaa.base.app.operation.session;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import com.neasaa.base.app.operation.session.LoginApiTest.ChangePasswordRequest;
import com.neasaa.base.app.operation.session.LoginApiTest.LoginRequest;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class TestHelper {
	
	public static LoginRequest getLoginRequestBody (String userName, String userPassword) {
		return LoginRequest.builder().loginName(userName).password(userPassword).build();
	}
	
	public static ChangePasswordRequest getChangePasswordRequestBody (String currentPassword, String newPassword) {
		return ChangePasswordRequest.builder().currentPassword(currentPassword).newPassword(newPassword).build();
	}
	
	public static String loginAndGetJSessionId (String logonName, String userPassword) {
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
		return sessionId;
	}
	
	public static Response logoutUser (String sessionId) {
		return given()
	    	.contentType(ContentType.JSON)
	    	.cookie("JSESSIONID", sessionId) 
	    	.log().all() // Logs full request
	    .when()
    		.post("/session/logout");    	
	}
}
