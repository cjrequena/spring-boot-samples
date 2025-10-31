package com.cjrequena.sample.cucumber.steps;

import com.cjrequena.sample.controller.dto.OrderDTO;
import com.cjrequena.sample.domain.model.enums.OrderStatus;
import com.cjrequena.sample.persistence.entity.CustomerEntity;
import com.cjrequena.sample.persistence.jpa.repository.CustomerRepository;
import com.cjrequena.sample.persistence.jpa.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@CucumberContextConfiguration
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PepitoSteps {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private CustomerRepository customerRepository;

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private ObjectMapper objectMapper;

  private CustomerEntity customer;
  private OrderDTO currentOrderDTO;
  private ResultActions lastResult;
  private OrderDTO createdOrder;
  private List<OrderDTO> retrievedOrders;

  // ------------------------------
  // Given Steps
  // ------------------------------

  @Given("A customer exists with the following details:")
  public void a_customer_exists_with_the_following_details(io.cucumber.datatable.DataTable dataTable) {
    Map<String, String> customerData = dataTable.asMaps().get(0);
    customer = CustomerEntity
      .builder()
      .firstName(customerData.get("firstName"))
      .lastName(customerData.get("lastName"))
      .email(customerData.get("email"))
      .phoneNumber("1234567890")
      .build();
    customer = customerRepository.save(customer);
  }

  @Given("I have order details with status {string}")
  public void i_have_order_details_with_status(String status) {
    currentOrderDTO = OrderDTO.builder()
      .orderDate(LocalDateTime.now())
      .status(OrderStatus.valueOf(status))
      .totalAmount(BigDecimal.valueOf(100.00))
      .customerId(customer.getId())
      .build();
  }

  @Given("An order exists in the system")
  public void an_order_exists_in_the_system() throws Exception {
    i_have_order_details_with_status("PENDING");
    i_create_a_new_order();
    the_order_should_be_created_successfully();
  }

  @Given("Multiple orders exist in the system")
  public void multiple_orders_exist_in_the_system() throws Exception {
    for (int i = 0; i < 2; i++) {
      i_have_order_details_with_status("PENDING");
      i_create_a_new_order();
    }
  }

  @Given("An order exists with status {string}")
  public void an_order_exists_with_status(String status) throws Exception {
    i_have_order_details_with_status(status);
    i_create_a_new_order();
    the_order_should_be_created_successfully();
  }

  @Given("Multiple orders exist with different statuses")
  public void multiple_orders_exist_with_different_statuses() throws Exception {
    i_have_order_details_with_status("PENDING");
    i_create_a_new_order();

    i_have_order_details_with_status("PAID");
    i_create_a_new_order();
  }

  @Given("I have invalid order with invalid amount")
  public void i_have_invalid_order_number() {
    currentOrderDTO = OrderDTO.builder()
      .orderDate(LocalDateTime.now())
      .status(OrderStatus.valueOf("PENDING"))
      .totalAmount(BigDecimal.valueOf(-100.00))
      .customerId(customer.getId())
      .build();
  }

  // ------------------------------
  // When Steps
  // ------------------------------

  @When("I create a new order")
  public void i_create_a_new_order() throws Exception {
    lastResult = mockMvc.perform(post("/api/orders")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(currentOrderDTO)));

    MvcResult result = lastResult.andReturn();
    if (result.getResponse().getStatus() == 201) {
      createdOrder = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        OrderDTO.class);
    }
  }

  @When("I request the order by its id")
  public void i_request_the_order_by_its_id() throws Exception {
    lastResult = mockMvc.perform(get("/api/orders/" + createdOrder.getId()));

    MvcResult result = lastResult.andReturn();
    if (result.getResponse().getStatus() == 200) {
      createdOrder = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        OrderDTO.class);
    }
  }

  @When("I request all orders")
  public void i_request_all_orders() throws Exception {
    lastResult = mockMvc.perform(get("/api/orders"));
    MvcResult result = lastResult.andReturn();
    if (result.getResponse().getStatus() == 200) {
      retrievedOrders = List.of(objectMapper.readValue(
        result.getResponse().getContentAsString(),
        OrderDTO[].class));
    }
  }

  @When("I update the order status to {string}")
  public void i_update_the_order_status_to(String newStatus) throws Exception {
    lastResult = mockMvc
      .perform(patch("/api/orders/" + createdOrder.getId() + "/status")
        .param("status", newStatus));

    MvcResult result = lastResult.andReturn();
    if (result.getResponse().getStatus() == 200) {
      createdOrder = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        OrderDTO.class);
    }
  }

  @When("I delete the order")
  public void i_delete_the_order() throws Exception {
    lastResult = mockMvc.perform(delete("/api/orders/" + createdOrder.getId()));
  }

  @When("I request orders with status {string}")
  public void i_request_orders_with_status(String status) throws Exception {
    lastResult = mockMvc
      .perform(get("/api/orders")
        .param("status", status));

    MvcResult result = lastResult.andReturn();
    if (result.getResponse().getStatus() == 200) {
      retrievedOrders = List.of(objectMapper.readValue(result.getResponse().getContentAsString(), OrderDTO[].class));
    }
  }

  @When("I attempt to create the order")
  public void i_attempt_to_create_the_order() {
    try {
      lastResult = mockMvc.perform(post("/api/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(currentOrderDTO)));
    } catch (Exception e) {
      // Exception expected for invalid data
    }
  }

  // ------------------------------
  // Then Steps
  // ------------------------------

  @Then("The order should be created successfully")
  public void the_order_should_be_created_successfully() throws Exception {
    lastResult.andExpect(status().isCreated());
    assertThat(createdOrder).isNotNull();
    assertThat(createdOrder.getId()).isNotNull();
  }

  @Then("The order should have status {string}")
  public void the_order_should_have_status(String status) {
    assertThat(createdOrder.getStatus()).isEqualTo(OrderStatus.valueOf(status));
  }

  @Then("The order should have a valid order number")
  public void the_order_should_have_a_valid_order_number() {
    assertThat(createdOrder.getOrderNumber()).isNotNull();
    assertThat(createdOrder.getOrderNumber()).matches("^ORD-\\d{8}-\\d{5}$");
  }

  @Then("I should receive the order details")
  public void i_should_receive_the_order_details() throws Exception {
    lastResult.andExpect(status().isOk());
    assertThat(createdOrder).isNotNull();
  }

  @Then("The order should contain the correct information")
  public void the_order_should_contain_the_correct_information() {
    assertThat(createdOrder.getCustomerId()).isEqualTo(customer.getId());
    assertThat(createdOrder.getOrderNumber()).isNotNull();
  }

  @Then("I should receive a list of all orders")
  public void i_should_receive_a_list_of_all_orders() throws Exception {
    lastResult.andExpect(status().isOk());
    assertThat(retrievedOrders).isNotNull();
  }

  @Then("The list should contain at least {int} orders")
  public void the_list_should_contain_at_least_orders(Integer count) {
    assertThat(retrievedOrders).hasSizeGreaterThanOrEqualTo(count);
  }

  @Then("The order should be updated successfully")
  public void the_order_should_be_updated_successfully() throws Exception {
    lastResult.andExpect(status().isOk());
  }

  @Then("The order should be deleted successfully")
  public void the_order_should_be_deleted_successfully() throws Exception {
    lastResult.andExpect(status().isNoContent());
  }

  @Then("The order should not be found when requested")
  public void the_order_should_not_be_found_when_requested() throws Exception {
    mockMvc
      .perform(get("/api/orders/" + createdOrder.getId()))
      .andExpect(status().isNotFound());
  }

  @Then("I should receive only orders with status {string}")
  public void i_should_receive_only_orders_with_status(String status) {
    assertThat(retrievedOrders).isNotEmpty();
    assertThat(retrievedOrders).allMatch(order -> order.getStatus().equals(OrderStatus.valueOf(status)));
  }

  @Then("The order creation should fail")
  public void the_order_creation_should_fail() throws Exception {
    lastResult.andExpect(status().isBadRequest());
  }

  @Then("I should receive a validation error")
  public void i_should_receive_a_validation_error() throws Exception {
    lastResult.andExpect(jsonPath("$.validation_errors").exists());
  }

}
