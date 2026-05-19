package com.tosin.koins.transaction.service;

import com.tosin.koins.common.exception.NotFoundException;
import com.tosin.koins.common.security.CurrentUserProvider;
import com.tosin.koins.transaction.dto.TransactionResponse;
import com.tosin.koins.transaction.entity.Transaction;
import com.tosin.koins.transaction.repository.TransactionRepository;
import com.tosin.koins.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Transaction business logic implementation.
 *
 * Important security rule:
 * A normal user must only access their own transaction records.
 */
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public List<TransactionResponse> getMyTransactions() {
        User currentUser = currentUserProvider.getCurrentUser();

        return transactionRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(TransactionResponse::from)
                .toList();
    }

    @Override
    public TransactionResponse getMyTransactionById(UUID transactionId) {
        User currentUser = currentUserProvider.getCurrentUser();

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NotFoundException("Transaction not found"));
        if (!transaction.getUserId().equals(currentUser.getId())) {
            throw new NotFoundException("Transaction not found");
        }

        return TransactionResponse.from(transaction);
    }
}