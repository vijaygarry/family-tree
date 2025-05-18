package com.neasaa.familytree.controller;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.neasaa.base.app.operation.session.LoginOperation;
import com.neasaa.base.app.operation.session.model.LoginRequest;
import com.neasaa.base.app.operation.session.model.LoginResponse;
import com.neasaa.base.app.utils.ValidationUtils;
import com.neasaa.familytree.WebRequestHandler;
import com.neasaa.familytree.utils.AppSessionWebWrapper;
import com.neasaa.familytree.utils.HttpSessionUtils;
import com.neasaa.familytree.utils.WebUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping(value = "/session")
public class SessionController {

	@RequestMapping(value = "/logout")
	@ResponseBody
	public String logout ( HttpServletRequest aRequest ) throws Exception {
		return "Logout Success";
	}
	
	@RequestMapping(value = "/login")
	@ResponseBody
	public ResponseEntity<LoginResponse> login ( @RequestBody LoginRequest loginRequest,
			HttpServletRequest aRequest ) throws Exception {
		
		log.info("Processing login request for " + loginRequest.getLoginName());
		HttpSession httpSession = aRequest.getSession(false);
		if(httpSession != null) {
			AppSessionWebWrapper existingAppSession = HttpSessionUtils.getAppSessionFromHttpSession(httpSession);
			if(existingAppSession != null ) {
				ValidationUtils.addToDoLog("Add logic to logout from database when user try to login with active session", "SessionController");
				//logoutUserFromDb(SessionExitCode.RELOGIN, httpSession);
			} else {
				httpSession.invalidate();
			}
		}
		httpSession = aRequest.getSession(true);
		
		loginRequest.setClientInformation(WebUtils.getClientInformation(aRequest));
		ResponseEntity<LoginResponse> operationResponse =
				WebRequestHandler.processRequest(LoginOperation.class, loginRequest );

		if ( operationResponse != null && operationResponse.getStatusCode() == HttpStatus.OK ) {
			
			LoginResponse opResponse = operationResponse.getBody();
			if ( opResponse != null ) {
				HttpSessionUtils.bindAppSessionToHttpSession(opResponse.getAppSessionUser(), httpSession);
				opResponse.setAppSessionUser(null);
			}
		}
		
		return operationResponse;
	}
	
//	@RequestMapping(value = "/logout")
//	@ResponseBody
//	public ResponseEntity<OperationResponse<EmptyDto>> logout ( HttpServletRequest aRequest ) throws Exception {
//		HttpSession httpSession = aRequest.getSession();
//		return logoutUserFromDb(SessionExitCode.USER_LOGOUT, httpSession);
//	}
	
//	private static ResponseEntity<OperationResponse<EmptyDto>> logoutUserFromDb (SessionExitCode aSessionExitCode, HttpSession aHttpSession) {
//		LogoutRequest input = new LogoutRequest();
//		input.setSessionExitCode(aSessionExitCode);
//		
//		ResponseEntity<OperationResponse<EmptyDto>> operationResponse = OperationExecutor
//				.executeOperation(LogoutOperation.class, input);
//		
//		AppSessionWebWrapper appSessionWrapper = HttpSessionUtils.getAppSessionFromHttpSession(aHttpSession);
//		if(appSessionWrapper != null) {
//			//Remove app session from wrapper, to avoid calling logout again.
//			appSessionWrapper.removeAppSession();
//			// Remove session attribute from httpSession
//			HttpSessionUtils.unbindAppSessionToHttpSession(aHttpSession);
//		}
//		aHttpSession.invalidate();
//		return operationResponse;
//	}
//	
//	@RequestMapping(value = "/getsessiondetails")
//	@ResponseBody
//	public ResponseEntity<OperationResponse<SessionDetailsOutput>> getSessionDetails ( ) throws Exception {
//		ResponseEntity<OperationResponse<SessionDetailsOutput>> operationResponse = OperationExecutor.executeOperation( SessionDetailOperation.class, new EmptyDto() );
//		return operationResponse;
//	}
//	
//	@RequestMapping(value = "/isvalid")
//	@ResponseBody
//	public ResponseEntity<OperationResponse<SessionValidResult>> isSessionValid ( ) throws Exception {
//		
//		ResponseEntity<OperationResponse<SessionValidResult>> operationResponse = OperationExecutor.executeOperation( ValidateSessionOperation.class, new EmptyDto() );
//		return operationResponse;
//	}
	
	
//	@RequestMapping(value = "/changepwd")
//	@ResponseBody
//	public ResponseEntity<OperationResponse<EmptyDto>> changePwd ( @RequestBody ChangePasswordInput aChangePasswordInput) throws Exception {
//		
//		
//		ResponseEntity<OperationResponse<EmptyDto>> operationResponse = WebOperationHelper
//				.executeOperation(ChangePasswordOperation.class, aChangePasswordInput);
//		return operationResponse;
//		
//	}
	
	
}