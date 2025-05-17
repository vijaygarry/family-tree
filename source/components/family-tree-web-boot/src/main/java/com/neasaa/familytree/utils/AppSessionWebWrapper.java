package com.neasaa.familytree.utils;

import com.neasaa.base.app.entity.AppSession;
import com.neasaa.base.app.enums.SessionExitCode;
import com.neasaa.base.app.operation.OperationExecutor;
import com.neasaa.base.app.operation.session.LogoutOperation;
import com.neasaa.base.app.operation.session.model.LogoutRequest;
import com.neasaa.base.app.operation.session.model.UserSessionDetails;
import com.neasaa.base.app.utils.ValidationUtils;

import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionBindingListener;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class AppSessionWebWrapper implements HttpSessionBindingListener {

	
	private UserSessionDetails userSessionDetails;
	private SessionExitCode exitCode = SessionExitCode.SESSION_TIMEOUT;
	
	/**
	 * @param aAppSUserSessionDetailsession
	 */
	public AppSessionWebWrapper ( UserSessionDetails userSessionDetails ) {
		super();
		this.userSessionDetails = userSessionDetails;
	}

	@Override
	/**
	 * Notifies the object that it is being bound to
     * a session and identifies the session.
     *
     * The default implementation takes no action.
     * 
	 */
	public void valueBound ( HttpSessionBindingEvent aEvent ) {
		
	}

	@Override
	/**
	 * Notifies the object that it is being unbound
     * from a session and identifies the session.
     *
     * The default implementation will logoff user
     * 
	 */
	public void valueUnbound ( HttpSessionBindingEvent aEvent ) {
		//This method will be called when user perform logout operation as well as when HTTP Timeout try to close the session.
		// During user logout, controller will set the appSession to null, so do not call logout when appSession object is null.
		if(this.userSessionDetails != null) {
			LogoutRequest request = new LogoutRequest();
			request.setSessionExitCode(this.exitCode);
			
			try {
				ValidationUtils.addToDoLog("Logout operation is not executed in AppSessionWebWrapper on unbound session", "AppSessionWebWrapper");
//				OperationExecutor.executeOperation( LogoutOperation.class, request );
			} catch (Exception ex) {
				log.info( "Failed to logout user while unbound from http session. Exit code " + this.exitCode, ex );
			}
			this.userSessionDetails.invalidate();
		}
	}

	public UserSessionDetails getUserSessionDetails() {
		return userSessionDetails;
	}
	
	/**
	 * Logout operation call this to make appSession null. This way, if Session Binder calls valueUnbound should not trigger logout again.
	 */
	public void removeAppSession () {
		this.userSessionDetails = null;
	}
	
//	/**
//	 * This exit code will be set in case of relogin.
//	 * 
//	 * @param aExitCode
//	 */
//	public void setExitCode ( SessionExitCode aExitCode ) {
//		this.exitCode = aExitCode;
//	}
}
