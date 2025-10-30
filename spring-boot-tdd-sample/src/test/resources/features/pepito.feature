Feature: Pepito
  As a user of the order management system
  I want to manage orders
  So that I can track customer purchases

  Background:
    Given A customer exists with the following details:
      | firstName | lastName | email                |
      | John      | Doe      | john.doe@example.com |

  Scenario: Create a new order successfully
    Given I have order details with status "PENDING"
    When I create a new order
    Then the order should be created successfully
    And the order should have status "PENDING"
    And the order should have a valid order number
