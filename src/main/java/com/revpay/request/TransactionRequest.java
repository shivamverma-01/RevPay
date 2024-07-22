package com.revpay.request;

import java.math.BigDecimal;
import java.math.BigInteger;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionRequest {

    @NotNull(message = "Bank Account Number cannot be null")
    private Long bankAccountNumber;

    @NotEmpty(message = "IFSC Code cannot be empty")
    @Pattern(regexp = "\\w{1,8}", message = "IFSC Code must be alphanumeric and up to 8 characters")
    private String ifscCode;

    @NotEmpty(message = "Transaction Type cannot be empty")
    @Pattern(regexp = "CREDIT|DEBIT", message = "Transaction Type must be CREDIT or DEBIT")
    private String transactionType;

    @NotNull(message = "Amount cannot be null")
    private Long amount;


}
