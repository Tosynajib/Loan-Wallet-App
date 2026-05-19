package com.tosin.koins.webhook.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tosin.koins.common.config.RabbitMqConfig;
import com.tosin.koins.common.enums.TransactionStatus;
import com.tosin.koins.common.exception.BadRequestException;
import com.tosin.koins.common.exception.NotFoundException;
import com.tosin.koins.common.exception.UnauthorizedException;
import com.tosin.koins.common.messaging.RabbitMqPublisher;
import com.tosin.koins.integration.payment.PaymentProvider;
import com.tosin.koins.integration.payment.dto.PaymentVerificationResponse;
import com.tosin.koins.integration.payment.event.PaymentConfirmationEvent;
import com.tosin.koins.integration.payment.paystack.PaystackWebhookEvent;
import com.tosin.koins.transaction.entity.Transaction;
import com.tosin.koins.transaction.repository.TransactionRepository;
import com.tosin.koins.wallet.entity.Wallet;
import com.tosin.koins.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentWebhookServiceImpl implements PaymentWebhookService {

    private final PaymentProvider paymentProvider;
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final ObjectMapper objectMapper;
    private final RabbitMqPublisher rabbitMqPublisher;

    @Override
    @Transactional
    public void processPaystackWebhook(String payload, String signature) {
        if (!paymentProvider.verifyWebhookSignature(payload, signature)) {
            throw new UnauthorizedException("Invalid Paystack webhook signature");
        }

        PaystackWebhookEvent event = parsePayload(payload);

        if (!"charge.success".equalsIgnoreCase(event.event())) {
            log.info("Ignoring unsupported Paystack webhook event: {}", event.event());
            return;
        }

        String reference = event.data().reference();

        Transaction transaction = transactionRepository.findByReferenceForUpdate(reference)
                .orElseThrow(() -> new NotFoundException("Transaction not found"));

        /**
         * Idempotency check:
         * If this transaction has already been processed, ignore the duplicate webhook.
         */
        if (transaction.getStatus() == TransactionStatus.SUCCESSFUL) {
            log.info("Duplicate Paystack webhook ignored for reference: {}", reference);
            return;
        }

        PaymentVerificationResponse verification = paymentProvider.verifyPayment(reference);

        if (!verification.successful()) {
            transaction.markFailed();
            transactionRepository.save(transaction);
            throw new BadRequestException("Payment verification failed");
        }

        /**
         * Extra safety:
         * The amount Paystack verified must match the amount we originally expected.
         */
        if (transaction.getAmount().compareTo(verification.amount()) != 0) {
            transaction.markFailed();
            transactionRepository.save(transaction);
            throw new BadRequestException("Payment amount mismatch");
        }

        Wallet wallet = walletRepository.findById(transaction.getWalletId())
                .orElseThrow(() -> new NotFoundException("Wallet not found"));

        wallet.credit(transaction.getAmount());

        transaction.setProviderReference(verification.providerReference());
        transaction.markSuccessful();

        walletRepository.save(wallet);
        transactionRepository.save(transaction);

        rabbitMqPublisher.publishPaymentConfirmation(
                new PaymentConfirmationEvent(
                        transaction.getUserId(),
                        wallet.getId(),
                        transaction.getId(),
                        transaction.getReference(),
                        transaction.getProviderReference(),
                        transaction.getAmount(),
                        wallet.getCurrency(),
                        LocalDateTime.now()
                )
        );

        log.info("Wallet funded successfully for reference: {}", reference);
    }

    private PaystackWebhookEvent parsePayload(String payload) {
        try {
            return objectMapper.readValue(payload, PaystackWebhookEvent.class);
        } catch (Exception ex) {
            throw new BadRequestException("Invalid Paystack webhook payload");
        }
    }
}