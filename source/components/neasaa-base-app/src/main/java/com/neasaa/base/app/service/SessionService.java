package com.neasaa.base.app.service;

import java.sql.SQLException;
import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.neasaa.base.app.dao.pg.AppSessionDao;
import com.neasaa.base.app.entity.AppSession;
import com.neasaa.base.app.entity.AppUser;
import com.neasaa.base.app.enums.ChannelEnum;
import com.neasaa.base.app.enums.SessionExitCode;
import com.neasaa.base.app.operation.BeanNames;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.utils.ExceptionUtils;

@Service (BeanNames.SESSION_SERVICE_BEAN)
public class SessionService {
	
	private final AppSessionDao sessionDao;

	public SessionService (AppSessionDao sessionDao) {
		this.sessionDao = sessionDao;
	}
	
	public AppSession createSession(AppUser authenticatedUser, boolean aAuthenticated, boolean sessionActive, ChannelEnum aChannel,
			String aAppHostName, String aClientIpAddress, String aClientOsName, String aClientBrowserName)
			throws OperationException {
		
		Date currentTime = new Date();
		AppSession appSession = AppSession.builder()
				.userId(authenticatedUser.getUserId())
				.channel(aChannel)
				.active(sessionActive)
				.authenticated(aAuthenticated)
				.sessionCreationTime(currentTime)
				.logoutTime(null)
				.lastAccessTime(currentTime)
				.exitCode((short)0)
				.appHostName(aAppHostName)
				.clientIpAddress(aClientIpAddress)
				.userAgent("OS:" + aClientOsName + ",Browser:" + aClientBrowserName)
				.build();
		
		try {
			return sessionDao.addAppSession(appSession);
		} catch (SQLException e) {
			throw ExceptionUtils.getInternalException("Failed to create session", e);
		}
	}
	

	public boolean logoutSession(AppSessionUser appSessionUser, SessionExitCode aSessionExitCode) {
		return sessionDao.logoutSession(appSessionUser.getSessionId(), aSessionExitCode);
	}
	

//	@Override
//	public boolean isSessionValidAndAuthenticated(AppSession aAppSession) {
//		return this.sessionDAO.isSessionValidAndAuthenticated(aAppSession);
//	}

	/**
	 * Update last access time for this session in separate database transaction.
	 * This is to make sure, time is updated even if operation fails.
	 */
	@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void updateLastAccessTime(AppSession aAppSession) throws OperationException {
//		this.sessionDAO.updateLastAccessTime(aAppSession);
	}

//	public void setSessionDAO(SessionDAO aSessionDAO) {
//		this.sessionDAO = aSessionDAO;
//	}
//
//	public void setUserDAO(UserDAO aUserDAO) {
//		this.userDAO = aUserDAO;
//	}
}
