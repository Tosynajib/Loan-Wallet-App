package com.tosin.koins.integration.payment;

import com.tosin.koins.integration.payment.dto.PaymentInitializeRequest;
import com.tosin.koins.integration.payment.dto.PaymentInitializeResponse;
import com.tosin.koins.integration.payment.dto.PaymentVerificationResponse;

public interface PaymentProvider {

    PaymentInitializeResponse initializePayment(PaymentInitializeRequest request);

    PaymentVerificationResponse verifyPayment(String reference);

    boolean verifyWebhookSignature(String payload, String signature);
}
