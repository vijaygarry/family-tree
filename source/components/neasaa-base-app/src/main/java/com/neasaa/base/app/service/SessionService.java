package com.neasaa.base.app.service;

import java.sql.SQLException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.neasaa.base.app.dao.pg.AppSessionDao;
import com.neasaa.base.app.dao.pg.AppSessionTxnDao;
import com.neasaa.base.app.entity.AppSession;
import com.neasaa.base.app.entity.AppSessionTxn;
import com.neasaa.base.app.entity.AppUser;
import com.neasaa.base.app.enums.ChannelEnum;
import com.neasaa.base.app.enums.SessionExitCode;
import com.neasaa.base.app.operation.BeanNames;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.UnauthorizedException;
import com.neasaa.base.app.utils.ExceptionUtils;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service (BeanNames.SESSION_SERVICE_BEAN)
public class SessionService {
	
	private static long SESSION_TIMEOUT_MILLIS = 30 * 60 * 1000;
	
	@Autowired
	private AppSessionDao sessionDao;
	
	@Autowired
	private AppSessionTxnDao appSessionTxnDao;

	
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
	

	public void checkSessionValidity(AppSessionUser appSessionUser) {
		if(appSessionUser == null) {
			throw new UnauthorizedException("Please login to perform this operation.");
		}
		if(!appSessionUser.isAuthenticated()) {
			throw new UnauthorizedException("Invalid session, please login to perform this operation.");
		}
		if(appSessionUser.getSessionId() <= 0) {
			throw new UnauthorizedException("Invalid session, please login to perform this operation.");
		}
		AppSession appSession = sessionDao.getSessionById(appSessionUser.getSessionId());
		if(appSession == null) {
			throw new UnauthorizedException("Session expired, please login to perform this operation.");
		}
		
		long currentTime = System.currentTimeMillis();
		//If session is not used from last 30 mins, then session is not valid
		if(appSession.getLastAccessTime().getTime() + SESSION_TIMEOUT_MILLIS < currentTime) {
			log.info("Session expired. Session last access time: {}, Current time: {}", appSession.getLastAccessTime(), currentTime);
			throw new UnauthorizedException("Session expired, please login to perform this operation.");
		}
	}

	/**
	 * Update last access time for this session in separate database transaction.
	 * This is to make sure, time is updated even if operation fails.
	 */
	@Transactional(transactionManager = BeanNames.TRANSACTION_MANAGER, propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void updateLastAccessTime(AppSessionUser appSessionUser) throws OperationException {
		try {
			sessionDao.updateLastAccessTime(appSessionUser.getSessionId());
		} catch (SQLException e) {
			throw ExceptionUtils.getInternalException("Failed to update last access time for session", e);
		}
	}

	@Transactional(transactionManager = BeanNames.TRANSACTION_MANAGER, propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void auditTransaction(long sessionId, String operationId, int userId, Date txnStartTime, long txnLatency,
			int httpResponseCode, String request, String response) {
		try {
			appSessionTxnDao.auditTransaction(sessionId, operationId, userId, txnStartTime, txnLatency,
					httpResponseCode, request, response);
		} catch (Throwable th) {
			log.error("Internal unhandle exception during auditing", th);
		}
	}
}
