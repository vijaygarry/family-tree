package com.neasaa.familytree;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitializeApplication implements BeanDefinitionRegistryPostProcessor {
	public static final String[] SCAN_PACKAGES = {
	        "com.neasaa.base.app.operation",
	        "com.neasaa.base.app.service",
	        "com.neasaa.base.app.dao.pg"
	    };
	@Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry);
        scanner.scan(SCAN_PACKAGES);
    }
	
}
