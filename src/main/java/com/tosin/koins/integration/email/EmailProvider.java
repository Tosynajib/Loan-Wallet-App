package com.tosin.koins.integration.email;

public interface EmailProvider {

    void sendEmail(String recipient, String subject, String message);
}