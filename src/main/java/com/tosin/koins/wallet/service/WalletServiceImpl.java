package com.tosin.koins.wallet.service;

import com.tosin.koins.common.enums.TransactionStatus;
import com.tosin.koins.common.enums.TransactionType;
import com.tosin.koins.common.exception.NotFoundException;
import com.tosin.koins.common.security.CurrentUserProvider;
import com.tosin.koins.common.util.ReferenceGenerator;
import com.tosin.koins.integration.payment.PaymentProvider;
import com.tosin.koins.integration.payment.dto.PaymentInitializeRequest;
import com.tosin.koins.integration.payment.dto.PaymentInitializeResponse;
import com.tosin.koins.transaction.dto.TransactionResponse;
import com.tosin.koins.transaction.entity.Transaction;
import com.tosin.koins.transaction.repository.TransactionRepository;
import com.tosin.koins.user.entity.User;
import com.tosin.koins.wallet.dto.FundWalletRequest;
import com.tosin.koins.wallet.dto.FundWalletResponse;
import com.tosin.koins.wallet.dto.WalletBalanceResponse;
import com.tosin.koins.wallet.dto.WalletResponse;
import com.tosin.koins.wallet.entity.Wallet;
import com.tosin.koins.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final CurrentUserProvider currentUserProvider;
    private final PaymentProvider paymentProvider;

    @Override
    public WalletResponse getMyWallet() {
        User currentUser = currentUserProvider.getCurrentUser();

        Wallet wallet = walletRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new NotFoundException("Wallet not found"));

        return WalletResponse.from(wallet);
    }

    @Override
    public WalletBalanceResponse getMyWalletBalance() {
        User currentUser = currentUserProvider.getCurrentUser();

        Wallet wallet = walletRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new NotFoundException("Wallet not found"));

        return new WalletBalanceResponse(
                wallet.getBalance(),
                wallet.getCurrency()
        );
    }

    @Override
    @Transactional
    public FundWalletResponse fundWallet(FundWalletRequest request) {
        User currentUser = currentUserProvider.getCurrentUser();

        Wallet wallet = walletRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new NotFoundException("Wallet not found"));

        String reference = ReferenceGenerator.generate("FUND");

        Transaction transaction = Transaction.builder()
                .userId(currentUser.getId())
                .walletId(wallet.getId())
                .type(TransactionType.CREDIT)
                .amount(request.amount())
                .status(TransactionStatus.PENDING)
                .reference(reference)
                .description("Wallet funding via Paystack")
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        PaymentInitializeResponse paymentResponse = paymentProvider.initializePayment(
                new PaymentInitializeRequest(
                        currentUser.getEmail(),
                        savedTransaction.getAmount(),
                        savedTransaction.getReference()
                )
        );

        return new FundWalletResponse(
                savedTransaction.getReference(),
                savedTransaction.getAmount(),
                wallet.getCurrency(),
                savedTransaction.getStatus().name(),
                paymentResponse.authorizationUrl(),
                "Payment initialized successfully. Complete payment using the authorization URL."
        );
    }

    @Override
    public List<TransactionResponse> getMyWalletTransactions() {
        User currentUser = currentUserProvider.getCurrentUser();

        Wallet wallet = walletRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new NotFoundException("Wallet not found"));

        return transactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId())
                .stream()
                .map(TransactionResponse::from)
                .toList();
    }
}