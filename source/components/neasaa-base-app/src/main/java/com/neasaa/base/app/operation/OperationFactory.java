package com.neasaa.base.app.operation;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.neasaa.base.app.operation.exception.InternalServerException;
import com.neasaa.base.app.operation.exception.OperationException;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class OperationFactory implements ApplicationContextAware {
	
	private static ApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext ( ApplicationContext aApplicationContext ) throws BeansException {
		applicationContext = aApplicationContext;
	}
	
	/**
	 * @param aClazz
	 *        - Interface class instance
	 * @return
	 * @throws OperationException
	 */
	public static <OP extends Operation<?, ?>> OP getOperationByType ( Class<OP> aClazz ) throws OperationException {
		try {
			String beanName = getBeanNameFromClass( aClazz );
			log.info("Looking for bean with name " + beanName);
			return applicationContext.getBean( beanName, aClazz );
		}
		catch ( Exception e ) {
			log.error( "Failed to get the operation for class <" + aClazz.getName() + ">", e );
			throw new InternalServerException("Internal error while executing your operation. Please contact administrator." );
		}
	}
	
	private static String getBeanNameFromClass (Class<?> aClazz) {
		String className = aClazz.getSimpleName();
		className = className.substring( 0, 1 ).toLowerCase() + className.substring( 1 );
		return className;
	}
}
