package com.neasaa.familytree.operation.family;

import com.neasaa.familytree.operation.FamilyTreeAbstractTest;
import com.neasaa.familytree.operation.model.AddFamilyRequest;
import com.neasaa.familytree.operation.model.InputAddress;
import org.junit.jupiter.api.Test;

import com.neasaa.base.app.utils.AssertionUtils;

import io.restassured.response.Response;

public class AddFamilyTest extends FamilyTreeAbstractTest {
	
	private static final String addFamilyUrl = "family/addfamily";
	
	@Test
	public void addFamilyInputValidationTest () {
		InputAddress address = InputAddress.builder().build();
		AddFamilyRequest request = AddFamilyRequest.builder().build();
		String sessionId = loginWithSuperUserAndGetJSessionId();
		Response response = executeOperation(request, sessionId, addFamilyUrl);
		response.then().log().all(); // Logs full response
		AssertionUtils.assertResponse(response, 400, "Required field family name is not provided");
		
		request.setFamilyName("Garothaya");
		response = executeOperation(request, sessionId, addFamilyUrl);
		response.then().log().all(); // Logs full response
		AssertionUtils.assertResponse(response, 400, "Required field gotra is not provided");
		
		request.setGotra("My Gotra");
		response = executeOperation(request, sessionId, addFamilyUrl);
		response.then().log().all(); // Logs full response
		AssertionUtils.assertResponse(response, 400, "Required field address is not provided");
		
		request.setAddress(address);
		response = executeOperation(request, sessionId, addFamilyUrl);
		response.then().log().all(); // Logs full response
		AssertionUtils.assertResponse(response, 400, "Required field address line-1 is not provided");
		
		address.setAddressLine1("Budhwara Chowk, Matakhidki Road");
		response = executeOperation(request, sessionId, addFamilyUrl);
		response.then().log().all(); // Logs full response
		AssertionUtils.assertResponse(response, 400, "Required field city is not provided");
		
		address.setCity("Amravati");
		response = executeOperation(request, sessionId, addFamilyUrl);
		response.then().log().all(); // Logs full response
		AssertionUtils.assertResponse(response, 400, "Required field state is not provided");
		
		address.setState("Maharastra");
		response = executeOperation(request, sessionId, addFamilyUrl);
		response.then().log().all(); // Logs full response
		AssertionUtils.assertResponse(response, 400, "Required field country is not provided");
		
		address.setCountry("India");
		response = executeOperation(request, sessionId, addFamilyUrl);
		response.then().log().all(); // Logs full response
		AssertionUtils.assertResponse(response, 400, "Required field postal code is not provided");
		
		logoutUser(sessionId);
	}
	
	
	@Test
	public void addFamilySuccessTest () {
		String sessionId = loginWithSuperUserAndGetJSessionId();
		AddFamilyRequest request = AddFamilyRequest.builder().build();
		request.setFamilyName("Garothaya");
		request.setGotra("My Gotra");	
		request.setAddress(InputAddress.getValidTestAddress());
		
		Response response = executeOperation(request, sessionId, addFamilyUrl);
		response.then().log().all(); // Logs full response
		AssertionUtils.assertResponse(response, 200, "Garothaya family added successfully !!!");
		logoutUser(sessionId);
	}
	
}
