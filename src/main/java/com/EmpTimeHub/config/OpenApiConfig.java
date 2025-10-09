package com.EmpTimeHub.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenApiConfig
 * <p>
 * Configures the OpenAPI (Swagger) documentation for the DealKaro application.
 * Adds JWT Bearer authentication scheme for secured endpoints.
 */
@Configuration
public class OpenApiConfig {

    private static final Logger logger = LoggerFactory.getLogger(OpenApiConfig.class);

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    /**
     * customOpenAPI
     * <p>
     * Creates and configures the OpenAPI bean with API info and security scheme.
     *
     * @return OpenAPI object for Swagger UI
     */
    @Bean
    public OpenAPI customOpenAPI() {
        logger.info("customOpenAPI: Initializing OpenAPI configuration with JWT security scheme");
        // Build OpenAPI info and security configuration
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("EmpTimeHub API")
                        .version("v1")
                        .description("API documentation with security"))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
        logger.debug("customOpenAPI: OpenAPI bean created successfully");
        return openAPI;
    }
}
