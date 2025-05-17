package com.neasaa.base.app.operation.session;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.containsString;

public class LoginApiTest {
	
	@BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080; // Update if needed
    }
	
	@Test
    public void testLoginSuccess() {
        given()
            .contentType(ContentType.JSON)
            .body("{ \"userId\": \"john_doe\", \"password\": \"secret123\" }")
            .log().all() // Logs full request
        .when()
            .post("/session/login")
        .then()
            .log().all() // Logs full response
            .statusCode(200)
            .body("userId", equalTo("john_doe"))
            .body("token", notNullValue());
    }

    @Test
    public void testInvalidLogin() {
        given()
            .contentType(ContentType.JSON)
            .body("{ \"userId\": \"john_doe\", \"password\": \"wrong\" }")
            .log().all() // Logs full request
        .when()
            .post("/session/login")
        .then()
        	.log().all() // Logs full response
            .statusCode(401)
            .body("error", containsString("Invalid"));
    }
}
