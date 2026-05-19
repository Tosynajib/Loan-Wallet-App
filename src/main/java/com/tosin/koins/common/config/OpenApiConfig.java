package com.tosin.koins.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI configuration.
 *
 * Adds API metadata and JWT Bearer authentication support to Swagger UI.
 */
@Configuration
public class OpenApiConfig {

    private static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI koinsOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Fintech Loan Wallet API")
                        .description("""
                                Spring Boot fintech backend for wallet funding, Paystack integration,
                                loan application, repayment tracking, RabbitMQ notifications, and admin loan management.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Tosin Ajibade")
                                .email("email@gmail.com"))
                        .license(new License()
                                .name("Assessment Project")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
                .components(new Components()
                        .addSecuritySchemes(
                                BEARER_AUTH,
                                new SecurityScheme()
                                        .name(BEARER_AUTH)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        ));
    }
}