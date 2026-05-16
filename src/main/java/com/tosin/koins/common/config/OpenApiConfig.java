package com.tosin.koins.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Swagger/OpenAPI configuration.
 *
 * Why we need this:
 * - It documents all APIs.
 * - It allows reviewers to test endpoints from the browser.
 * - It explains JWT authentication to Swagger UI.
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI koinsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("KOINS Loan and Wallet Management API")
                        .description("A fintech backend API for user onboarding, wallet funding, loan processing, repayments, and transaction tracking.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Oluwatosin Ajibade")
                                .email("tosynajib@gmail.com")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(
                                SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        ));
    }
}