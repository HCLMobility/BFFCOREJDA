package com.jda.mobility.framework.extensions;

import com.jda.mobility.framework.extensions.config.ProductApiSettings;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.retry.annotation.EnableRetry;

import com.jda.mobility.framework.extensions.config.AppProperties;



@SpringBootApplication
@EnableConfigurationProperties({AppProperties.class, ProductApiSettings.class})
@EnableRetry
public class BffWebManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(BffWebManagementApplication.class);
	}

}
