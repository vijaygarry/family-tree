package com.neasaa.familytree.controller;

import com.neasaa.base.app.operation.session.RequestForgotPasswordOTPOperation;
import com.neasaa.base.app.operation.session.ResetForgotPasswordOperation;
import com.neasaa.base.app.operation.session.model.RequestForgotPasswordOTPRequest;
import com.neasaa.base.app.operation.session.model.RequestForgotPasswordOTPResponse;
import com.neasaa.base.app.operation.session.model.ResetForgotPasswordRequest;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.neasaa.base.app.enums.SessionExitCode;
import com.neasaa.base.app.operation.model.EmptyOperationRequest;
import com.neasaa.base.app.operation.model.EmptyOperationResponse;
import com.neasaa.base.app.operation.session.ChangePasswordOperation;
import com.neasaa.base.app.operation.session.GetSessionDetailOperation;
import com.neasaa.base.app.operation.session.LoginOperation;
import com.neasaa.base.app.operation.session.LogoutOperation;
import com.neasaa.base.app.operation.session.model.ChangePasswordRequest;
import com.neasaa.base.app.operation.session.model.GetSessionDetailResponse;
import com.neasaa.base.app.operation.session.model.LoginRequest;
import com.neasaa.base.app.operation.session.model.LoginResponse;
import com.neasaa.base.app.operation.session.model.LogoutRequest;
import com.neasaa.base.app.utils.ValidationUtils;
import com.neasaa.familytree.WebRequestHandler;
import com.neasaa.familytree.utils.AppSessionWebWrapper;
import com.neasaa.familytree.utils.HttpSessionUtils;
import com.neasaa.familytree.utils.WebUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Log4j2
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping(value = "/api/session", method = RequestMethod.POST)
public class SessionController implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins("http://localhost:3000") // React app
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
				.allowCredentials(true) // Must be true!
				.allowedHeaders("*");
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
	
	@RequestMapping(value = "/logout")
	@ResponseBody
	public ResponseEntity<EmptyOperationResponse> logout ( HttpServletRequest aRequest ) throws Exception {
		HttpSession httpSession = aRequest.getSession();
		return logoutUserFromDb(SessionExitCode.USER_LOGOUT, httpSession);
	}
	
	private static ResponseEntity<EmptyOperationResponse> logoutUserFromDb (SessionExitCode aSessionExitCode, HttpSession aHttpSession) {
		LogoutRequest input = new LogoutRequest();
		input.setSessionExitCode(aSessionExitCode);
		
		ResponseEntity<EmptyOperationResponse> operationResponse = WebRequestHandler.processRequest(LogoutOperation.class, input);
		
		AppSessionWebWrapper appSessionWrapper = HttpSessionUtils.getAppSessionFromHttpSession(aHttpSession);
		if(appSessionWrapper != null) {
			//Remove app session from wrapper, to avoid calling logout again.
			appSessionWrapper.removeAppSession();
			// Remove session attribute from httpSession
			HttpSessionUtils.unbindAppSessionToHttpSession(aHttpSession);
		}
		aHttpSession.invalidate();
		return operationResponse;
	}
	
	
	@RequestMapping(value = "/getsessiondetail", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<GetSessionDetailResponse> getSessionDetail ( ) throws Exception {
		return WebRequestHandler.processRequest(GetSessionDetailOperation.class, new EmptyOperationRequest() );
	}
//	
//	@RequestMapping(value = "/isvalid")
//	@ResponseBody
//	public ResponseEntity<OperationResponse<SessionValidResult>> isSessionValid ( ) throws Exception {
//		
//		ResponseEntity<OperationResponse<SessionValidResult>> operationResponse = OperationExecutor.executeOperation( ValidateSessionOperation.class, new EmptyDto() );
//		return operationResponse;
//	}
	
	
	@RequestMapping(value = "/changepassword")
	@ResponseBody
	public ResponseEntity<EmptyOperationResponse> changePwd ( @RequestBody ChangePasswordRequest changePasswordRequest) throws Exception {
		return WebRequestHandler.processRequest(ChangePasswordOperation.class, changePasswordRequest);
	}

	@RequestMapping(value = "/requestForgotPasswordOTP")
	@ResponseBody
	public ResponseEntity<RequestForgotPasswordOTPResponse> requestForgotPasswordOTP (@RequestBody RequestForgotPasswordOTPRequest request) throws Exception {
		return WebRequestHandler.processRequest(RequestForgotPasswordOTPOperation.class, request);
	}

	@RequestMapping(value = "/resetForgotPassword")
	@ResponseBody
	public ResponseEntity<EmptyOperationResponse> resetForgotPassword (@RequestBody ResetForgotPasswordRequest request) throws Exception {
		return WebRequestHandler.processRequest(ResetForgotPasswordOperation.class, request);
	}
	
}