package com.neasaa.base.app.service;

import java.sql.SQLException;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.neasaa.base.app.cache.CacheManager;
import com.neasaa.base.app.dao.pg.AppRoleDao;
import com.neasaa.base.app.dao.pg.OperationEntityDao;
import com.neasaa.base.app.operation.BeanNames;

@Configuration
public class CacheLoader implements InitializingBean {
	
	@Bean(name = BeanNames.CACHE_MANAGER_BEAN)
    public CacheManager cacheManager(
    		OperationEntityDao operationEntityDao,
    		AppRoleDao appRoleDao
            ) {

        CacheManager cacheManager = CacheManager.getCacheManager(); // Static factory method
        cacheManager.setOperationEntityDao(operationEntityDao);
        cacheManager.setAppRoleDao(appRoleDao);
        try {
			cacheManager.loadCache();
		} catch (SQLException e) {
			throw new RuntimeException ("Failed to load cache.");
		}
        return cacheManager;
    }
	
	@Override
    public void afterPropertiesSet() {
		
//		    String[] beanNames = applicationContext.getBeanDefinitionNames();
//		    Set<String> packages = new HashSet<>();
//		    for (String name : beanNames) {
//		        Object bean = applicationContext.getBean(name);
//		        packages.add(bean.getClass().getPackage().getName());
//		    }
//		    packages.forEach(System.out::println);
		
//		CacheManager cacheManager = CacheManager.getCacheManager();
//		try {
//			cacheManager.loadCache();
//		} catch (SQLException e) {
//			throw new RuntimeException ("Failed to load cache.");
//		}
	}
}
