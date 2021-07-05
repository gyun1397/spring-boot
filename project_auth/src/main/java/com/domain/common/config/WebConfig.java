package com.domain.common.config;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfig implements WebMvcConfigurer {
    private static final String[] allowedMethods = { "OPTIONS", "GET", "HEAD", "POST", "PUT", "DELETE", "PATCH" };
    private static final String[] exposedHeaders = { "Origin", "Authorization"};

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods(allowedMethods)
                .exposedHeaders(exposedHeaders)
                .allowCredentials(false)
                .maxAge(86400);
    }
}
