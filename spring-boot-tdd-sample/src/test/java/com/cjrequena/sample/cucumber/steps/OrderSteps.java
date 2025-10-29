package com.cjrequena.sample.cucumber.steps;

import com.cjrequena.sample.controller.dto.OrderDTO;
import com.cjrequena.sample.controller.dto.OrderItemDTO;
import com.cjrequena.sample.domain.model.enums.OrderStatus;
import com.cjrequena.sample.persistence.entity.CustomerEntity;
import com.cjrequena.sample.persistence.jpa.repository.CustomerRepository;
import com.cjrequena.sample.persistence.jpa.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderSteps {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private CustomerRepository customerRepository;

  @Autowired
  private OrderRepository orderRepository;

  private final ObjectMapper objectMapper;
  private CustomerEntity testCustomer;
  private OrderDTO currentOrderDTO;
  private ResultActions lastResult;
  private OrderDTO createdOrder;
  private List<OrderDTO> retrievedOrders;

  public OrderSteps() {
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());
  }

  // Background Steps
  @Given("a customer exists with the following details:")
  public void aCustomerExistsWithTheFollowingDetails(DataTable dataTable) {
    Map<String, String> customerData = dataTable.asMaps().get(0);

    testCustomer = CustomerEntity.builder()
      .firstName(customerData.get("firstName"))
      .lastName(customerData.get("lastName"))
      .email(customerData.get("email"))
      .phoneNumber("1234567890")
      .build();

    testCustomer = customerRepository.save(testCustomer);
  }

  // Given Steps

  @Given("I have order details with status {string}")
  public void iHaveOrderDetailsWithStatus(String status) {
    OrderItemDTO itemDTO = OrderItemDTO.builder()
      .productName("Test Product")
      .sku("TEST-001")
      .quantity(2)
      .unitPrice(BigDecimal.valueOf(50.00))
      .build();

    currentOrderDTO = OrderDTO.builder()
      .orderDate(LocalDateTime.now())
      .status(OrderStatus.valueOf(status))
      .totalAmount(BigDecimal.valueOf(100.00))
      .customerId(testCustomer.getId())
      .items(List.of(itemDTO))
      .build();
  }

  @Given("an order exists in the system")
  public void anOrderExistsInTheSystem() throws Exception {
    iHaveOrderDetailsWithStatus("PENDING");
    iCreateANewOrder();
    theOrderShouldBeCreatedSuccessfully();
  }

  @Given("multiple orders exist in the system")
  public void multipleOrdersExistInTheSystem() throws Exception {
    for (int i = 0; i < 2; i++) {
      iHaveOrderDetailsWithStatus("PENDING");
      iCreateANewOrder();
    }
  }

  @Given("an order exists with status {string}")
  public void anOrderExistsWithStatus(String status) throws Exception {
    iHaveOrderDetailsWithStatus(status);
    iCreateANewOrder();
    theOrderShouldBeCreatedSuccessfully();
  }

  @Given("multiple orders exist with different statuses")
  public void multipleOrdersExistWithDifferentStatuses() throws Exception {
    iHaveOrderDetailsWithStatus("PENDING");
    iCreateANewOrder();

    iHaveOrderDetailsWithStatus("PAID");
    iCreateANewOrder();
  }

  @Given("I have invalid order details with empty items")
  public void iHaveInvalidOrderDetailsWithEmptyItems() {
    currentOrderDTO = OrderDTO.builder()
      .orderDate(LocalDateTime.now())
      .status(OrderStatus.PENDING)
      .totalAmount(BigDecimal.ZERO)
      .customerId(testCustomer.getId())
      .items(List.of())
      .build();
  }

  @Given("I have order details with multiple items:")
  public void iHaveOrderDetailsWithMultipleItems(DataTable dataTable) {
    List<OrderItemDTO> items = new ArrayList<>();
    BigDecimal totalAmount = BigDecimal.ZERO;

    for (Map<String, String> row : dataTable.asMaps()) {
      BigDecimal unitPrice = new BigDecimal(row.get("unitPrice"));
      int quantity = Integer.parseInt(row.get("quantity"));
      BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

      OrderItemDTO item = OrderItemDTO.builder()
        .productName(row.get("productName"))
        .sku(row.get("sku"))
        .quantity(quantity)
        .unitPrice(unitPrice)
        .subtotal(subtotal)
        .build();

      items.add(item);
      totalAmount = totalAmount.add(subtotal);
    }

    currentOrderDTO = OrderDTO.builder()
      .orderDate(LocalDateTime.now())
      .status(OrderStatus.PENDING)
      .totalAmount(totalAmount)
      .customerId(testCustomer.getId())
      .items(items)
      .build();
  }

  @Given("multiple orders exist for the customer")
  public void multipleOrdersExistForTheCustomer() throws Exception {
    for (int i = 0; i < 3; i++) {
      iHaveOrderDetailsWithStatus("PENDING");
      iCreateANewOrder();
    }
  }

  @Given("I have order details with one item:")
  public void iHaveOrderDetailsWithOneItem(DataTable dataTable) {
    Map<String, String> row = dataTable.asMaps().get(0);
    BigDecimal unitPrice = new BigDecimal(row.get("unitPrice"));
    int quantity = Integer.parseInt(row.get("quantity"));

    OrderItemDTO item = OrderItemDTO.builder()
      .productName(row.get("productName"))
      .sku(row.get("sku"))
      .quantity(quantity)
      .unitPrice(unitPrice)
      .build();

    currentOrderDTO = OrderDTO.builder()
      .orderDate(LocalDateTime.now())
      .status(OrderStatus.PENDING)
      .totalAmount(unitPrice.multiply(BigDecimal.valueOf(quantity)))
      .customerId(testCustomer.getId())
      .items(List.of(item))
      .build();
  }

  @Given("I have order details without customer")
  public void iHaveOrderDetailsWithoutCustomer() {
    OrderItemDTO itemDTO = OrderItemDTO.builder()
      .productName("Test Product")
      .sku("TEST-001")
      .quantity(1)
      .unitPrice(BigDecimal.valueOf(10.00))
      .build();

    currentOrderDTO = OrderDTO.builder()
      .orderDate(LocalDateTime.now())
      .status(OrderStatus.PENDING)
      .totalAmount(BigDecimal.valueOf(10.00))
      .customerId(null)
      .items(List.of(itemDTO))
      .build();
  }

  @Given("I have order details with invalid item:")
  public void iHaveOrderDetailsWithInvalidItem(DataTable dataTable) {
    Map<String, String> row = dataTable.asMaps().get(0);

    try {
      BigDecimal unitPrice = new BigDecimal(row.get("unitPrice"));
      int quantity = Integer.parseInt(row.get("quantity"));

      OrderItemDTO item = OrderItemDTO.builder()
        .productName(row.get("productName"))
        .sku(row.get("sku"))
        .quantity(quantity)
        .unitPrice(unitPrice)
        .build();

      currentOrderDTO = OrderDTO.builder()
        .orderDate(LocalDateTime.now())
        .status(OrderStatus.PENDING)
        .totalAmount(unitPrice.multiply(BigDecimal.valueOf(quantity)))
        .customerId(testCustomer.getId())
        .items(List.of(item))
        .build();
    } catch (Exception e) {
      // Invalid data will be caught during validation
    }
  }

  // When Steps

  @When("I create a new order")
  public void iCreateANewOrder() throws Exception {
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
  public void iRequestTheOrderByItsId() throws Exception {
    lastResult = mockMvc.perform(get("/api/orders/" + createdOrder.getId()));

    MvcResult result = lastResult.andReturn();
    if (result.getResponse().getStatus() == 200) {
      createdOrder = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        OrderDTO.class);
    }
  }

  @When("I request all orders")
  public void iRequestAllOrders() throws Exception {
    lastResult = mockMvc.perform(get("/api/orders"));

    MvcResult result = lastResult.andReturn();
    if (result.getResponse().getStatus() == 200) {
      retrievedOrders = List.of(objectMapper.readValue(
        result.getResponse().getContentAsString(),
        OrderDTO[].class));
    }
  }

  @When("I update the order status to {string}")
  public void iUpdateTheOrderStatusTo(String newStatus) throws Exception {
    lastResult = mockMvc.perform(patch("/api/orders/" + createdOrder.getId() + "/status")
      .param("status", newStatus));

    MvcResult result = lastResult.andReturn();
    if (result.getResponse().getStatus() == 200) {
      createdOrder = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        OrderDTO.class);
    }
  }

  @When("I delete the order")
  public void iDeleteTheOrder() throws Exception {
    lastResult = mockMvc.perform(delete("/api/orders/" + createdOrder.getId()));
  }

  @When("I request orders with status {string}")
  public void iRequestOrdersWithStatus(String status) throws Exception {
    lastResult = mockMvc.perform(get("/api/orders")
      .param("status", status));

    MvcResult result = lastResult.andReturn();
    if (result.getResponse().getStatus() == 200) {
      retrievedOrders = List.of(objectMapper.readValue(
        result.getResponse().getContentAsString(),
        OrderDTO[].class));
    }
  }

  @When("I attempt to create the order")
  public void iAttemptToCreateTheOrder() throws Exception {
    try {
      lastResult = mockMvc.perform(post("/api/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(currentOrderDTO)));
    } catch (Exception e) {
      // Exception expected for invalid data
    }
  }

  @When("I attempt to update the order status to {string}")
  public void iAttemptToUpdateTheOrderStatusTo(String newStatus) throws Exception {
    try {
      lastResult = mockMvc.perform(patch("/api/orders/" + createdOrder.getId() + "/status")
        .param("status", newStatus));
    } catch (Exception e) {
      // Exception expected for invalid transition
    }
  }

  @When("I request orders for the customer")
  public void iRequestOrdersForTheCustomer() throws Exception {
    lastResult = mockMvc.perform(get("/api/orders")
      .param("customerId", testCustomer.getId().toString()));

    MvcResult result = lastResult.andReturn();
    if (result.getResponse().getStatus() == 200) {
      retrievedOrders = List.of(objectMapper.readValue(
        result.getResponse().getContentAsString(),
        OrderDTO[].class));
    }
  }

  @When("I update the order with new items:")
  public void iUpdateTheOrderWithNewItems(DataTable dataTable) throws Exception {
    List<OrderItemDTO> items = new ArrayList<>();
    BigDecimal totalAmount = BigDecimal.ZERO;

    for (Map<String, String> row : dataTable.asMaps()) {
      BigDecimal unitPrice = new BigDecimal(row.get("unitPrice"));
      int quantity = Integer.parseInt(row.get("quantity"));
      BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

      OrderItemDTO item = OrderItemDTO.builder()
        .productName(row.get("productName"))
        .sku(row.get("sku"))
        .quantity(quantity)
        .unitPrice(unitPrice)
        .subtotal(subtotal)
        .build();

      items.add(item);
      totalAmount = totalAmount.add(subtotal);
    }

    createdOrder.setItems(items);
    createdOrder.setTotalAmount(totalAmount);

    lastResult = mockMvc.perform(put("/api/orders/" + createdOrder.getId())
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(createdOrder)));

    MvcResult result = lastResult.andReturn();
    if (result.getResponse().getStatus() == 200) {
      createdOrder = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        OrderDTO.class);
    }
  }

  @When("I attempt to delete an order with id {int}")
  public void iAttemptToDeleteAnOrderWithId(int orderId) throws Exception {
    lastResult = mockMvc.perform(delete("/api/orders/" + orderId));
  }

  @When("I attempt to get an order with id {int}")
  public void iAttemptToGetAnOrderWithId(int orderId) throws Exception {
    lastResult = mockMvc.perform(get("/api/orders/" + orderId));
  }

  // Then Steps

  @Then("the order should be created successfully")
  public void theOrderShouldBeCreatedSuccessfully() throws Exception {
    lastResult.andExpect(status().isCreated());
    assertThat(createdOrder).isNotNull();
    assertThat(createdOrder.getId()).isNotNull();
  }

  @Then("the order should have status {string}")
  public void theOrderShouldHaveStatus(String status) {
    assertThat(createdOrder.getStatus()).isEqualTo(OrderStatus.valueOf(status));
  }

  @Then("the order should have a valid order number")
  public void theOrderShouldHaveAValidOrderNumber() {
    assertThat(createdOrder.getOrderNumber()).isNotNull();
    assertThat(createdOrder.getOrderNumber()).matches("^ORD-\\d{8}-\\d{5}$");
  }

  @Then("I should receive the order details")
  public void iShouldReceiveTheOrderDetails() throws Exception {
    lastResult.andExpect(status().isOk());
    assertThat(createdOrder).isNotNull();
  }

  @Then("the order should contain the correct information")
  public void theOrderShouldContainTheCorrectInformation() {
    assertThat(createdOrder.getCustomerId()).isEqualTo(testCustomer.getId());
    assertThat(createdOrder.getOrderNumber()).isNotNull();
  }

  @Then("I should receive a list of all orders")
  public void iShouldReceiveAListOfAllOrders() throws Exception {
    lastResult.andExpect(status().isOk());
    assertThat(retrievedOrders).isNotNull();
  }

  @Then("the list should contain at least {int} orders")
  public void theListShouldContainAtLeastOrders(int count) {
    assertThat(retrievedOrders).hasSizeGreaterThanOrEqualTo(count);
  }

  @Then("the order should be updated successfully")
  public void theOrderShouldBeUpdatedSuccessfully() throws Exception {
    lastResult.andExpect(status().isOk());
  }

  @Then("the order status should be {string}")
  public void theOrderStatusShouldBe(String status) {
    assertThat(createdOrder.getStatus()).isEqualTo(OrderStatus.valueOf(status));
  }

  @Then("the order should be deleted successfully")
  public void theOrderShouldBeDeletedSuccessfully() throws Exception {
    lastResult.andExpect(status().isNoContent());
  }

  @Then("the order should not be found when requested")
  public void theOrderShouldNotBeFoundWhenRequested() throws Exception {
    mockMvc.perform(get("/api/orders/" + createdOrder.getId()))
      .andExpect(status().isNotFound());
  }

  @Then("I should receive only orders with status {string}")
  public void iShouldReceiveOnlyOrdersWithStatus(String status) {
    assertThat(retrievedOrders).isNotEmpty();
    assertThat(retrievedOrders).allMatch(order ->
      order.getStatus().equals(OrderStatus.valueOf(status)));
  }

  @Then("the order creation should fail")
  public void theOrderCreationShouldFail() throws Exception {
    lastResult.andExpect(status().isBadRequest());
  }

  @Then("I should receive a validation error")
  public void iShouldReceiveAValidationError() throws Exception {
    lastResult.andExpect(jsonPath("$.validationErrors").exists());
  }

  @Then("the update should fail")
  public void theUpdateShouldFail() throws Exception {
    lastResult.andExpect(status().isConflict());
  }

  @Then("I should receive an error message")
  public void iShouldReceiveAnErrorMessage() throws Exception {
    lastResult.andExpect(jsonPath("$.message").exists());
  }

  @Then("the order should have {int} items")
  public void theOrderShouldHaveItems(int itemCount) {
    assertThat(createdOrder.getItems()).hasSize(itemCount);
  }

  @Then("the order total should be calculated correctly")
  public void theOrderTotalShouldBeCalculatedCorrectly() {
    BigDecimal calculatedTotal = createdOrder.getItems().stream()
      .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
      .reduce(BigDecimal.ZERO, BigDecimal::add);

    assertThat(createdOrder.getTotalAmount()).isEqualByComparingTo(calculatedTotal);
  }

  @Then("I should receive all orders for that customer")
  public void iShouldReceiveAllOrdersForThatCustomer() throws Exception {
    lastResult.andExpect(status().isOk());
    assertThat(retrievedOrders).isNotEmpty();
  }

  @Then("all orders should belong to the same customer")
  public void allOrdersShouldBelongToTheSameCustomer() {
    assertThat(retrievedOrders).allMatch(order ->
      order.getCustomerId().equals(testCustomer.getId()));
  }

  @Then("the order should have a created timestamp")
  public void theOrderShouldHaveACreatedTimestamp() {
    assertThat(createdOrder.getCreatedAt()).isNotNull();
  }

  @Then("the order should have an updated timestamp")
  public void theOrderShouldHaveAnUpdatedTimestamp() {
    assertThat(createdOrder.getUpdatedAt()).isNotNull();
  }

  @Then("the updated timestamp should be more recent than created timestamp")
  public void theUpdatedTimestampShouldBeMoreRecentThanCreatedTimestamp() {
    assertThat(createdOrder.getUpdatedAt())
      .isAfterOrEqualTo(createdOrder.getCreatedAt());
  }

  @Then("the deletion should fail")
  public void theDeletionShouldFail() throws Exception {
    lastResult.andExpect(status().isNotFound());
  }

  @Then("I should receive a not found error")
  public void iShouldReceiveANotFoundError() throws Exception {
    lastResult.andExpect(status().isNotFound());
  }

  @Then("the order total should be {double}")
  public void theOrderTotalShouldBe(double expectedTotal) {
    assertThat(createdOrder.getTotalAmount())
      .isEqualByComparingTo(BigDecimal.valueOf(expectedTotal));
  }

  @Then("the order number should match pattern {string}")
  public void theOrderNumberShouldMatchPattern(String pattern) {
    assertThat(createdOrder.getOrderNumber()).matches(pattern);
  }

  @Then("I should receive an error message about missing customer")
  public void iShouldReceiveAnErrorMessageAboutMissingCustomer() throws Exception {
    lastResult.andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").exists());
  }

  // And Steps (reusing Then steps)

  @And("the order should have status {string}")
  public void andTheOrderShouldHaveStatus(String status) {
    theOrderShouldHaveStatus(status);
  }

  @And("the order status should be {string}")
  public void andTheOrderStatusShouldBe(String status) {
    theOrderStatusShouldBe(status);
  }
}
