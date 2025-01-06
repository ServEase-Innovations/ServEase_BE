package com.springboot.app.webconfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // Allow all endpoints
            .allowedOrigins("*")  // Allow all origins
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("Origin", "Content-Type", "Accept", "Authorization", "X-Requested-With")
            .allowCredentials(true)  // Allow credentials
            .maxAge(3600);  // Cache preflight request for 1 hour
    }
}
