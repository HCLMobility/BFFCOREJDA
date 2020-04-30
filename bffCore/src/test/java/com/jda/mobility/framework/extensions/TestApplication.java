package com.jda.mobility.framework.extensions;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TestApplication {
    @Bean
    public ObjectMapper mapper() {
        return new ObjectMapper();
    }
}
