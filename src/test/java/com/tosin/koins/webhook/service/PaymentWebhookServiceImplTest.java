package com.tosin.koins.webhook.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tosin.koins.common.enums.TransactionStatus;
import com.tosin.koins.common.enums.TransactionType;
import com.tosin.koins.common.exception.UnauthorizedException;
import com.tosin.koins.common.messaging.RabbitMqPublisher;
import com.tosin.koins.integration.payment.PaymentProvider;
import com.tosin.koins.integration.payment.dto.PaymentVerificationResponse;
import com.tosin.koins.integration.payment.paystack.PaystackWebhookEvent;
import com.tosin.koins.transaction.entity.Transaction;
import com.tosin.koins.transaction.repository.TransactionRepository;
import com.tosin.koins.wallet.entity.Wallet;
import com.tosin.koins.wallet.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for Paystack webhook processing.
 */
@ExtendWith(MockitoExtension.class)
class PaymentWebhookServiceImplTest {

    @Mock
    private PaymentProvider paymentProvider;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RabbitMqPublisher rabbitMqPublisher;

    @InjectMocks
    private PaymentWebhookServiceImpl paymentWebhookService;

    @Test
    void processPaystackWebhook_shouldRejectInvalidSignature() {
        String payload = "{}";
        String signature = "invalid-signature";

        when(paymentProvider.verifyWebhookSignature(payload, signature)).thenReturn(false);

        assertThrows(
                UnauthorizedException.class,
                () -> paymentWebhookService.processPaystackWebhook(payload, signature)
        );

        verify(transactionRepository, never()).findByReferenceForUpdate(anyString());
        verify(walletRepository, never()).save(any());
        verify(rabbitMqPublisher, never()).publishPaymentConfirmation(any());
    }

    @Test
    void processPaystackWebhook_shouldIgnoreUnsupportedEvent() throws Exception {
        String payload = "{}";
        String signature = "valid-signature";

        PaystackWebhookEvent event = new PaystackWebhookEvent(
                "customeridentification.success",
                new PaystackWebhookEvent.Data("FUND-123", "success", 500000L)
        );

        when(paymentProvider.verifyWebhookSignature(payload, signature)).thenReturn(true);
        when(objectMapper.readValue(payload, PaystackWebhookEvent.class)).thenReturn(event);

        paymentWebhookService.processPaystackWebhook(payload, signature);

        verify(transactionRepository, never()).findByReferenceForUpdate(anyString());
        verify(walletRepository, never()).save(any());
        verify(rabbitMqPublisher, never()).publishPaymentConfirmation(any());
    }

    @Test
    void processPaystackWebhook_shouldIgnoreDuplicateSuccessfulWebhook() throws Exception {
        String payload = "{}";
        String signature = "valid-signature";
        String reference = "FUND-123";

        PaystackWebhookEvent event = new PaystackWebhookEvent(
                "charge.success",
                new PaystackWebhookEvent.Data(reference, "success", 500000L)
        );

        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .walletId(UUID.randomUUID())
                .type(TransactionType.CREDIT)
                .amount(BigDecimal.valueOf(5000))
                .status(TransactionStatus.SUCCESSFUL)
                .reference(reference)
                .build();

        when(paymentProvider.verifyWebhookSignature(payload, signature)).thenReturn(true);
        when(objectMapper.readValue(payload, PaystackWebhookEvent.class)).thenReturn(event);
        when(transactionRepository.findByReferenceForUpdate(reference)).thenReturn(Optional.of(transaction));

        paymentWebhookService.processPaystackWebhook(payload, signature);

        verify(paymentProvider, never()).verifyPayment(reference);
        verify(walletRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
        verify(rabbitMqPublisher, never()).publishPaymentConfirmation(any());
    }

    @Test
    void processPaystackWebhook_shouldCreditWalletAndMarkTransactionSuccessful() throws Exception {
        String payload = "{}";
        String signature = "valid-signature";
        String reference = "FUND-123";

        UUID userId = UUID.randomUUID();
        UUID walletId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();

        PaystackWebhookEvent event = new PaystackWebhookEvent(
                "charge.success",
                new PaystackWebhookEvent.Data(reference, "success", 500000L)
        );

        Transaction transaction = Transaction.builder()
                .id(transactionId)
                .userId(userId)
                .walletId(walletId)
                .type(TransactionType.CREDIT)
                .amount(BigDecimal.valueOf(5000))
                .status(TransactionStatus.PENDING)
                .reference(reference)
                .build();

        Wallet wallet = Wallet.builder()
                .id(walletId)
                .userId(userId)
                .balance(BigDecimal.ZERO)
                .currency("NGN")
                .build();

        PaymentVerificationResponse verification = new PaymentVerificationResponse(
                true,
                reference,
                "123456789",
                BigDecimal.valueOf(5000),
                "success"
        );

        when(paymentProvider.verifyWebhookSignature(payload, signature)).thenReturn(true);
        when(objectMapper.readValue(payload, PaystackWebhookEvent.class)).thenReturn(event);
        when(transactionRepository.findByReferenceForUpdate(reference)).thenReturn(Optional.of(transaction));
        when(paymentProvider.verifyPayment(reference)).thenReturn(verification);
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        paymentWebhookService.processPaystackWebhook(payload, signature);

        assertEquals(BigDecimal.valueOf(5000), wallet.getBalance());
        assertEquals(TransactionStatus.SUCCESSFUL, transaction.getStatus());
        assertEquals("123456789", transaction.getProviderReference());

        verify(walletRepository).save(wallet);
        verify(transactionRepository).save(transaction);
        verify(rabbitMqPublisher).publishPaymentConfirmation(any());
    }
}