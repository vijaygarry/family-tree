package com.neasaa.base.app.cache;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.neasaa.base.app.dao.pg.AppRoleDao;
import com.neasaa.base.app.dao.pg.OperationEntityDao;
import com.neasaa.base.app.entity.AppRole;
import com.neasaa.base.app.entity.OperationEntity;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class CacheManager {
	
	private static CacheManager instance;
	
	@Setter
	private OperationEntityDao operationEntityDao;
	
	@Setter
	private AppRoleDao appRoleDao;
	
	private Map<String, OperationEntity> operationNameToOperationMap = new HashMap<>();
	private Map<String, AppRole> roleIdToRoleMap = new HashMap<>();
	
	private CacheManager () {
		
	}
	
	public static CacheManager getCacheManager () {
		if(instance == null) {
			synchronized (CacheManager.class) {
				if(instance == null) {
					instance = new CacheManager();
				}
			}
		}
		return instance;
	}
	
	public OperationEntity getOperationByName(String aOperationName) {
		return this.operationNameToOperationMap.get(aOperationName);
	}
	
	public AppRole getAppRoleById(String aRoleId) {
		return this.roleIdToRoleMap.get(aRoleId);
	}
	
	public void loadCache () throws SQLException {
		log.info("Loading cache");
//		List<ConfigEntry> allConfigs = this.configurationDao.getAllActiveConfigurations();
//		Map<String, Map<String, String>> configMap = new HashMap<>();
//		Map<String, String> singleConfigs = new HashMap<>();
//		for(ConfigEntry config: allConfigs) {
//			Map<String, String> configValueMap = configMap.get(config.getConfigGroup().toUpperCase());
//			if(configValueMap == null) {
//				configValueMap = new HashMap<>();
//				configMap.put(config.getConfigGroup().toUpperCase(), configValueMap);
//			}
//			configValueMap.put(config.getConfigName(), config.getConfigValue());
//			singleConfigs.put(config.getConfigGroup().toUpperCase() + "-" + config.getConfigName().toUpperCase(), config.getConfigValue());
//		}
		Map<String, OperationEntity> operationMap = getOperationMap();
		Map<String, AppRole> roleMap = getRoleMap();
//		
		synchronized (this) {
//			this.configurationMap = singleConfigs;
//			this.groupConfigMap = configMap;
			this.operationNameToOperationMap = operationMap;
			this.roleIdToRoleMap = roleMap;
		}
	}
	
	private Map<String, OperationEntity> getOperationMap() throws SQLException {
		List<OperationEntity> activeOperations = this.operationEntityDao.fetchAllActiveOperations();
		Map<String, OperationEntity> operationMap = new HashMap<>();
		if(activeOperations != null) {
			for(OperationEntity operation : activeOperations) {
				operationMap.put( operation.getOperationId(), operation );
			}
		}
		return operationMap;
	}
	
	private Map<String, AppRole> getRoleMap() {
		List<AppRole> appRoles = appRoleDao.getAllActiveRoles();
		Map<String, AppRole> roleMap = new HashMap<>();
		if(appRoles != null) {
			for(AppRole role : appRoles) {
				roleMap.put( role.getRoleId(), role );
			}
		}
		
		return roleMap;
	}
}
