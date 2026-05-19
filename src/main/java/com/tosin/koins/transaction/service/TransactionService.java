package com.tosin.koins.transaction.service;

import com.tosin.koins.transaction.dto.TransactionResponse;

import java.util.List;
import java.util.UUID;

public interface TransactionService {

    List<TransactionResponse> getMyTransactions();

    TransactionResponse getMyTransactionById(UUID transactionId);
}