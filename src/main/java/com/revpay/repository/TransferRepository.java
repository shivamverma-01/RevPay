package com.revpay.repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.revpay.model.Transfer;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
//	 @Query("SELECT COALESCE(SUM(t.transactionAmount), 0) FROM Transaction t WHERE t.accountId = :accountId AND t.transactionType = 'DEBIT' AND t.created_at BETWEEN :startOfDay AND :endOfDay")
//	    Long getTotalAmountByAccountAndDate(@Param("accountId") Long accountId, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
//

}
