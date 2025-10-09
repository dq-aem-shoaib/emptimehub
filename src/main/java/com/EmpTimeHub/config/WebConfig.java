package com.EmpTimeHub.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Web configuration for CORS and static resources
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Value("${cors.allowed-methods}")
    private List<String> allowedMethods;

    @Value("${cors.allow-credentials}")
    private boolean allowCredentials;

    @Value("${upload.dir}")
    private String uploadLocation;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins.toArray(new String[0])) // Localhost + Lovable domain
                .allowedMethods(allowedMethods.toArray(new String[0])) // GET, POST, etc.
                .allowedHeaders("*") // Important: Set directly as "*"
                .allowCredentials(allowCredentials);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/uploads/images/**")
                .addResourceLocations("file:" + uploadLocation + "/");
    }
}
