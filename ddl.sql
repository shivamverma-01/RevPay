
Create the `revpay_payments` database if it is not present already

CREATE DATABASE IF NOT EXISTS revpay_payment;


`users`: Manages user credentials and usernames.

CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `password` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_r43af9ap4edm43mmtq01oddj6` (`username`)
) 


`accounts`: Stores account details, balances, and transaction permissions.

CREATE TABLE `accounts` (
  `account_id` bigint NOT NULL AUTO_INCREMENT,
  `activation_status` enum('ACTIVE','INACTIVE') NOT NULL,
  `balance` bigint NOT NULL,
  `bank_account_number` bigint NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `credit_allowed` bit(1) NOT NULL,
  `daily_withdrawal_limit` bigint NOT NULL,
  `debit_allowed` bit(1) NOT NULL,
  `ifsc_code` varchar(255) NOT NULL,
  `last_modified_at` datetime(6) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`account_id`),
  UNIQUE KEY `UK_ga46kn8v5cjecivi5o4gw2mog` (`bank_account_number`)
) 


`transactions`: Logs financial transactions including amount and type.

CREATE TABLE `transactions` (
  `transaction_id` bigint NOT NULL AUTO_INCREMENT,
  `account_id` bigint NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `transaction_amount` bigint NOT NULL,
  `transaction_type` varchar(255) NOT NULL,
  PRIMARY KEY (`transaction_id`)
)

`transfers`: Records transfers between accounts with timestamps.

CREATE TABLE `transfers` (
  `transfer_id` bigint NOT NULL AUTO_INCREMENT,
  `beneficiary_transaction_id` bigint NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `sender_transaction_id` bigint NOT NULL,
  PRIMARY KEY (`transfer_id`)
)
