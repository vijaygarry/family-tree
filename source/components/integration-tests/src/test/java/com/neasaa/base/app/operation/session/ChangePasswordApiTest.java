package com.neasaa.base.app.operation.session;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static com.neasaa.base.app.operation.session.TestHelper.getChangePasswordRequestBody;

import org.junit.jupiter.api.Test;

import io.restassured.http.ContentType;

public class ChangePasswordApiTest {

	@Test
    public void testChangePasswordWithNoSession() {

    	given()
        	.contentType(ContentType.JSON)
        	.body(getChangePasswordRequestBody("abc", "abc"))
        	.log().all() // Logs full request
        .when()
        	.post("/session/changepassword")
        .then()
	        .log().all() // Logs full response
	        .statusCode(401)
	        .body("operationMessage", equalTo("Please login to perform this operation."));
    }
	
	@Test
    public void testChangePassword_InvalidCurrPAssword() {

    	String sessionId = TestHelper.loginAndGetJSessionId(Config.SUPER_ADMIN_USER_NAME_1, Config.SUPER_ADMIN_USER_PWD_1);
        
        given()
        	.contentType(ContentType.JSON)
        	.body(getChangePasswordRequestBody("wrongpwd", "abc"))
        	.cookie("JSESSIONID", sessionId) 
        	.log().all()
        .when()
        	.post("/session/changepassword")
        .then()
	        .log().all()
	        .statusCode(400)
	        .body("operationMessage", equalTo("Current password does not match.")); 
    }
	
	@Test
    public void testChangePassword_CurrentNewSamePwd() {

    	String sessionId = TestHelper.loginAndGetJSessionId(Config.SUPER_ADMIN_USER_NAME_1, Config.SUPER_ADMIN_USER_PWD_1);
        
        given()
        	.contentType(ContentType.JSON)
        	.body(getChangePasswordRequestBody("abc", "abc"))
        	.cookie("JSESSIONID", sessionId) 
        	.log().all()
        .when()
        	.post("/session/changepassword")
        .then()
	        .log().all()
	        .statusCode(400)
	        .body("operationMessage", equalTo("Current and new password can not be same.")); 
    }
	
	@Test
    public void testChangePasswordSuccess() {

    	String sessionId = TestHelper.loginAndGetJSessionId(Config.SUPER_ADMIN_USER_NAME_1, Config.SUPER_ADMIN_USER_PWD_1);
        
        given()
        	.contentType(ContentType.JSON)
        	.body(getChangePasswordRequestBody(Config.SUPER_ADMIN_USER_PWD_1, "newPassword"))
        	.cookie("JSESSIONID", sessionId) 
        	.log().all() // Logs full request
        .when()
        	.post("/session/changepassword")
        .then()
	        .log().all() // Logs full response
	        .statusCode(200)
	        .body("operationMessage", equalTo("Password change successfully !!!"));
       
        //Revert back the password
        given()
	    	.contentType(ContentType.JSON)
	    	.body(getChangePasswordRequestBody("newPassword", Config.SUPER_ADMIN_USER_PWD_1))
	    	.cookie("JSESSIONID", sessionId) 
	    	.log().all() // Logs full request
	    .when()
    		.post("/session/changepassword")
	    .then()
	        .log().all() // Logs full response
	        .statusCode(200)
	        .body("operationMessage", equalTo("Password change successfully !!!"));
    }
}
