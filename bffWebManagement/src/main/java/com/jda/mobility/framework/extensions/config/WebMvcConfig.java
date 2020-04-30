package com.jda.mobility.framework.extensions.config;

import com.jda.mobility.framework.extensions.security.CorrelationInterceptor;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.RequestType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * The class WebMvcConfig.java
 * HCL Technologies Ltd.
 */
@Configuration
@EnableRetry
public class WebMvcConfig extends RequestContextListener implements WebMvcConfigurer {

    /** The field MAX_AGE_SECS of type long */
    private static final long MAX_AGE_SEC = 3600;
    private static final String X_AUTH_TOKEN = "X-Auth-Token";
    private static final String CORRELATION_ID_HEADER_NAME = "X-Correlation-Id";
    private static final String CROS_MAPPING_PATH = "/**";
    @Autowired
    private CorrelationInterceptor correlationInterceptor;
    /**
     *update corsRegistry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping(CROS_MAPPING_PATH)
        .allowedOrigins(BffAdminConstantsUtils.ASTERISK)
        .allowedMethods(RequestType.GET.getType(), RequestType.POST.getType(), RequestType.PUT.getType(), RequestType.PATCH.getType(), 
        		RequestType.DELETE.getType(), RequestType.OPTIONS.getType())
        .allowedHeaders(BffAdminConstantsUtils.ASTERISK)
        .exposedHeaders(X_AUTH_TOKEN, CORRELATION_ID_HEADER_NAME)
        .maxAge(MAX_AGE_SEC);
    }
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(correlationInterceptor);
    }

    @Bean
    public HttpTraceRepository httpTraceRepository() {
        return new InMemoryHttpTraceRepository();
    }
}
