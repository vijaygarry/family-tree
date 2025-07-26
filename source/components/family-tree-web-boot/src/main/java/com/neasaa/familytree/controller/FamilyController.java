package com.neasaa.familytree.controller;

import com.neasaa.base.app.operation.model.EmptyOperationRequest;
import com.neasaa.familytree.operation.account.GetAccountListOperation;
import com.neasaa.familytree.operation.account.GetAccountStatementOperation;
import com.neasaa.familytree.operation.account.model.GetAccountListResponse;
import com.neasaa.familytree.operation.account.model.GetAccountStatementRequest;
import com.neasaa.familytree.operation.account.model.GetAccountStatementResponse;
import com.neasaa.familytree.operation.events.GetEventsOperation;
import com.neasaa.familytree.operation.events.model.GetEventsRequest;
import com.neasaa.familytree.operation.events.model.GetEventsResponse;
import com.neasaa.familytree.operation.family.GetFamilyDetailsOperation;
import com.neasaa.familytree.operation.family.GetMemberProfileOperation;
import com.neasaa.familytree.operation.family.SearchFamilyOperation;
import com.neasaa.familytree.operation.family.model.AddFamilyMemberRequest;
import com.neasaa.familytree.operation.family.model.AddFamilyMemberResponse;
import com.neasaa.familytree.operation.family.model.AddFamilyRequest;
import com.neasaa.familytree.operation.family.model.AddFamilyResponse;
import com.neasaa.familytree.operation.family.model.GetFamilyDetailsRequest;
import com.neasaa.familytree.operation.family.model.GetFamilyDetailsResponse;
import com.neasaa.familytree.operation.family.model.GetMemberProfileRequest;
import com.neasaa.familytree.operation.family.model.GetMemberProfileResponse;
import com.neasaa.familytree.operation.family.model.SearchFamilyRequest;
import com.neasaa.familytree.operation.family.model.SearchFamilyResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.neasaa.familytree.WebRequestHandler;
import com.neasaa.familytree.operation.family.AddFamilyMemberOperation;
import com.neasaa.familytree.operation.family.AddFamilyOperation;

//import lombok.extern.log4j.Log4j2;

//@Log4j2
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping(value = "/api/family", method = RequestMethod.POST)
public class FamilyController {
	
	@RequestMapping(value = "/addfamily")
	@ResponseBody
	public ResponseEntity<AddFamilyResponse> addFamily (@RequestBody AddFamilyRequest addFamilyRequest) throws Exception {
		return WebRequestHandler.processRequest(AddFamilyOperation.class, addFamilyRequest);
	}
	
	@RequestMapping(value = "/addfamilymember")
	@ResponseBody
	public ResponseEntity<AddFamilyMemberResponse> addFamilyMember (@RequestBody AddFamilyMemberRequest addFamilyMemberRequest) throws Exception {
		return WebRequestHandler.processRequest(AddFamilyMemberOperation.class, addFamilyMemberRequest);
	}

	@RequestMapping(value = "/getmemberprofile")
	@ResponseBody
	public ResponseEntity<GetMemberProfileResponse> getMemberProfile (@RequestBody GetMemberProfileRequest getMemberProfileRequest) throws Exception {
		return WebRequestHandler.processRequest(GetMemberProfileOperation.class, getMemberProfileRequest);
	}

	@RequestMapping(value = "/getfamilydetails")
	@ResponseBody
	public ResponseEntity<GetFamilyDetailsResponse> getFamilyDetails (@RequestBody GetFamilyDetailsRequest getFamilyDetailsRequest) throws Exception {
		return WebRequestHandler.processRequest(GetFamilyDetailsOperation.class, getFamilyDetailsRequest);
	}

	@RequestMapping(value = "/searchfamily")
	@ResponseBody
	public ResponseEntity<SearchFamilyResponse> searchFamily (@RequestBody SearchFamilyRequest searchFamilyRequest) throws Exception {
		return WebRequestHandler.processRequest(SearchFamilyOperation.class, searchFamilyRequest);
	}

	@RequestMapping(value = "/getEvents")
	@ResponseBody
	public ResponseEntity<GetEventsResponse> getEvents (@RequestBody GetEventsRequest request) throws Exception {
		return WebRequestHandler.processRequest(GetEventsOperation.class, request);
	}

	@RequestMapping(value = "/getAccountList")
	@ResponseBody
	public ResponseEntity<GetAccountListResponse> getAccountList () throws Exception {
		return WebRequestHandler.processRequest(GetAccountListOperation.class, new EmptyOperationRequest());
	}

	@RequestMapping(value = "/getAccountStatement")
	@ResponseBody
	public ResponseEntity<GetAccountStatementResponse> getAccountStatement (@RequestBody GetAccountStatementRequest request) throws Exception {
		return WebRequestHandler.processRequest(GetAccountStatementOperation.class, request);
	}


}
