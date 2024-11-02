RevPay Payment Gateway
======================

RevPay is a RESTful payment gateway application designed for businesses to manage payments and financial transactions effectively. The project leverages Spring Boot for backend development, with MySQL for database management, and is fully containerized using Docker for seamless deployment.

Features
--------

*   **Business Registration:** Register new businesses to use the payment gateway.
*   **Account Management:** Manage multiple accounts per business, control transaction types, and set activation status.
*   **Transaction Handling:** Enable deposits, withdrawals, and transfers.
*   **Balance Inquiry:** Quickly check account balances.

API Endpoints
-------------

*   `POST /api/business/register`: Register a new business.
*   `POST /api/accounts/create`: Create a new account.
*   `POST /api/accounts/transaction`: Process a deposit or withdrawal transaction.
*   `POST /api/accounts/transfer`: Transfer funds between accounts.
*   `GET /api/accounts/balance/{accountId}`: Retrieve account balance information.

Running the Project with Docker
-------------------------------

### Prerequisites

*   Docker

### Setup Instructions

1.  **Clone the repository:**
    
        git clone https://github.com/shivamverma-01/RevPay.git
        cd RevPay
                    
    
2.  **Start the application with Docker Compose:**
    
        docker-compose up --build
    
    This command will:
    
    *   Set up a MySQL container for the database.
    *   Launch the Spring Boot backend service.
    
    The application will be accessible at `http://localhost:8080`.
    
3.  **Verify API Endpoints:** Use Postman or curl to make requests to the endpoints listed above.

Complete Project Details
------------------------

For additional project details, visit the complete repository [here](https://github.com/shivamverma-01/RevPay/tree/main).

Contact
-------

For further questions, please feel free to contact **Shivam Verma** at [verma.shivam2605@gmail.com](mailto:verma.shivam2605@gmail.com).
