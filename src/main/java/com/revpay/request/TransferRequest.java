package com.revpay.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

@Getter
@Setter
public class TransferRequest {

    @NotNull(message = "Sender Account ID cannot be null")
    private Long accountId; // Updated to match with your service method

    @NotNull(message = "Beneficiary account number cannot be null")
    private Long beneficiaryAccountNumber;

    @NotNull(message = "Beneficiary IFSC code cannot be null")
    private String beneficiaryIfscCode;

    @NotNull(message = "Amount cannot be null")
    private Long amount;

    @NotNull(message = "Transaction type cannot be null")
    private String transactionType;
}
