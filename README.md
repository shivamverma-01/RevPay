
RevPay Payment Gateway
======================

Overview
--------

RevPay is a payment gateway service for businesses to manage payments through REST APIs.

Features
--------

*   **Business Registration**
*   **Account Management**
    *   Create multiple accounts
    *   Set activation status (ACTIVE/INACTIVE)
    *   Control transaction types (CREDIT/DEBIT)
    *   Set daily withdrawal limits
*   **Transaction Handling**
    *   Deposit and withdrawal transactions
*   **Balance Inquiry**

API Endpoints
-------------

*   `POST http://localhost:8080/api/business/register`: Register a new business.
*   `POST http://localhost:8080/api/accounts/create`: Create a new account.
*   `POST http://localhost:8080/api/accounts/transaction`: Process a deposit or withdrawal transaction.
*   `POST http://localhost:8080/api/accounts/transfer`: Create a transfer between accounts.
*   `GET http://localhost:8080/api/accounts/balance/{accountId}`: Get the balance of an account.

Running the Project
-------------------

### Prerequisites

*   Java 11 or higher
*   Maven
*   Spring Boot
*   MySQL

### Setup

1.  Clone the repository:
    
        git clone https://github.com/shivamverma-01/RevPay.git
    
2.  Navigate to the project directory:
    
        cd revpay
    
3.  Build the project:
    
        mvn clean install
    
4.  Run the application:
    
        mvn spring-boot:run
    

Configuration
-------------

Update `application.properties` in `src/main/resources` for custom configuration.
