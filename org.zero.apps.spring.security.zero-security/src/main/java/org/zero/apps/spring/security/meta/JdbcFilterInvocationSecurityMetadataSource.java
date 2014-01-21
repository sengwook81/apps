package org.zero.apps.spring.security.meta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.AntPathRequestMatcher;
import org.springframework.security.web.util.RequestMatcher;

public class JdbcFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

	protected final Log logger = LogFactory.getLog(getClass());

	private final Map<RequestMatcher, Collection<ConfigAttribute>> requestMap = new LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>>();

	private final Collection<ConfigAttribute> defRoles = new ArrayList<ConfigAttribute>();
	
	
	public JdbcFilterInvocationSecurityMetadataSource() {
		Collection<ConfigAttribute> atts = new ArrayList<ConfigAttribute>();
		ConfigAttribute ca = new SecurityConfig("ROLE_ADMIN");
		atts.add(ca);
		ConfigAttribute ca2 = new SecurityConfig("ROLE_USER");
		atts.add(ca2);
		
		requestMap.put(new AntPathRequestMatcher("/app/*"),atts);
		
		defRoles.add(ca);
		defRoles.add(ca2);
	}

	public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
		FilterInvocation fi = (FilterInvocation) object;
		logger.debug("REQUEST URL : " + fi.getRequestUrl());
		if(fi.getRequestUrl().startsWith("/log")) { 
			return null;
		}
		
		for (Entry<RequestMatcher, Collection<ConfigAttribute>> en : requestMap.entrySet()) {
			if (en.getKey().matches(fi.getRequest())) {
				logger.debug("Match Pattern : " + en.getKey().toString());
				return en.getValue();
			}
		}
		
		return defRoles;
	}

	public Collection<ConfigAttribute> getAllConfigAttributes() {
		logger.debug("getAllConfigAttributes");
		return null;
	}

	public boolean supports(Class<?> clazz) {
		logger.debug("Supports" + clazz);
		return FilterInvocation.class.isAssignableFrom(clazz);
	}

}
