package com.neasaa.familytree;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.neasaa.base.app.operation.Operation;
import com.neasaa.base.app.operation.OperationContext;
import com.neasaa.base.app.operation.OperationExecutor;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.model.EmptyOperationResponse;
import com.neasaa.base.app.operation.model.OperationRequest;
import com.neasaa.base.app.operation.model.OperationResponse;
import com.neasaa.base.app.operation.session.model.UserSessionDetails;
import com.neasaa.base.app.utils.ValidationUtils;
import com.neasaa.familytree.utils.AppSessionWebWrapper;
import com.neasaa.familytree.utils.HttpSessionUtils;

import jakarta.servlet.http.HttpSession;

public class WebRequestHandler {
	
//	private static final Map<Integer, HttpStatus> APP_ERROR_TO_HTTP_ERROR_MAP = new HashMap<>();
//	
//	static {
//		APP_ERROR_TO_HTTP_ERROR_MAP.put(ErrorCodes.SUCCESS_CODE, HttpStatus.OK );
//		APP_ERROR_TO_HTTP_ERROR_MAP.put(ErrorCodes.USER_UNAUTHORIZED, HttpStatus.UNAUTHORIZED );
//		APP_ERROR_TO_HTTP_ERROR_MAP.put(ErrorCodes.VALIDATION_ERROR_CODE, HttpStatus.BAD_REQUEST );
//		APP_ERROR_TO_HTTP_ERROR_MAP.put(ErrorCodes.OPERATION_NOT_ALLOWED, HttpStatus.FORBIDDEN );
//	}
	
	public static <Request extends OperationRequest, Response extends OperationResponse, OP extends Operation<Request, Response>> ResponseEntity<Response> processRequest (Class<OP> operationClass, Request request) {
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession session = attr.getRequest().getSession();
		AppSessionWebWrapper appSessionWrapper = HttpSessionUtils.getAppSessionFromHttpSession(session);
		UserSessionDetails userSessionDetails = null;
		if(appSessionWrapper != null) {
			userSessionDetails = appSessionWrapper.getUserSessionDetails();
		}
		// Create instance of OperationContext
		ValidationUtils.addToDoLog("Implement AppHostname (on which host this application is running)", "WebRequestHandler");
		OperationContext operationContext = new OperationContext(userSessionDetails, "AppHostname");
		Response response = null;
		try {
			response = OperationExecutor.executeOperation(operationClass, request, operationContext);
			return new ResponseEntity<>(response, HttpStatus.OK);	
		} catch (OperationException ex) {
			return (ResponseEntity<Response>) buildResponse(ex, ex.getHttpResponseCode());
		} finally {
			operationContext.markComplete();
		}
		
//		int resCode = response.getResponseCode();
//		HttpStatus httpStatus = APP_ERROR_TO_HTTP_ERROR_MAP.get(resCode);
//		if(httpStatus == null) {
//			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
//		}
//		
//		return response;
	}
	
	public static ResponseEntity<? extends OperationResponse> buildResponse (OperationException ex, int responseCode) {
		return new ResponseEntity<OperationResponse>(new EmptyOperationResponse(ex.getMessage()), HttpStatus.valueOf(responseCode));
	}
//	public static ClientInformation getClientInformation (HttpServletRequest httpRequest){
//		return new ClientInformation(WebUtils.getClientIp(httpRequest), httpRequest.getHeader("User-Agent"));
//	}
}
