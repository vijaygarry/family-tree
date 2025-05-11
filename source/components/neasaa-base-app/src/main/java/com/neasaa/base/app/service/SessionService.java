package com.neasaa.base.app.service;

import java.util.Date;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.neasaa.base.app.dao.pg.AbstractDao;
import com.neasaa.base.app.dao.pg.AppSessionDao;
import com.neasaa.base.app.entity.AppSession;
import com.neasaa.base.app.enums.ChannelEnum;
import com.neasaa.base.app.operation.exception.OperationException;

public class SessionService {
	
	private final AppSessionDao sessionDao;
//	private UserDAO userDAO;

	public SessionService (AppSessionDao sessionDao) {
		this.sessionDao = sessionDao;
	}
	
	public AppSession createSession(AuthenticatedUser authenticatedUser, boolean aAuthenticated, ChannelEnum aChannel,
			String aAppHostName, String aClientIpAddress, String aClientOsName, String aClientBrowserName)
			throws OperationException {
		Date currentTime = new Date();
		AppSession appSession = AppSession.builder()
				.userId(authenticatedUser.getUserId())
				.channelId(aChannel.name())
				.active(aRs.getString("ISACTIVE"))
				.sessionCreationTime(currentTime)
				.logoutTime(null)
				.lastAccessTime(currentTime)
				.exitCode(aRs.getShort("EXITCODE"))
				.appHostName(aRs.getString("APPHOSTNAME"))
				.clientIpAddress(aClientIpAddress)
				.userAgent(aRs.getString("USER_AGENT"))
				.build();
		
		return sessionDao.addAppSession(appSession);
		
		
	}
	
//	public AppSession createSession(AppUserDto aSessionUser, boolean aAuthenticated, ChannelEnum aChannel,
//			String aAppHostName, String aClientIpAddress, String aClientOsName, String aClientBrowserName)
//			throws OperationException {
//		AppSessionEntity sessionEntity = this.sessionDAO.createSession(aSessionUser, aAuthenticated, aChannel,
//				aAppHostName, aClientIpAddress, aClientOsName, aClientBrowserName);
//		this.userDAO.updateLastSuccessLoginTime(aSessionUser.getLogonName());
//
//		return EntityDtoMapper.getAppSessionDtoFromEntity(sessionEntity, aSessionUser);
//	}
//
//	@Override
//	public boolean logoutSession(AppSession aAppSession, SessionExitCode aSessionExitCode) {
//		return this.sessionDAO.logoutSession(aAppSession, aSessionExitCode);
//	}

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
