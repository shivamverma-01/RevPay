-- Create the database if it does not exist
CREATE DATABASE IF NOT EXISTS revpay_payments;

-- Switch to the `revpay_payments` database
USE revpay_payments;

-- Create the `users` table
CREATE TABLE `users` (
 `id` BIGINT NOT NULL AUTO_INCREMENT,
 `password` VARCHAR(255) NOT NULL,
 `username` VARCHAR(255) NOT NULL,
 PRIMARY KEY (`id`),
 UNIQUE KEY `UK_r43af9ap4edm43mmtq01oddj6` (`username`)
);

-- Create the `accounts` table
CREATE TABLE `accounts` (
 `account_id` BIGINT NOT NULL AUTO_INCREMENT,
 `activation_status` ENUM('ACTIVE','INACTIVE') NOT NULL,
 `balance` BIGINT NOT NULL,
 `bank_account_number` BIGINT NOT NULL,
 `created_at` DATETIME(6) NOT NULL,
 `credit_allowed` BIT(1) NOT NULL,
 `daily_withdrawal_limit` BIGINT NOT NULL,
 `debit_allowed` BIT(1) NOT NULL,
 `ifsc_code` VARCHAR(255) NOT NULL,
 `last_modified_at` DATETIME(6) NOT NULL,
 `user_id` BIGINT NOT NULL,
 PRIMARY KEY (`account_id`),
 UNIQUE KEY `UK_ga46kn8v5cjecivi5o4gw2mog` (`bank_account_number`),
 FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
);

-- Create the `transactions` table
CREATE TABLE `transactions` (
 `transaction_id` BIGINT NOT NULL AUTO_INCREMENT,
 `account_id` BIGINT NOT NULL,
 `created_at` DATETIME(6) NOT NULL,
 `transaction_amount` BIGINT NOT NULL,
 `transaction_type` VARCHAR(255) NOT NULL,
 PRIMARY KEY (`transaction_id`),
 FOREIGN KEY (`account_id`) REFERENCES `accounts`(`account_id`)
);

-- Create the `transfers` table
CREATE TABLE `transfers` (
 `transfer_id` BIGINT NOT NULL AUTO_INCREMENT,
 `beneficiary_transaction_id` BIGINT NOT NULL,
 `created_at` DATETIME(6) NOT NULL,
 `sender_transaction_id` BIGINT NOT NULL,
 PRIMARY KEY (`transfer_id`),
 FOREIGN KEY (`beneficiary_transaction_id`) REFERENCES `transactions`(`transaction_id`),
 FOREIGN KEY (`sender_transaction_id`) REFERENCES `transactions`(`transaction_id`)
);
