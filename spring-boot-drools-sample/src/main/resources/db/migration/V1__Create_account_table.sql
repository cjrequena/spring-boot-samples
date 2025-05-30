CREATE TABLE account (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    balance DECIMAL(19,2) NOT NULL,
    account_type VARCHAR(20) NOT NULL,
    premium BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT AUTO_INCREMENT
);

CREATE UNIQUE INDEX idx_account_number ON account(account_number);
