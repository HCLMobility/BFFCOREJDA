/**
 * 
 */
package com.jda.mobility.framework.extensions.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * The class BffConfigUtils.java
 * HCL Technologies Ltd.
 */
@Configuration
public class BffConfigUtils {

	/**
	 * @return RestTemplate
	 */
	@Bean
	RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
	
	/**
	 * Bean to convert emptyString to null during DeSerialization.
	 * Any beans of type com.fasterxml.jackson.databind.Module are automatically registered 
	 * with the auto-configured Jackson2ObjectMapperBuilder and are applied to any ObjectMapper
	 * instances that it creates.
	 * @return SimpleModule
	 */
	@Bean
	  SimpleModule stringDeserializeModule() {
	    SimpleModule module = new SimpleModule();

	    module.addDeserializer(
	        String.class,
	        new StdDeserializer<String>(String.class) {
				private static final long serialVersionUID = -676860012538371695L;

			@Override
	          public String deserialize(JsonParser parser, DeserializationContext context)
	              throws IOException {
	            String result = StringDeserializer.instance.deserialize(parser, context);
	            if (StringUtils.isEmpty(result)) {
	              return null;
	            }
	            return result.trim();
	          }
	        });

	    return module;
	  }
}
