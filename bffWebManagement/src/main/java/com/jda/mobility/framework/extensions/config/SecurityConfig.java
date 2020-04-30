package com.jda.mobility.framework.extensions.config;

import java.util.Arrays;

import com.jda.mobility.framework.extensions.security.ActuatorApiKeyFilter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

import com.jda.iam.client.TokenValidator;
import com.jda.iam.config.ClientConfiguration;
import com.jda.iam.config.IAMClientConfiguration;
import com.jda.iam.core.IAMException;
import com.jda.mobility.framework.extensions.security.BffLogoutSuccessHandler;
import com.jda.mobility.framework.extensions.security.CustomAuthenticationProvider;
import com.jda.mobility.framework.extensions.security.PermissionAuthorizationFilter;
import com.jda.mobility.framework.extensions.security.RestAuthenticationEntryPoint;
import com.jda.mobility.framework.extensions.security.SecurityAuditorAware;
import com.jda.mobility.framework.extensions.security.TokenAuthenticationFilter;
import com.jda.mobility.framework.extensions.util.ConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;

/**
 * The class provides configuration for application security.
 */
@SuppressWarnings({ConstantsUtils.UNCHECKED,ConstantsUtils.RAWTYPE})
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
@EnableAutoConfiguration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	private static final Logger LOGGER = LogManager.getLogger(SecurityConfig.class);
	
	@Value("${app.openid.providerlocation}")
    private String providerLocation ;
	
	@Value("${app.openid.clientId}")
    private String clientId;
 
    @Value("${app.openid.clientSecret}")
    private String clientSecret;
   
    @Value("${app.openid.redirectUri}")
    private String redirectUri;
    
    @Value("${app.openid.authenticationMethod}")
    private String authMethod;
 
    @Value("${app.openid.audience}")
    private String audience;    
    
    @Value("${app.openid.scope}")
    private String scope;

    @Value("${actuator.api.key}")
    private String actuatorApiKey;

    @Autowired
	private AppProperties appProperties;
 
	 /** The field authProvider of type CustomAuthenticationProvider */
	@Autowired
	private CustomAuthenticationProvider authProvider;
    
    /**
     * @param authenticationManagerBuilder
     */
    @Override
	public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.authenticationProvider(authProvider);
	}

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
    	return new ProviderManager(Arrays.asList(authProvider));
    }
    @Bean
	public ClientConfiguration iamClientConfig() {
		return new IAMClientConfiguration.Builder()
				.providerLocation(providerLocation)
				.clientId(clientId)
				.clientSecret(clientSecret)
				.redirectUri(redirectUri)
				.clientAuthentication(authMethod)
				.audience(audience)
				.scopes(scope)
				.build();

	}

	/**
	 * @return TokenAuthenticationFilter
	 */
	@Bean
	public TokenAuthenticationFilter tokenAuthenticationFilter() {
		TokenAuthenticationFilter tokenAuthFilter = null;
		try {
			if (appProperties.isOidcEnabled()) {
				tokenAuthFilter = new TokenAuthenticationFilter(new TokenValidator(providerLocation), iamClientConfig());
				LOGGER.log(Level.DEBUG, "Provider configuration loaded successfully for: {}", providerLocation);
			}
			else {
				tokenAuthFilter = new TokenAuthenticationFilter();
				LOGGER.log(Level.DEBUG, "Basic Authentication scheme in effect. Hence, ignored IAM provider: {}", providerLocation);
			}
		} catch (IAMException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, providerLocation, exp);
			tokenAuthFilter = new TokenAuthenticationFilter();
		}
		return tokenAuthFilter;
	}

    @Bean
    public ActuatorApiKeyFilter actuatorApiKeyFilter() {
        return new ActuatorApiKeyFilter(actuatorApiKey);
    }
    
	@Autowired
	private FindByIndexNameSessionRepository sessionRepository;

	@Bean
	public SpringSessionBackedSessionRegistry sessionRegistry(
			FindByIndexNameSessionRepository sessionRepository) {
		return new SpringSessionBackedSessionRegistry(sessionRepository);
	}
	@Bean
	public LogoutSuccessHandler logoutSuccessHandler() {
	    return new BffLogoutSuccessHandler();
	}
    /**
     * Configure HttpSecurity
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                    .and()
                .sessionManagement()
                	.maximumSessions(-1)
                	.sessionRegistry(sessionRegistry(this.sessionRepository))
                	.and()
                	.sessionFixation()
                		.newSession()
                		.sessionCreationPolicy(SessionCreationPolicy.NEVER)
                    .and()
                .csrf()
                    .disable()
                .formLogin()
                    .disable()
                .httpBasic()
                    .disable()
                .logout()
                	.logoutUrl("/api/auth/v1/logout")
                	.logoutSuccessHandler(logoutSuccessHandler())
                	.clearAuthentication(true)
                	.invalidateHttpSession(true)
                	.deleteCookies("JSESSIONID")
                	.and()
                .exceptionHandling()
                    .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                    .and()
                .authorizeRequests()
                    .antMatchers(
                            "/api/auth/v1/user",
                            "/api/form/v1/log/",
                            // Actuator endpoints will be secured by ActuatorApiKeyFilter
                            "/actuator/**"
                        )
                        .permitAll()
                    .anyRequest()
                        .authenticated();

        http.addFilterBefore(tokenAuthenticationFilter(), LogoutFilter.class)
                .addFilterBefore(actuatorApiKeyFilter(), TokenAuthenticationFilter.class);
    }
    @Bean
    public AuditorAware<String> auditorAware(){
        return new SecurityAuditorAware();
    }

	@Bean
	public FilterRegistrationBean<PermissionAuthorizationFilter> registrationBean(
			PermissionAuthorizationFilter permissionAuthorizationFilter) {
		FilterRegistrationBean<PermissionAuthorizationFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(permissionAuthorizationFilter);
		registrationBean.addUrlPatterns("/api/user/v1/layers", "/api/config/v1/list/APPLICATION", "/api/user/v1/layers/map");
		return registrationBean;
	}
}