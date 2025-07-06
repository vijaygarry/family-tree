package com.neasaa.familytree.operation.family;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

import java.time.Year;

import com.neasaa.base.app.BaseAppAbstractTest;
import com.neasaa.familytree.operation.model.InputAddress;
import com.neasaa.familytree.operation.model.InputRelationship;
import org.junit.jupiter.api.Test;

import com.neasaa.base.app.utils.AssertionUtils;

import io.restassured.response.Response;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class AddFamilyMemberTest extends BaseAppAbstractTest {
	
	private static final String addFamilyMemberUrl = "family/addfamilymember";
	private static final int validTestFamilyId = 2;
	@Test
	public void addFamilyMemberInputValidationTest () {
		
		AddFamilyMemberRequest request = AddFamilyMemberRequest.builder().build();
		String sessionId = loginWithSuperUserAndGetJSessionId();
		
		executeOperationAndAssertResponse(request, sessionId, VALIDATION_ERROR_CODE, "Invalid value for field family id");
		
		request.setFamilyId(0);
		executeOperationAndAssertResponse(request, sessionId, VALIDATION_ERROR_CODE, "Invalid value for field family id");
		
		request.setFamilyId(validTestFamilyId);
		executeOperationAndAssertResponse(request, sessionId, VALIDATION_ERROR_CODE, "Required field first name is not provided");
		
		request.setFirstName("Vijay");
		executeOperationAndAssertResponse(request, sessionId, VALIDATION_ERROR_CODE, "Required field gender is not provided");
		
		request.setGender("wrong value");
		executeOperationAndAssertResponse(request, sessionId, VALIDATION_ERROR_CODE, "Invalid value for field gender");
		
		request.setGender("Male");
		executeOperationAndAssertResponse(request, sessionId, VALIDATION_ERROR_CODE, "Required field birth month is not provided");
		
		request.setBirthDay((short)120);
		executeOperationAndAssertResponse(request, sessionId, VALIDATION_ERROR_CODE, "Invalid value for field birth day");
		
		request.setBirthDay((short)-1);
		executeOperationAndAssertResponse(request, sessionId, VALIDATION_ERROR_CODE, "Invalid value for field birth day");
		
		request.setBirthDay((short)20);
		request.setBirthMonth((short)0);
		executeOperationAndAssertResponse(request, sessionId, VALIDATION_ERROR_CODE, "Invalid value for field birth month");
		
		request.setBirthMonth((short)13);
		executeOperationAndAssertResponse(request, sessionId, VALIDATION_ERROR_CODE, "Invalid value for field birth month");
		
		request.setBirthMonth((short)6);
		executeOperationAndAssertResponse(request, sessionId, VALIDATION_ERROR_CODE, "Required field birth year is not provided");
		
		request.setBirthYear((short)1890);
		executeOperationAndAssertResponse(request, sessionId, VALIDATION_ERROR_CODE, "Invalid value for field birth year");
		
		int nextYear = Year.now().getValue() + 1;
		request.setBirthYear((short)nextYear);
		executeOperationAndAssertResponse(request, sessionId, VALIDATION_ERROR_CODE, "Invalid value for field birth year");
		
		request.setBirthYear((short)1978);
		executeOperationAndAssertResponse(request, sessionId, VALIDATION_ERROR_CODE, "Required field marital status is not provided");
		
		//Check for valid value for marital status
		request.setMaritalStatus("Testing");
		executeOperationAndAssertResponse(request, sessionId, VALIDATION_ERROR_CODE, "Invalid value for field marital status");
		
		request.setMaritalStatus("Married");
		
		InputAddress address = InputAddress.builder().build();
		request.setMemberAddress(address);
		executeOperationAndAssertResponse(request, sessionId, VALIDATION_ERROR_CODE, "Required field address line-1 is not provided");
		
		address.setAddressLine1("Budhwara Chowk, Matakhidki Road");
		executeOperationAndAssertResponse(request, sessionId, VALIDATION_ERROR_CODE, "Required field city is not provided");
		
		address.setCity("Amravati");
		executeOperationAndAssertResponse(request, sessionId, VALIDATION_ERROR_CODE, "Required field state is not provided");
		
		address.setState("Maharastra");
		executeOperationAndAssertResponse(request, sessionId, VALIDATION_ERROR_CODE, "Required field country is not provided");
		
		address.setCountry("India");
		executeOperationAndAssertResponse(request, sessionId, VALIDATION_ERROR_CODE, "Required field postal code is not provided");
		
		address.setPostalCode("444601");
		request.setAddressSameAsFamily(true);
		executeOperationAndAssertResponse(request, sessionId, VALIDATION_ERROR_CODE, "Either select address same as family address or specify member address");
		
		request.setAddressSameAsFamily(false);
		request.setPhone("abc");
		executeOperationAndAssertResponse(request, sessionId, VALIDATION_ERROR_CODE, "Invalid phone number provided");
		
		logoutUser(sessionId);
	}
	
	@Test
	public void addFamilyMemberInvalidValueTest () {
		
		String sessionId = loginWithSuperUserAndGetJSessionId();
		
		InputAddress address = InputAddress.getValidTestAddress();
		InputRelationship relationship = InputRelationship.builder().relatedMemberId(2).relationshipType("Son").relatedMemberFullName("Bhagwatnarayan Garothaya").build();
		AddFamilyMemberRequest request = AddFamilyMemberRequest.builder()
				.familyId(98987) //Family does not exists
				.firstName("Vijay")
				.nickName("My nick name")
				.birthDay((short)20)
				.birthMonth((short)6)
				.birthYear((short)1978)
				.gender("Male")
				.maritalStatus("Married")
				.phone("+91 571-484-3772")
				.memberAddress(address)
				.build();
		
		executeOperationAndAssertResponse(request, sessionId, VALIDATION_ERROR_CODE, "Family not found");
		request.setFamilyId(validTestFamilyId);
		request.setRelashinship(relationship);
//		executeOperationAndAssertResponse(request, sessionId, VALIDATION_ERROR_CODE, "First member should be the head of family");
//		request.setHeadOfFamily(true);
		Response response = executeOperationAndAssertResponse(request, sessionId, SUCCESS_HTTP_CODE, "Member Vijay Garothaya added successfully !!!");
		response.then()
			.body("firstName", equalTo("Vijay"))
			.body("lastName", equalTo("Garothaya"))
			.body("memberId", greaterThan(0));
		
		logoutUser(sessionId);
	}
	private Response executeOperationAndAssertResponse (AddFamilyMemberRequest request, String sessionId, int httpResponseCode, String responseMessage) {
		Response response = executeOperation(request, sessionId, addFamilyMemberUrl);
		response.then().log().all(); // Logs full response
		AssertionUtils.assertResponse(response, httpResponseCode, responseMessage);
		return response;
	}
	
	@Builder
    @Getter
    @Setter
	private static class AddFamilyMemberRequest {
		private int familyId;
		private boolean headOfFamily;
		private String firstName;
		private String lastName;
		private String maidenLastName;
		private String nickName;
		private boolean addressSameAsFamily;
		private InputAddress memberAddress;
		private String phone;
		private boolean isPhoneWhatsappRegistered;
		private String email;
		private String linkedinUrl;
		private String gender;
		private Short birthDay;
		private Short birthMonth;
		private Short birthYear;
		private String maritalStatus;
		private String educationDetails;
		private String occupation;
		private String workingAt;
		private String hobby;
		private String profileImage;
		private String profileImageThumbnail;
		
		private InputRelationship relashinship;
	}
}
