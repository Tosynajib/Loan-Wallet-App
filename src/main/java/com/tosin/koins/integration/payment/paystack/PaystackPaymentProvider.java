package com.tosin.koins.integration.payment.paystack;

import com.tosin.koins.common.exception.PaymentException;
import com.tosin.koins.integration.payment.PaymentProvider;
import com.tosin.koins.integration.payment.dto.PaymentInitializeRequest;
import com.tosin.koins.integration.payment.dto.PaymentInitializeResponse;
import com.tosin.koins.integration.payment.dto.PaymentVerificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

/**
 * Real Paystack implementation of PaymentProvider.
 *
 * WalletService should not know Paystack directly.
 * It only talks to PaymentProvider.
 */
@Slf4j
@Service
public class PaystackPaymentProvider implements PaymentProvider {

    private final RestClient restClient;
    private final PaystackProperties properties;

    public PaystackPaymentProvider(
            RestClient.Builder restClientBuilder,
            PaystackProperties properties
    ) {
        this.properties = properties;
        this.restClient = restClientBuilder
                .baseUrl(properties.baseUrl())
                .defaultHeader("Authorization", "Bearer " + properties.secretKey())
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public PaymentInitializeResponse initializePayment(PaymentInitializeRequest request) {
        try {
            PaystackInitializeRequest paystackRequest = new PaystackInitializeRequest(
                    request.email(),
                    toKobo(request.amount()),
                    request.reference(),
                    properties.callbackUrl()
            );

            PaystackInitializeResponse response = restClient.post()
                    .uri("/transaction/initialize")
                    .body(paystackRequest)
                    .retrieve()
                    .body(PaystackInitializeResponse.class);

            if (response == null || !Boolean.TRUE.equals(response.status()) || response.data() == null) {
                throw new PaymentException("Unable to initialize Paystack payment");
            }

            return new PaymentInitializeResponse(
                    response.data().reference(),
                    response.data().authorizationUrl(),
                    response.data().accessCode()
            );

        } catch (Exception ex) {
            log.error("Paystack payment initialization failed", ex);
            throw new PaymentException("Payment initialization failed");
        }
    }

    @Override
    public PaymentVerificationResponse verifyPayment(String reference) {
        try {
            PaystackVerifyResponse response = restClient.get()
                    .uri("/transaction/verify/{reference}", reference)
                    .retrieve()
                    .body(PaystackVerifyResponse.class);

            if (response == null || !Boolean.TRUE.equals(response.status()) || response.data() == null) {
                return new PaymentVerificationResponse(false, reference, null, BigDecimal.ZERO, "failed");
            }

            boolean successful = "success".equalsIgnoreCase(response.data().status());

            return new PaymentVerificationResponse(
                    successful,
                    response.data().reference(),
                    String.valueOf(response.data().providerTransactionId()),
                    fromKobo(response.data().amount()),
                    response.data().status()
            );

        } catch (Exception ex) {
            log.error("Paystack payment verification failed for reference {}", reference, ex);
            throw new PaymentException("Payment verification failed");
        }
    }

    @Override
    public boolean verifyWebhookSignature(String payload, String signature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");

            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    properties.secretKey().getBytes(StandardCharsets.UTF_8),
                    "HmacSHA512"
            );

            mac.init(secretKeySpec);

            String computedHash = HexFormat.of()
                    .formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));

            return computedHash.equals(signature);

        } catch (Exception ex) {
            log.error("Unable to verify Paystack webhook signature", ex);
            return false;
        }
    }

    private Long toKobo(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(100)).longValueExact();
    }

    private BigDecimal fromKobo(Long amountInKobo) {
        if (amountInKobo == null) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(amountInKobo)
                .divide(BigDecimal.valueOf(100));
    }
}