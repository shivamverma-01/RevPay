package com.revpay.request;

import java.math.BigInteger;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountCreationRequest {

    @Column(nullable = false, unique = true)
    @NotNull(message = "Bank Account Number cannot be null")
    @Pattern(regexp = "\\d{1,10}", message = "Bank Account Number must be numeric and up to 10 digits")
    private Long bankAccountNumber;

    @NotEmpty(message = "IFSC Code cannot be empty")
    @Pattern(regexp = "\\w{1,8}", message = "IFSC Code must be alphanumeric and up to 8 characters")
    private String ifscCode;

    @NotNull(message = "Activation Status cannot be null")
    private Integer activationStatus; // 0 for INACTIVE, 1 for ACTIVE
}
