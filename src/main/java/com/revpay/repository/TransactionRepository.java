package com.revpay.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.revpay.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    @Query("SELECT COALESCE(SUM(t.transactionAmount), 0) FROM Transaction t WHERE t.accountId = :accountId AND t.transactionType = 'DEBIT' AND t.created_at BETWEEN :startOfDay AND :endOfDay")
    Long getTotalAmountByAccountAndDate(@Param("accountId") Long accountId, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    // New method to find transactions by account ID and type
    List<Transaction> findByAccountIdAndTransactionType(Long accountId, String transactionType);
}

