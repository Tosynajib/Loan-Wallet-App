package com.tosin.koins.transaction.repository;

import com.tosin.koins.transaction.entity.Transaction;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    Optional<Transaction> findByReference(String reference);

    boolean existsByReference(String reference);

    List<Transaction> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<Transaction> findByWalletIdOrderByCreatedAtDesc(UUID walletId);

    /**
     * Locks the transaction row during webhook processing.
     *
     * Why:
     * - Prevents two webhook calls from updating the same transaction at the same time.
     * - Helps avoid duplicate wallet crediting.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Transaction t where t.reference = :reference")
    Optional<Transaction> findByReferenceForUpdate(String reference);
}