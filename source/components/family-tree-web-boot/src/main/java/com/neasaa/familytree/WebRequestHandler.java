package com.neasaa.familytree;

import com.neasaa.base.app.operation.Operation;
import com.neasaa.base.app.operation.OperationContext;
import com.neasaa.base.app.operation.OperationExecutor;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.model.EmptyOperationResponse;
import com.neasaa.base.app.operation.model.OperationRequest;
import com.neasaa.base.app.operation.model.OperationResponse;
import com.neasaa.base.app.service.AppSessionUser;
import com.neasaa.base.app.utils.ValidationUtils;
import com.neasaa.familytree.utils.AppSessionWebWrapper;
import com.neasaa.familytree.utils.HttpSessionUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class WebRequestHandler {

  public static <
          Request extends OperationRequest,
          Response extends OperationResponse,
          OP extends Operation<Request, Response>>
      ResponseEntity<Response> processRequest(Class<OP> operationClass, Request request) {
    ServletRequestAttributes attr =
        (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
    HttpSession session = attr.getRequest().getSession();
    AppSessionWebWrapper appSessionWrapper = HttpSessionUtils.getAppSessionFromHttpSession(session);
    AppSessionUser appSessionUser = null;
    if (appSessionWrapper != null) {
      appSessionUser = appSessionWrapper.getAppSessionUser();
    }
    // Create instance of OperationContext
    ValidationUtils.addToDoLog(
        "Implement AppHostname (on which host this application is running)", "WebRequestHandler");
    OperationContext operationContext = new OperationContext(appSessionUser, "AppHostname");
    Response response = null;
    try {
      response = OperationExecutor.executeOperation(operationClass, request, operationContext);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (OperationException ex) {
      return (ResponseEntity<Response>) buildResponse(ex, ex.getHttpResponseCode());
    } finally {
      operationContext.markComplete();
    }
  }

  public static ResponseEntity<? extends OperationResponse> buildResponse(
      OperationException ex, int responseCode) {
    return new ResponseEntity<OperationResponse>(
        new EmptyOperationResponse(ex.getMessage()), HttpStatus.valueOf(responseCode));
  }
}
