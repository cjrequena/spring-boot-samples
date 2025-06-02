CREATE TABLE transaction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    reason VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_transaction_account FOREIGN KEY (account_id) REFERENCES account(id)
);

CREATE INDEX idx_transaction_account ON transaction(account_id);
CREATE INDEX idx_transaction_timestamp ON transaction(timestamp);
