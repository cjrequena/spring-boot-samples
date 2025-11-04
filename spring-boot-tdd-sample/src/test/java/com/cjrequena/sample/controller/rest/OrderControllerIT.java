package com.cjrequena.sample.controller.rest;

import com.cjrequena.sample.controller.dto.OrderDTO;
import com.cjrequena.sample.domain.model.enums.OrderStatus;
import com.cjrequena.sample.persistence.entity.CustomerEntity;
import com.cjrequena.sample.persistence.jpa.repository.CustomerRepository;
import com.cjrequena.sample.persistence.jpa.repository.OrderRepository;
import com.cjrequena.sample.shared.common.util.Constant;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Order API Integration Tests")
class OrderControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private CustomerRepository customerRepository;

  @Autowired
  private ObjectMapper objectMapper;

  private CustomerEntity testCustomer;

  @BeforeEach
  void setUp() {
    orderRepository.deleteAll();
    customerRepository.deleteAll();
    testCustomer = CustomerEntity
      .builder()
      .firstName("John")
      .lastName("Doe")
      .email("john.doe@example-it.com")
      .phoneNumber("1234567890")
      .build();
    testCustomer = customerRepository.save(testCustomer);
  }

  @Test
  @DisplayName("Should create order and return 201 Created")
  void testCreateOrder_ReturnsCreated() throws Exception {
    // Given
    OrderDTO orderDTO = OrderDTO.builder()
      .orderDate(LocalDateTime.now())
      .status(OrderStatus.PENDING)
      .totalAmount(BigDecimal.valueOf(1500.00))
      .customerId(testCustomer.getId())
      .build();

    // When & Then
    mockMvc.perform(post("/api/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Accept-Version", Constant.VND_SAMPLE_SERVICE_V1)
        .content(objectMapper.writeValueAsString(orderDTO)))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").exists())
      .andExpect(jsonPath("$.order_number").exists())
      .andExpect(jsonPath("$.status").value("PENDING"))
      .andExpect(jsonPath("$.customer_id").value(testCustomer.getId()));
  }

  @Test
  @DisplayName("Should get order by id and return 200 OK")
  void testGetOrderById_ReturnsOk() throws Exception {
    // Given - Create order first
    OrderDTO orderDTO = OrderDTO.builder()
      .orderDate(LocalDateTime.now())
      .status(OrderStatus.PENDING)
      .totalAmount(BigDecimal.valueOf(50.00))
      .customerId(testCustomer.getId())
      .build();

    String createResponse = mockMvc.perform(post("/api/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Accept-Version", Constant.VND_SAMPLE_SERVICE_V1)
        .content(objectMapper.writeValueAsString(orderDTO)))
      .andExpect(status().isCreated())
      .andReturn().getResponse().getContentAsString();

    OrderDTO createdOrder = objectMapper.readValue(createResponse, OrderDTO.class);

    // When & Then
    mockMvc.perform(
        get("/api/orders/{id}", createdOrder.getId())
          .accept(MediaType.APPLICATION_JSON)
          .header("Accept-Version", Constant.VND_SAMPLE_SERVICE_V1)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(createdOrder.getId()))
      .andExpect(jsonPath("$.order_number").value(createdOrder.getOrderNumber()));
  }

  @Test
  @DisplayName("Should return 404 when order not found")
  void testGetOrderById_NotFound() throws Exception {
    // When & Then
    mockMvc.perform(
        get("/api/orders/99999")
          .accept(MediaType.APPLICATION_JSON)
          .header("Accept-Version", Constant.VND_SAMPLE_SERVICE_V1)
      )
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.message").value(containsString("Order not found")));
  }

  @Test
  @DisplayName("Should get all orders and return 200 OK")
  void testGetAllOrders_ReturnsOk() throws Exception {
    // Given - Create multiple orders
    OrderDTO order1 = OrderDTO.builder()
      .orderDate(LocalDateTime.now())
      .status(OrderStatus.PENDING)
      .totalAmount(BigDecimal.valueOf(100.00))
      .customerId(testCustomer.getId())
      .build();

    mockMvc.perform(
        post("/api/orders")
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
          .header("Accept-Version", Constant.VND_SAMPLE_SERVICE_V1)
          .content(objectMapper.writeValueAsString(order1))
      )
      .andExpect(status().isCreated());

    // When & Then
    mockMvc.perform(
        get("/api/orders")
          .accept(MediaType.APPLICATION_JSON)
          .header("Accept-Version", Constant.VND_SAMPLE_SERVICE_V1)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
  }

  @Test
  @DisplayName("Should update order and return 200 OK")
  void testUpdateOrder_ReturnsOk() throws Exception {
    // Given - Create order first
    OrderDTO orderDTO = OrderDTO.builder()
      .orderDate(LocalDateTime.now())
      .status(OrderStatus.PENDING)
      .totalAmount(BigDecimal.valueOf(75.00))
      .customerId(testCustomer.getId())
      .build();

    String createResponse = mockMvc.perform(post("/api/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .header("Accept-Version", Constant.VND_SAMPLE_SERVICE_V1)
        .content(objectMapper.writeValueAsString(orderDTO))
      )
      .andExpect(status().isCreated())
      .andReturn().getResponse().getContentAsString();

    OrderDTO createdOrder = objectMapper.readValue(createResponse, OrderDTO.class);

    // Update the order
    createdOrder.setStatus(OrderStatus.PAID);
    createdOrder.setTotalAmount(BigDecimal.valueOf(100.00));

    // When & Then
    mockMvc.perform(
        put("/api/orders/{id}", createdOrder.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
          .header("Accept-Version", Constant.VND_SAMPLE_SERVICE_V1)
          .content(objectMapper.writeValueAsString(createdOrder))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status").value("PAID"));
  }

  @Test
  @DisplayName("Should delete order and return 204 No Content")
  void testDeleteOrder_ReturnsNoContent() throws Exception {
    // Given - Create order first
    OrderDTO orderDTO = OrderDTO.builder()
      .orderDate(LocalDateTime.now())
      .status(OrderStatus.PENDING)
      .totalAmount(BigDecimal.valueOf(300.00))
      .customerId(testCustomer.getId())
      .build();

    String createResponse = mockMvc.perform(post("/api/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .header("Accept-Version", Constant.VND_SAMPLE_SERVICE_V1)
        .content(objectMapper.writeValueAsString(orderDTO))
      )
      .andExpect(status().isCreated())
      .andReturn().getResponse().getContentAsString();

    OrderDTO createdOrder = objectMapper.readValue(createResponse, OrderDTO.class);

    // When & Then - Delete
    mockMvc.perform(
        delete("/api/orders/{id}", createdOrder.getId())
          .accept(MediaType.APPLICATION_JSON)
          .header("Accept-Version", Constant.VND_SAMPLE_SERVICE_V1)
      )
      .andExpect(status().isNoContent());

    // Verify deletion
    mockMvc.perform(
        get("/api/orders/{id}", createdOrder.getId())
          .accept(MediaType.APPLICATION_JSON)
          .header("Accept-Version", Constant.VND_SAMPLE_SERVICE_V1)
      )
      .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should update order status and return 200 OK")
  void testUpdateOrderStatus_ReturnsOk() throws Exception {
    // Given - Create order first
    OrderDTO orderDTO = OrderDTO.builder()
      .orderDate(LocalDateTime.now())
      .status(OrderStatus.PENDING)
      .totalAmount(BigDecimal.valueOf(50.00))
      .customerId(testCustomer.getId())
      .build();

    String createResponse = mockMvc.perform(post("/api/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .header("Accept-Version", Constant.VND_SAMPLE_SERVICE_V1)
        .content(objectMapper.writeValueAsString(orderDTO))
      )
      .andExpect(status().isCreated())
      .andReturn().getResponse().getContentAsString();

    OrderDTO createdOrder = objectMapper.readValue(createResponse, OrderDTO.class);

    // When & Then
    mockMvc.perform(
        patch("/api/orders/{id}/status", createdOrder.getId())
          .contentType("application/json-patch+json")
          .accept(MediaType.APPLICATION_JSON)
          .header("Accept-Version", Constant.VND_SAMPLE_SERVICE_V1)
          .param("status", "SHIPPED")
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status").value("SHIPPED"));
  }

  @Test
  @DisplayName("Should get orders by status and return 200 OK")
  void testGetOrdersByStatus_ReturnsOk() throws Exception {
    // Given - Create orders with different statuses
    OrderDTO pendingOrder = OrderDTO.builder()
      .orderDate(LocalDateTime.now())
      .status(OrderStatus.PENDING)
      .totalAmount(BigDecimal.valueOf(50.00))
      .customerId(testCustomer.getId())
      .build();

    mockMvc.perform(
        post("/api/orders")
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
          .header("Accept-Version", Constant.VND_SAMPLE_SERVICE_V1)
          .content(objectMapper.writeValueAsString(pendingOrder))
      )
      .andExpect(status().isCreated());

    // When & Then
    mockMvc.perform(
      get("/api/orders")
        .accept(MediaType.APPLICATION_JSON)
        .header("Accept-Version", Constant.VND_SAMPLE_SERVICE_V1)
        .param("status", "PENDING")
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
      .andExpect(jsonPath("$[0].status").value("PENDING"));
  }

  @Test
  @DisplayName("Should get orders by customer id and return 200 OK")
  void testGetOrdersByCustomerId_ReturnsOk() throws Exception {
    // Given - Create order
    OrderDTO orderDTO = OrderDTO.builder()
      .orderDate(LocalDateTime.now())
      .status(OrderStatus.PENDING)
      .totalAmount(BigDecimal.valueOf(50.00))
      .customerId(testCustomer.getId())
      .build();

    mockMvc.perform(
        post("/api/orders")
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
          .header("Accept-Version", Constant.VND_SAMPLE_SERVICE_V1)
          .content(objectMapper.writeValueAsString(orderDTO))
      )
      .andExpect(status().isCreated());

    // When & Then
    mockMvc.perform(
        get("/api/orders")
          .accept(MediaType.APPLICATION_JSON)
          .header("Accept-Version", Constant.VND_SAMPLE_SERVICE_V1)
          .param("customerId", testCustomer.getId().toString())
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
      .andExpect(jsonPath("$[0].customer_id").value(testCustomer.getId()));
  }

  @Test
  @DisplayName("Should return 400 for invalid order data")
  void testCreateOrder_ValidationError() throws Exception {
    // Given - Invalid order (missing required fields)
    OrderDTO invalidOrder = OrderDTO
      .builder()
      .customerId(testCustomer.getId())
      .build();

    // When & Then
    mockMvc.perform(
        post("/api/orders")
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
          .header("Accept-Version", Constant.VND_SAMPLE_SERVICE_V1)
          .content(objectMapper.writeValueAsString(invalidOrder))
      )
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.validation_errors").isArray());
  }

  @Test
  @DisplayName("Should handle multiple order status transitions")
  void testMultipleStatusTransitions() throws Exception {
    // Given - Create order
    OrderDTO orderDTO = OrderDTO.builder()
      .orderDate(LocalDateTime.now())
      .status(OrderStatus.PENDING)
      .totalAmount(BigDecimal.valueOf(500.00))
      .customerId(testCustomer.getId())
      .build();

    String createResponse = mockMvc
      .perform(
        post("/api/orders")
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
          .header("Accept-Version", Constant.VND_SAMPLE_SERVICE_V1)
          .content(objectMapper.writeValueAsString(orderDTO))
      )
      .andExpect(status().isCreated())
      .andReturn().getResponse().getContentAsString();

    OrderDTO createdOrder = objectMapper.readValue(createResponse, OrderDTO.class);

    // Transition: PENDING -> PAID
    mockMvc
      .perform(
        patch("/api/orders/{id}/status", createdOrder.getId())
          .contentType("application/json-patch+json")
          .accept(MediaType.APPLICATION_JSON)
          .header("Accept-Version", Constant.VND_SAMPLE_SERVICE_V1)
        .param("status", "PAID")
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status").value("PAID"));

    // Transition: PAID -> SHIPPED
    mockMvc
      .perform(
        patch("/api/orders/{id}/status", createdOrder.getId())
          .contentType("application/json-patch+json")
          .accept(MediaType.APPLICATION_JSON)
          .header("Accept-Version", Constant.VND_SAMPLE_SERVICE_V1)
        .param("status", "SHIPPED")
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status").value("SHIPPED"));

    // Transition: SHIPPED -> DELIVERED
    mockMvc
      .perform(
        patch("/api/orders/{id}/status", createdOrder.getId())
          .contentType("application/json-patch+json")
          .accept(MediaType.APPLICATION_JSON)
          .header("Accept-Version", Constant.VND_SAMPLE_SERVICE_V1)
        .param("status", "DELIVERED")
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status").value("DELIVERED"));
  }

  @Test
  @DisplayName("Should return 404 when updating non-existent order")
  void testUpdateOrder_NotFound() throws Exception {
    // Given
    OrderDTO orderDTO = OrderDTO.builder()
      .orderNumber("ORD-20250101-99999")
      .orderDate(LocalDateTime.now())
      .status(OrderStatus.PENDING)
      .totalAmount(BigDecimal.valueOf(100.00))
      .customerId(testCustomer.getId())
      .build();

    // When & Then
    mockMvc.perform(
        put("/api/orders/99999")
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
          .header("Accept-Version", Constant.VND_SAMPLE_SERVICE_V1)
          .content(objectMapper.writeValueAsString(orderDTO))
      )
      .andExpect(status().isNotFound());
  }
}
