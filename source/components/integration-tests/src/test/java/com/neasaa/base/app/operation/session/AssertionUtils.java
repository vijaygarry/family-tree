package com.neasaa.base.app.operation.session;

import static org.hamcrest.Matchers.equalTo;

import io.restassured.response.Response;

public class AssertionUtils {
	
	public static void assertResponse(Response apiResponse, int expectedHttpCode, String expectedResponseMessage) {
		apiResponse.then()
        .log().all()
        .statusCode(expectedHttpCode)
        .body("operationMessage", equalTo(expectedResponseMessage)); 
		
//		assertEquals(expectedHttpCode, apiResponse.getStatusCode());
//		assertEquals(expectedResponseMessage, apiResponse.jsonPath().getString("operationMessage"));
	}
}
