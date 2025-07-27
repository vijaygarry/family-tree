package com.neasaa.base.app.operation.session;

import static com.neasaa.util.Constants.GET_SESSION_DETAIL_URL;
import static com.neasaa.util.Constants.LOGIN_URL;
import static com.neasaa.util.Constants.LOGOUT_URL;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import com.neasaa.base.app.BaseAppAbstractTest;
import com.neasaa.base.app.utils.Config;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.restassured.http.ContentType;

public class LoginApiTest extends BaseAppAbstractTest {
	
	@BeforeAll
    public static void setup() {
        setUrl();
    }
	
	@Test
    public void testLoginSuccess() {
        given()
            .contentType(ContentType.JSON)
            .body(getLoginRequestBody(Config.SUPER_ADMIN_USER_NAME_1, Config.SUPER_ADMIN_USER_PWD_1))
            .log().all() // Logs full request
        .when()
            .post(LOGIN_URL)
        .then()
            .log().all() // Logs full response
            .statusCode(200)
            .body("firstName", equalTo("Vijay"))
            .body("lastName", equalTo("Garothaya"))
            .body("emailId", equalTo("vijay_garry@hotmail.com"))
            .body("logonName", equalTo("vijay"))
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
            .post(LOGIN_URL)
        .then()
        	.log().all() // Logs full response
            .statusCode(401)
            .body("operationMessage", containsString("Invalid"));
    }
    
    @Test
    public void testLogoutSuccess() {

    	String sessionId = loginWithSuperUserAndGetJSessionId();
        
        given()
        	.contentType(ContentType.JSON)
        	.cookie("JSESSIONID", sessionId) 
        	.log().all() // Logs full request
        .when()
        	.post(LOGOUT_URL)
        .then()
	        .log().all() // Logs full response
	        .statusCode(200)
	        .body("operationMessage", equalTo("Logout successful!!!"));
       
        
        given()
	    	.contentType(ContentType.JSON)
	    	.cookie("JSESSIONID", sessionId) 
	    	.log().all() // Logs full request
	    .when()
    		.post(LOGOUT_URL)
    	.then()
        	.log().all() // Logs full response
        	.statusCode(200)
        	.body("operationMessage", equalTo("No session exists"));
    }
    
    @Test
    public void testGetSessionDetail() {

    	String sessionId = loginWithSuperUserAndGetJSessionId();
        
        given()
        	.contentType(ContentType.JSON)
        	.cookie("JSESSIONID", sessionId) 
        	.log().all() // Logs full request
        .when()
        	.post(GET_SESSION_DETAIL_URL)
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
}
