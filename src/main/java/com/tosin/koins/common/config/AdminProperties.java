package com.tosin.koins.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.admin")
public record AdminProperties(
        String email,
        String password,
        String fullName,
        String phoneNumber,
        String bvnOrNin
) {
}