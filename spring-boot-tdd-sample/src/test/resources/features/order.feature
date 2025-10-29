Feature: Order Management
  As a user of the order management system
  I want to manage orders
  So that I can track customer purchases

  Background:
    Given a customer exists with the following details:
      | firstName | lastName | email                |
      | John      | Doe      | john.doe@example.com |

  Scenario: Create a new order successfully
    Given I have order details with status "PENDING"
    When I create a new order
    Then the order should be created successfully
    And the order should have status "PENDING"
    And the order should have a valid order number

  Scenario: Get an existing order by id
    Given an order exists in the system
    When I request the order by its id
    Then I should receive the order details
    And the order should contain the correct information

  Scenario: Get all orders
    Given multiple orders exist in the system
    When I request all orders
    Then I should receive a list of all orders
    And the list should contain at least 2 orders

  Scenario: Update an existing order
    Given an order exists with status "PENDING"
    When I update the order status to "PAID"
    Then the order should be updated successfully
    And the order status should be "PAID"

  Scenario: Delete an order
    Given an order exists in the system
    When I delete the order
    Then the order should be deleted successfully
    And the order should not be found when requested

  Scenario: Get orders by status
    Given multiple orders exist with different statuses
    When I request orders with status "PENDING"
    Then I should receive only orders with status "PENDING"

  Scenario: Create order with invalid data
    Given I have invalid order details with empty items
    When I attempt to create the order
    Then the order creation should fail
    And I should receive a validation error

  Scenario: Update order status through multiple transitions
    Given an order exists with status "PENDING"
    When I update the order status to "PAID"
    And I update the order status to "SHIPPED"
    And I update the order status to "DELIVERED"
    Then the order status should be "DELIVERED"

  Scenario: Cannot modify cancelled order
    Given an order exists with status "CANCELLED"
    When I attempt to update the order status to "PAID"
    Then the update should fail
    And I should receive an error message

  Scenario: Create order with multiple items
    Given I have order details with multiple items:
      | productName     | sku        | quantity | unitPrice |
      | Wireless Mouse  | MOUSE-001  | 2        | 29.99     |
      | USB-C Cable     | CABLE-002  | 3        | 19.99     |
      | Laptop Stand    | STAND-003  | 1        | 89.99     |
    When I create a new order
    Then the order should be created successfully
    And the order should have 3 items
    And the order total should be calculated correctly

  Scenario: Get orders by customer
    Given multiple orders exist for the customer
    When I request orders for the customer
    Then I should receive all orders for that customer
    And all orders should belong to the same customer

  Scenario Outline: Update order status with valid transitions
    Given an order exists with status "<currentStatus>"
    When I update the order status to "<newStatus>"
    Then the order status should be "<newStatus>"

    Examples:
      | currentStatus | newStatus |
      | PENDING       | PAID      |
      | PAID          | SHIPPED   |
      | SHIPPED       | DELIVERED |
      | DELIVERED     | CANCELLED |

  Scenario Outline: Prevent invalid status transitions
    Given an order exists with status "<currentStatus>"
    When I attempt to update the order status to "<invalidStatus>"
    Then the update should fail
    And I should receive an error message

    Examples:
      | currentStatus | invalidStatus |
      | CANCELLED     | PENDING       |
      | CANCELLED     | PAID          |
      | CANCELLED     | SHIPPED       |
      | DELIVERED     | PENDING       |
      | DELIVERED     | PAID          |
      | DELIVERED     | SHIPPED       |

  Scenario: Create order and verify timestamps
    Given I have order details with status "PENDING"
    When I create a new order
    Then the order should be created successfully
    And the order should have a created timestamp
    And the order should have an updated timestamp

  Scenario: Update order and verify timestamp changes
    Given an order exists with status "PENDING"
    When I update the order status to "PAID"
    Then the order should be updated successfully
    And the updated timestamp should be more recent than created timestamp

  Scenario: Delete non-existent order
    When I attempt to delete an order with id 99999
    Then the deletion should fail
    And I should receive a not found error

  Scenario: Get non-existent order
    When I attempt to get an order with id 99999
    Then I should receive a not found error

  Scenario: Create order with minimum valid data
    Given I have order details with one item:
      | productName | sku       | quantity | unitPrice |
      | Test Item   | TEST-001  | 1        | 10.00     |
    When I create a new order
    Then the order should be created successfully
    And the order total should be 10.00

  Scenario: Verify order number format
    Given I have order details with status "PENDING"
    When I create a new order
    Then the order should be created successfully
    And the order number should match pattern "ORD-\d{8}-\d{5}"

  Scenario: Calculate order total correctly
    Given I have order details with multiple items:
      | productName | sku      | quantity | unitPrice |
      | Item A      | ITEM-A   | 2        | 25.50     |
      | Item B      | ITEM-B   | 3        | 10.00     |
      | Item C      | ITEM-C   | 1        | 99.99     |
    When I create a new order
    Then the order should be created successfully
    And the order total should be 180.99

  Scenario: Update order items
    Given an order exists with status "PENDING"
    When I update the order with new items:
      | productName | sku      | quantity | unitPrice |
      | New Item    | NEW-001  | 5        | 20.00     |
    Then the order should be updated successfully
    And the order should have 1 items
    And the order total should be 100.00

  Scenario: Order lifecycle - Complete workflow
    Given I have order details with status "PENDING"
    When I create a new order
    Then the order should be created successfully
    When I update the order status to "PAID"
    Then the order status should be "PAID"
    When I update the order status to "SHIPPED"
    Then the order status should be "SHIPPED"
    When I update the order status to "DELIVERED"
    Then the order status should be "DELIVERED"

  Scenario: Cannot create order without customer
    Given I have order details without customer
    When I attempt to create the order
    Then the order creation should fail
    And I should receive an error message about missing customer

  Scenario: Cannot create order with zero quantity items
    Given I have order details with invalid item:
      | productName | sku      | quantity | unitPrice |
      | Bad Item    | BAD-001  | 0        | 10.00     |
    When I attempt to create the order
    Then the order creation should fail
    And I should receive a validation error

  Scenario: Cannot create order with negative price
    Given I have order details with invalid item:
      | productName | sku      | quantity | unitPrice |
      | Bad Item    | BAD-001  | 1        | -10.00    |
    When I attempt to create the order
    Then the order creation should fail
    And I should receive a validation error

  @smoke
  Scenario: Smoke test - Basic order operations
    Given I have order details with status "PENDING"
    When I create a new order
    Then the order should be created successfully
    When I request the order by its id
    Then I should receive the order details
    When I delete the order
    Then the order should be deleted successfully

  @regression
  Scenario: Regression test - All CRUD operations
    Given I have order details with status "PENDING"
    When I create a new order
    Then the order should be created successfully
    When I request all orders
    Then I should receive a list of all orders
    When I update the order status to "PAID"
    Then the order status should be "PAID"
    When I request the order by its id
    Then I should receive the order details
    When I delete the order
    Then the order should be deleted successfully
