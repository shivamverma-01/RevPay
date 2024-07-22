package com.revpay.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revpay.model.Account;

import java.math.BigInteger;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByBankAccountNumber(Long bankAccountNumber);
    Optional<Account> findByUserId(Long userId);
    
}
