package com.neasaa.base.app.operation.session;

import static com.neasaa.base.app.operation.model.ChangePasswordRequest.getChangePasswordRequestBody;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import com.neasaa.base.app.BaseAppAbstractTest;
import com.neasaa.base.app.utils.Config;
import org.junit.jupiter.api.Test;

import io.restassured.http.ContentType;

public class ChangePasswordApiTest extends BaseAppAbstractTest {

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

    	String sessionId = loginWithSuperUserAndGetJSessionId();
        
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

    	String sessionId = loginWithSuperUserAndGetJSessionId();
        
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
        logoutUser(sessionId);
    }
	
	@Test
    public void testChangePasswordSuccess() {

    	String sessionId = loginWithSuperUserAndGetJSessionId();
        
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
        logoutUser(sessionId);
    }
}
