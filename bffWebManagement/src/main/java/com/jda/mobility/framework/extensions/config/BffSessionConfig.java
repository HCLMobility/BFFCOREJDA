/**
 * 
 */
package com.jda.mobility.framework.extensions.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

/**
 * The class BffSessionConfig.java
 * HCL Technologies Ltd.
 */
@Configuration
//@EnableJdbcHttpSession
public class BffSessionConfig extends AbstractHttpSessionApplicationInitializer{
	/**
	 * customize Spring Session’s HttpSession integration to use HTTP headers 
	 * to convey the current session information instead of cookies
	 * @return HttpSessionIdResolver
	 */
	@Bean
	public HttpSessionIdResolver httpSessionIdResolver() {
		return HeaderHttpSessionIdResolver.xAuthToken(); 
	}
	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
	    return new HttpSessionEventPublisher();
	}
}
