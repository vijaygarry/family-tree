package com.neasaa.familytree.utils;


import com.neasaa.base.app.service.AppSessionUser;

import jakarta.servlet.http.HttpSession;

public class HttpSessionUtils {
	
	public static final String APP_SESSION_ATTRIBUTE_NAME = "APP_SESSION";
	
	
	public static void bindAppSessionToHttpSession (AppSessionUser appSessionUser, HttpSession aHttpSession) {
		if ( appSessionUser != null ) {
			aHttpSession.setAttribute( APP_SESSION_ATTRIBUTE_NAME, new AppSessionWebWrapper( appSessionUser) );
		}
	}
	
	public static void unbindAppSessionToHttpSession (HttpSession aHttpSession) {
		aHttpSession.removeAttribute( APP_SESSION_ATTRIBUTE_NAME );
	}
	
	public static AppSessionWebWrapper getAppSessionFromHttpSession(HttpSession aHttpSession) {
		return (AppSessionWebWrapper) aHttpSession.getAttribute(APP_SESSION_ATTRIBUTE_NAME);
	}
}
