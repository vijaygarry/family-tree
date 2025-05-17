package com.neasaa.familytree.utils;


import com.neasaa.base.app.operation.session.model.UserSessionDetails;

import jakarta.servlet.http.HttpSession;

public class HttpSessionUtils {
	
	public static final String APP_SESSION_ATTRIBUTE_NAME = "APP_SESSION";
	
	
	public static void bindAppSessionToHttpSession (UserSessionDetails userSessionDetails, HttpSession aHttpSession) {
		if ( userSessionDetails != null ) {
			aHttpSession.setAttribute( APP_SESSION_ATTRIBUTE_NAME, new AppSessionWebWrapper( userSessionDetails) );
		}
	}
	
	public static void unbindAppSessionToHttpSession (HttpSession aHttpSession) {
		aHttpSession.removeAttribute( APP_SESSION_ATTRIBUTE_NAME );
	}
	
	public static AppSessionWebWrapper getAppSessionFromHttpSession(HttpSession aHttpSession) {
		return (AppSessionWebWrapper) aHttpSession.getAttribute(APP_SESSION_ATTRIBUTE_NAME);
	}
}
