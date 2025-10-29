Feature: Pepito
  As a user of the order management system
  I want to manage orders
  So that I can track customer purchases

  Background:
    Given A customer exists with the following details:
      | firstName | lastName | email                |
      | John      | Doe      | john.doe@example.com |

  Scenario: Hello world
    Given the app is running
    When I say hello
    Then I should get "Hello, World!"
