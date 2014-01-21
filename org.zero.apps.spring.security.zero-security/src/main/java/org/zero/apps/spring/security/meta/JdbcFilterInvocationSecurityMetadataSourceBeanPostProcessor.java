package org.zero.apps.spring.security.meta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;

public class JdbcFilterInvocationSecurityMetadataSourceBeanPostProcessor implements BeanPostProcessor {

	protected static final Logger log = LoggerFactory.getLogger(JdbcFilterInvocationSecurityMetadataSourceBeanPostProcessor.class);
	
	private FilterInvocationSecurityMetadataSource metadataSource = new JdbcFilterInvocationSecurityMetadataSource();

	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof FilterInvocationSecurityMetadataSource) {
			log.debug("JdbcFilterInvocationSecurityMetadataSourceBeanPostProcessor Initialize");
			return metadataSource;
		}
		
		return bean;
	}

	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
