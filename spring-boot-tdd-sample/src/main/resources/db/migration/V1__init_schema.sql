-- ===================================================================
--  Flyway Migration: Initial schema for Customers and Orders
--  Compatible with PostgreSQL and H2 (MODE=PostgreSQL)
-- ===================================================================

-- Drop existing tables (for clean re-run in dev environments)
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS customers;

-- =========================
-- Table: customers
-- =========================
CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    phone_number VARCHAR(20),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- =========================
-- Table: orders
-- =========================
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    order_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    total_amount NUMERIC(19, 2) NOT NULL,
    customer_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id)
        REFERENCES customers (id)
        ON DELETE CASCADE
);

-- =========================
-- Indexes for orders
-- =========================
CREATE INDEX idx_order_number ON orders (order_number);
CREATE INDEX idx_order_status ON orders (status);
CREATE INDEX idx_order_date ON orders (order_date);

-- =========================
-- Default values (optional)
-- =========================
-- You could optionally add:
-- ALTER TABLE orders ALTER COLUMN status SET DEFAULT 'PENDING';
-- ALTER TABLE orders ALTER COLUMN order_date SET DEFAULT CURRENT_TIMESTAMP;

-- ===================================================================
-- End of V1__init_schema.sql
-- ===================================================================
