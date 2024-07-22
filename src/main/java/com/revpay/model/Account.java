package com.revpay.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Getter
@Setter
public class Account {

 public enum ActivationStatus {
 ACTIVE, INACTIVE
 }

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long accountId;

 @Column(nullable = false)
 private Long userId;

 @Column(nullable = false, unique = true)
 private Long bankAccountNumber; // Changed to Long for bigint mapping

 @Column(nullable = false)
 private String ifscCode;

 @Column(nullable = false)
 @Enumerated(EnumType.STRING)
 private ActivationStatus activationStatus;

 @Column(nullable = false)
 private Long balance;

 @Column(nullable = false)
 @Temporal(TemporalType.TIMESTAMP) 
 private LocalDateTime created_at;

 @Column(nullable = false)
 @Temporal(TemporalType.TIMESTAMP) 
 private LocalDateTime lastModified_at;

 @Column(nullable = false)
 private Boolean creditAllowed = true;

 @Column(nullable = false)
 private Boolean debitAllowed = true;

 @Column(nullable = false)
 private Long dailyWithdrawalLimit =1000L; // default limit
}