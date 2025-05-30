package com.neasaa.familytree.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.neasaa.familytree.WebRequestHandler;
import com.neasaa.familytree.operation.family.AddFamilyOperation;
import com.neasaa.familytree.operation.family.model.AddFamilyRequest;
import com.neasaa.familytree.operation.family.model.AddFamilyResponse;

//import lombok.extern.log4j.Log4j2;

//@Log4j2
@RestController
@RequestMapping(value = "/family", method = RequestMethod.POST)
public class FamilyController {
	
	@RequestMapping(value = "/addfamily")
	@ResponseBody
	public ResponseEntity<AddFamilyResponse> changePwd ( @RequestBody AddFamilyRequest addFamilyRequest) throws Exception {
		return WebRequestHandler.processRequest(AddFamilyOperation.class, addFamilyRequest);
	}
	
}
