package com.neasaa.familytree.operation.family;

import org.junit.jupiter.api.Test;

import com.neasaa.base.app.operation.session.AssertionUtils;
import com.neasaa.base.app.operation.session.Config;
import com.neasaa.base.app.operation.session.TestHelper;

import io.restassured.response.Response;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class AddFamilyTest {
	
	private static final String addFamilyUrl = "family/addfamily";
	
	@Test
	public void addFamilyInputValidationTest () {
		FamilyAddress address = FamilyAddress.builder().build();
		AddFamilyRequest request = AddFamilyRequest.builder().build();
		String sessionId = TestHelper.loginAndGetJSessionId(Config.SUPER_ADMIN_USER_NAME_1, Config.SUPER_ADMIN_USER_PWD_1);
		Response response = TestHelper.executeOperation(request, sessionId, addFamilyUrl);
		response.then().log().all(); // Logs full response
		AssertionUtils.assertResponse(response, 400, "Required field family name is not provided");
		
		request.setFamilyName("Garothaya");
		response = TestHelper.executeOperation(request, sessionId, addFamilyUrl);
		response.then().log().all(); // Logs full response
		AssertionUtils.assertResponse(response, 400, "Required field gotra is not provided");
		
		request.setGotra("My Gotra");
		response = TestHelper.executeOperation(request, sessionId, addFamilyUrl);
		response.then().log().all(); // Logs full response
		AssertionUtils.assertResponse(response, 400, "Required field address is not provided");
		
		request.setAddress(address);
		response = TestHelper.executeOperation(request, sessionId, addFamilyUrl);
		response.then().log().all(); // Logs full response
		AssertionUtils.assertResponse(response, 400, "Required field address line-1 is not provided");
		
		address.setAddressLine1("Budhwara Chowk, Matakhidki Road");
		response = TestHelper.executeOperation(request, sessionId, addFamilyUrl);
		response.then().log().all(); // Logs full response
		AssertionUtils.assertResponse(response, 400, "Required field city is not provided");
		
		address.setCity("Amravati");
		response = TestHelper.executeOperation(request, sessionId, addFamilyUrl);
		response.then().log().all(); // Logs full response
		AssertionUtils.assertResponse(response, 400, "Required field state is not provided");
		
		address.setState("Maharastra");
		response = TestHelper.executeOperation(request, sessionId, addFamilyUrl);
		response.then().log().all(); // Logs full response
		AssertionUtils.assertResponse(response, 400, "Required field country is not provided");
		
		address.setCountry("India");
		response = TestHelper.executeOperation(request, sessionId, addFamilyUrl);
		response.then().log().all(); // Logs full response
		AssertionUtils.assertResponse(response, 400, "Required field postal code is not provided");
		
		address.setPostalCode("444601");
		response = TestHelper.executeOperation(request, sessionId, addFamilyUrl);
		response.then().log().all(); // Logs full response
		AssertionUtils.assertResponse(response, 200, "Garothaya family added successfully !!!");
	}
	
	
	@Test
	public void addFamilySuccessTest () {
		FamilyAddress address = FamilyAddress.builder().build();
		AddFamilyRequest request = AddFamilyRequest.builder().build();
		
	}
	
	@Builder
    @Getter
    @Setter
	private static class AddFamilyRequest {
		private String familyName;
		private String gotra;
		private String phone;
		private boolean isPhoneWhatsappRegistered;
		private String email;
		private FamilyAddress address;
	}
	
	@Builder
    @Getter
    @Setter
	private static class FamilyAddress {
		private String addressLine1;
		private String addressLine2;
		private String addressLine3;
		private String city;
		private String district;
		private String state;
		private String postalCode;
		private String country;
	}
}
