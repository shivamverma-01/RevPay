package com.revpay.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.revpay.exception.AccountAlreadyExistsException;
import com.revpay.model.Account;
import com.revpay.model.Account.ActivationStatus;
import com.revpay.model.CustomUserDetails;
import com.revpay.model.Transaction;
import com.revpay.model.Transfer;
import com.revpay.repository.AccountRepository;
import com.revpay.repository.TransactionRepository;
import com.revpay.repository.TransferRepository;
import com.revpay.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransferRepository transferRepository;

    @Autowired
    private UserRepository userRepository;

    public Account createAccount(Long userId, Long bankAccountNumber, String ifscCode, Integer activationStatus) {
        Optional<Account> existingAccount = accountRepository.findByBankAccountNumber(bankAccountNumber);
        if (existingAccount.isPresent()) {
            throw new AccountAlreadyExistsException("Bank account already exists");
        }
        Account account = new Account();
        account.setUserId(userId);
        account.setBankAccountNumber(bankAccountNumber);
        account.setIfscCode(ifscCode);
        account.setActivationStatus(activationStatus == 1 ? Account.ActivationStatus.ACTIVE : Account.ActivationStatus.INACTIVE);
        account.setBalance(0L);
        account.setCreated_at(LocalDateTime.now());
        account.setLastModified_at(LocalDateTime.now());

        return accountRepository.save(account);
    }

    public String processTransaction(Long bankAccountNumber, String ifscCode, String transactionType, Long amount) {
        if (amount <= 0) {
            return "Amount must be positive";
        }

        if (transactionType == null || (!transactionType.equalsIgnoreCase("DEBIT") && !transactionType.equalsIgnoreCase("CREDIT"))) {
            return "Invalid transaction type";
        }

        // Fetch the account details
        Optional<Account> accountOptional = accountRepository.findByBankAccountNumber(bankAccountNumber);
        if (!accountOptional.isPresent()) {
            return "Invalid Account Details for Transaction";
        }

        Account account = accountOptional.get();
        if (!account.getIfscCode().equals(ifscCode)) {
            return "Invalid Account Details for Transaction";
        }

        // Check account activation status
        if (account.getActivationStatus() != Account.ActivationStatus.ACTIVE) {
            return "Account is inactive";
        }

        // Check transaction type permissions
        if ("CREDIT".equalsIgnoreCase(transactionType) && !account.getCreditAllowed()) {
            return "Credit transactions are not allowed";
        }

        if ("DEBIT".equalsIgnoreCase(transactionType) && !account.getDebitAllowed()) {
            return "Debit transactions are not allowed";
        }

        // Calculate daily withdrawal limit
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        Long dailyLimit = account.getDailyWithdrawalLimit();
        
        // Fetch the total amount of withdrawals today
        Long totalTodayWithdrawals = transactionRepository.getTotalAmountByAccountAndDate(account.getAccountId(), startOfDay, endOfDay);

        // Log values for debugging
        System.out.println("Total Today Withdrawals: " + totalTodayWithdrawals);
        System.out.println("Amount Being Processed: " + amount);
        System.out.println("Daily Limit: " + dailyLimit);

        Long totalWithdrawalsIncludingCurrent = totalTodayWithdrawals + (transactionType.equalsIgnoreCase("DEBIT") ? amount : 0);
        System.out.println("Total Withdrawals Including Current: " + totalWithdrawalsIncludingCurrent);

        // Check if withdrawal exceeds daily limit
        if ("DEBIT".equalsIgnoreCase(transactionType) && totalWithdrawalsIncludingCurrent > dailyLimit) {
            return "Withdrawal exceeds daily limit";
        }

        // Check if sufficient balance is available for debit transaction
        if ("DEBIT".equalsIgnoreCase(transactionType) && account.getBalance() < amount) {
            return "Insufficient balance";
        }

        // Perform the transaction
        if ("DEBIT".equalsIgnoreCase(transactionType)) {
            account.setBalance(account.getBalance() - amount);

            Transaction transaction = new Transaction();
            transaction.setAccountId(account.getAccountId());
            transaction.setTransactionType("DEBIT");
            transaction.setTransactionAmount(amount);
            transaction.setCreated_at(LocalDateTime.now());
            transactionRepository.save(transaction);

        } else if ("CREDIT".equalsIgnoreCase(transactionType)) {
            account.setBalance(account.getBalance() + amount);

            Transaction transaction = new Transaction();
            transaction.setAccountId(account.getAccountId());
            transaction.setTransactionType("CREDIT");
            transaction.setTransactionAmount(amount);
            transaction.setCreated_at(LocalDateTime.now());
            transactionRepository.save(transaction);

        } else {
            return "Invalid transaction type";
        }

        // Update account modification time
        account.setLastModified_at(LocalDateTime.now());
        accountRepository.save(account);

        return "Transaction successful";
    }



    public String createTransfer(Long accountId, Long beneficiaryAccountNumber, String beneficiaryIfscCode, Long amount, String transactionType) {
        if (amount <= 0) {
            return "Transfer amount must be positive";
        }

        if (transactionType == null || (!transactionType.equalsIgnoreCase("DEPOSIT") && !transactionType.equalsIgnoreCase("CREDIT"))) {
            return "Invalid transaction type";
        }

        Optional<Account> senderOptional = accountRepository.findById(accountId);
        if (!senderOptional.isPresent()) {
            return "Invalid Sender Account Details";
        }

        Account sender = senderOptional.get();
        if (sender.getActivationStatus() != Account.ActivationStatus.ACTIVE || (transactionType.equalsIgnoreCase("DEBIT") && !sender.getDebitAllowed())) {
            return "Sender account is inactive or debit transactions are not allowed";
        }

        // Calculate daily withdrawal limit
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        Long dailyLimit = sender.getDailyWithdrawalLimit();
        Long totalTodayWithdrawals = transactionRepository.getTotalAmountByAccountAndDate(accountId, startOfDay, endOfDay);

        // Log values for debugging
        System.out.println("Total Today Withdrawals: " + totalTodayWithdrawals);
        System.out.println("Amount Being Processed: " + amount);
        System.out.println("Daily Limit: " + dailyLimit);
        System.out.println("Total Withdrawals Including Current: " + (totalTodayWithdrawals + (transactionType.equalsIgnoreCase("DEBIT") ? amount : 0)));

        Long totalWithdrawalsIncludingCurrent = totalTodayWithdrawals + (transactionType.equalsIgnoreCase("DEBIT") ? amount : 0);

        if (totalWithdrawalsIncludingCurrent > dailyLimit) {
            return "Withdrawal exceeds daily limit";
        }

        if (transactionType.equalsIgnoreCase("DEBIT") && sender.getBalance() < amount) {
            return "Insufficient balance";
        }

        Optional<Account> beneficiaryOptional = accountRepository.findByBankAccountNumber(beneficiaryAccountNumber);
        if (!beneficiaryOptional.isPresent()) {
            return "Invalid Beneficiary Account Details";
        }

        Account beneficiary = beneficiaryOptional.get();
        if (beneficiaryIfscCode == null || beneficiaryIfscCode.isEmpty() || !beneficiaryIfscCode.equals(beneficiary.getIfscCode())) {
            return "Invalid Beneficiary Account Details";
        }

        if (beneficiary.getActivationStatus() != Account.ActivationStatus.ACTIVE || !beneficiary.getCreditAllowed()) {
            return "Beneficiary account is inactive or credit transactions are not allowed";
        }

        if (transactionType.equalsIgnoreCase("DEBIT")) {
            sender.setBalance(sender.getBalance() - amount);
            accountRepository.save(sender);

            beneficiary.setBalance(beneficiary.getBalance() + amount);
            accountRepository.save(beneficiary);

            Transaction senderTransaction = new Transaction();
            senderTransaction.setAccountId(sender.getAccountId());
            senderTransaction.setTransactionType("DEPOSIT");
            senderTransaction.setTransactionAmount(amount);
            senderTransaction.setCreated_at(LocalDateTime.now());

            Transaction savedSenderTransaction = transactionRepository.save(senderTransaction);

            Transaction beneficiaryTransaction = new Transaction();
            beneficiaryTransaction.setAccountId(beneficiary.getAccountId());
            beneficiaryTransaction.setTransactionType("CREDIT");
            beneficiaryTransaction.setTransactionAmount(amount);
            beneficiaryTransaction.setCreated_at(LocalDateTime.now());

            Transaction savedBeneficiaryTransaction = transactionRepository.save(beneficiaryTransaction);

            Transfer transfer = new Transfer();
            transfer.setSender_transaction_Id(savedSenderTransaction.getTransactionId());
            transfer.setBeneficiary_transaction_Id(savedBeneficiaryTransaction.getTransactionId());
            transfer.setCreated_At(LocalDateTime.now());
            transferRepository.save(transfer);

        } else if (transactionType.equalsIgnoreCase("CREDIT")) {
            sender.setBalance(sender.getBalance() + amount);
            accountRepository.save(sender);

            Transaction senderTransaction = new Transaction();
            senderTransaction.setAccountId(sender.getAccountId());
            senderTransaction.setTransactionType("CREDIT");
            senderTransaction.setTransactionAmount(amount);
            senderTransaction.setCreated_at(LocalDateTime.now());

            Transaction savedSenderTransaction = transactionRepository.save(senderTransaction);

            Transfer transfer = new Transfer();
            transfer.setSender_transaction_Id(savedSenderTransaction.getTransactionId());
            transfer.setCreated_At(LocalDateTime.now());
            transferRepository.save(transfer);
        }

        return "Transfer successful";
    }


    public Long getBalanceByAccountId(Long accountId, Authentication authentication) {
        // Extract the user ID from the authentication object
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long userId = ((CustomUserDetails) userDetails).getUserId(); // Adjust this based on how user ID is retrieved

        // Find the account by account ID
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (!accountOptional.isPresent()) {
            throw new RuntimeException("Account not found");
        }

        Account account = accountOptional.get();

        // Check if the account belongs to the authenticated user
        if (!account.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to account");
        }

        // Return the account balance
        return account.getBalance();
    }
}
