package com.revpay.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revpay.exception.AccountAlreadyExistsException;
import com.revpay.model.CustomUserDetails;
import com.revpay.request.AccountCreationRequest;
import com.revpay.request.TransactionRequest;
import com.revpay.request.TransferRequest;
import com.revpay.service.AccountService;

import org.springframework.http.converter.HttpMessageNotReadableException;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createAccount(@RequestBody AccountCreationRequest request, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId(); // Retrieve userId from CustomUserDetails

        try {
            accountService.createAccount(userId, request.getBankAccountNumber(), request.getIfscCode(), request.getActivationStatus());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Account created successfully");
            response.put("status", HttpStatus.CREATED.value());

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (AccountAlreadyExistsException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("status", HttpStatus.BAD_REQUEST.value());

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("status", HttpStatus.BAD_REQUEST.value());

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/transaction")
    public ResponseEntity<Map<String, Object>> processTransaction(@Valid @RequestBody TransactionRequest request) {
        String result = accountService.processTransaction(
                request.getBankAccountNumber(),
                request.getIfscCode(),
                request.getTransactionType(),
                request.getAmount()
        );

        Map<String, Object> response = new HashMap<>();
        if (result.equals("Transaction successful")) {
            response.put("message", result);
            response.put("status", HttpStatus.OK.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("message", result);
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<Map<String, Object>> createTransfer(@Valid @RequestBody TransferRequest request) {
        String result = accountService.createTransfer(
                request.getAccountId(),
                request.getBeneficiaryAccountNumber(),
                request.getBeneficiaryIfscCode(),
                request.getAmount(),
                request.getTransactionType()
        );

        Map<String, Object> response = new HashMap<>();
        if (result.equals("Transfer successful")) {
            response.put("message", result);
            response.put("status", HttpStatus.OK.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("message", result);
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/balance/{accountId}")
    public ResponseEntity<Map<String, Object>> getBalanceByAccountId(@PathVariable Long accountId, Authentication authentication) {
        try {
            Long balance = accountService.getBalanceByAccountId(accountId, authentication);

            Map<String, Object> response = new HashMap<>();
            response.put("balance", balance);
            response.put("status", HttpStatus.OK.value());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("status", HttpStatus.NOT_FOUND.value());

            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Malformed JSON request");
        response.put("status", HttpStatus.BAD_REQUEST.value());
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalExceptions(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
