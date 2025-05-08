package com.neasaa.familytree.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/session")
public class SessionController {

	@RequestMapping(value = "/logout")
	@ResponseBody
	public String logout ( HttpServletRequest aRequest ) throws Exception {
//		HttpSession httpSession = aRequest.getSession();
		return "Logout Success";
	}
	
//	@RequestMapping(value = "/login")
//	@ResponseBody
//	public ResponseEntity<OperationResponse<SessionDetailsOutput>> login ( @RequestBody LoginInput aLoginDetails,
//			HttpServletRequest aRequest ) throws Exception {
//		
//		HttpSession httpSession = aRequest.getSession(false);
//		if(httpSession != null) {
//			AppSessionWebWrapper existingAppSession = HttpSessionUtils.getAppSessionFromHttpSession(httpSession);
//			if(existingAppSession != null ) {
//				logoutUserFromDb(SessionExitCode.RELOGIN, httpSession);
//			} else {
//				httpSession.invalidate();
//			}
//		}
//		httpSession = aRequest.getSession(true);
//		
//		aLoginDetails.setClientIpAddress( aRequest.getRemoteAddr() );
//		//TODO: Parse the userAgent from request and populate the below attributes
//		aLoginDetails.setClientOsName( null );
//		aLoginDetails.setClientBrowserName( null );
//		aLoginDetails.setChannel(ChannelEnum.BROWSER.name());
//		ResponseEntity<OperationResponse<SessionDetailsOutput>> operationResponse =
//				OperationExecutor.executeOperation( CreateSessionOperation.class, aLoginDetails );
//
//		if ( operationResponse != null && operationResponse.getStatusCode() == HttpStatus.OK ) {
//			
//			OperationResponse<SessionDetailsOutput> opResponse = operationResponse.getBody();
//			if ( opResponse != null ) {
//				SessionDetailsOutput output = opResponse.getOperationOutput();
//				if(output != null) {
//					HttpSessionUtils.bindAppSessionToHttpSession(output.getSessionDetails(), httpSession);
//				}
//			}
//		}
//		
//		return operationResponse;
//	}
	
//	@RequestMapping(value = "/logout")
//	@ResponseBody
//	public ResponseEntity<OperationResponse<EmptyDto>> logout ( HttpServletRequest aRequest ) throws Exception {
//		HttpSession httpSession = aRequest.getSession();
//		return logoutUserFromDb(SessionExitCode.USER_LOGOUT, httpSession);
//	}
	
//	private static ResponseEntity<OperationResponse<EmptyDto>> logoutUserFromDb (SessionExitCode aSessionExitCode, HttpSession aHttpSession) {
//		LogoutInput input = new LogoutInput();
//		input.setSessionExitCode(aSessionExitCode);
//		
//		ResponseEntity<OperationResponse<EmptyDto>> operationResponse = OperationExecutor
//				.executeOperation(LogoutSessionOperation.class, input);
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