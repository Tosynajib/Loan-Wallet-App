package com.tosin.koins.webhook.service;

public interface PaymentWebhookService {

    void processPaystackWebhook(String payload, String signature);

}
