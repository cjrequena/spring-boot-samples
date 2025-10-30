-- ===================================================================
--  Flyway Migration: Insert demo data for Customers and Orders
--  Works with PostgreSQL and H2 (MODE=PostgreSQL)
-- ===================================================================

-- Insert demo customers
INSERT INTO customers (first_name, last_name, email, phone_number, created_at, updated_at)
VALUES
  ('John',  'Doe',   'john.doe@example.com',   '555-0101', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Jane',  'Smith', 'jane.smith@example.com', '555-0202', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Alice', 'Brown', 'alice.brown@example.com','555-0303', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Bob',   'Taylor','bob.taylor@example.com', '555-0404', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert demo orders
-- Using DATEADD() instead of PostgreSQL's INTERVAL for cross-compatibility
INSERT INTO orders (order_number, order_date, status, total_amount, customer_id, created_at, updated_at)
VALUES
  ('ORD-20251030-00001', DATEADD('DAY', -10, CURRENT_TIMESTAMP), 'PENDING',   125.50, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-20251030-00002', DATEADD('DAY',  -8, CURRENT_TIMESTAMP), 'SHIPPED',   300.00, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-20251030-00003', DATEADD('DAY',  -5, CURRENT_TIMESTAMP), 'DELIVERED',  89.99, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-20251030-00004', DATEADD('DAY',  -3, CURRENT_TIMESTAMP), 'CANCELLED',  45.00, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ORD-20251030-00005', DATEADD('DAY',  -1, CURRENT_TIMESTAMP), 'PENDING',   220.75, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
