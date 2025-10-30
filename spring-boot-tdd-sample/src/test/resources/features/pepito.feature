Feature: Pepito
  As a user of the order management system
  I want to manage orders
  So that I can track customer purchases

  Background:
    Given A customer exists with the following details:
      | firstName | lastName | email                |
      | John      | Doe      | john.doe@example-bdd.com |

  Scenario: Create a new order successfully
    Given I have order details with status "PENDING"
    When I create a new order
    Then The order should be created successfully
    And The order should have status "PENDING"
    And The order should have a valid order number

  Scenario: Get an existing order by id
    Given An order exists in the system
    When I request the order by its id
    Then I should receive the order details
    And The order should contain the correct information

  Scenario: Get all orders
    Given Multiple orders exist in the system
    When I request all orders
    Then I should receive a list of all orders
    And The list should contain at least 2 orders

  Scenario: Update an existing order
    Given An order exists with status "PENDING"
    When I update the order status to "PAID"
    Then The order should be updated successfully
    And The order should have status "PAID"

  Scenario: Delete an order
    Given An order exists in the system
    When I delete the order
    Then The order should be deleted successfully
    And The order should not be found when requested
