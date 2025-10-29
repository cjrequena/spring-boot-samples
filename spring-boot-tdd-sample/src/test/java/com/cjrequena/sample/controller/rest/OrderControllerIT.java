package com.cjrequena.sample.controller.rest;

import com.cjrequena.sample.controller.dto.OrderDTO;
import com.cjrequena.sample.controller.dto.OrderItemDTO;
import com.cjrequena.sample.domain.model.enums.OrderStatus;
import com.cjrequena.sample.persistence.entity.CustomerEntity;
import com.cjrequena.sample.persistence.jpa.repository.CustomerRepository;
import com.cjrequena.sample.persistence.jpa.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    private ObjectMapper objectMapper;
    private CustomerEntity testCustomer;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        orderRepository.deleteAll();
        customerRepository.deleteAll();

        testCustomer = CustomerEntity.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("1234567890")
                .build();
        testCustomer = customerRepository.save(testCustomer);
    }

    @Test
    @DisplayName("Should create order and return 201 Created")
    void testCreateOrder_ReturnsCreated() throws Exception {
        // Given
        OrderItemDTO itemDTO = OrderItemDTO.builder()
                .productName("Laptop")
                .sku("LAPTOP-001")
                .quantity(1)
                .unitPrice(BigDecimal.valueOf(1500.00))
                .build();

        OrderDTO orderDTO = OrderDTO.builder()
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.valueOf(1500.00))
                .customerId(testCustomer.getId())
                .items(List.of(itemDTO))
                .build();

        // When & Then
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.orderNumber").exists())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.customerId").value(testCustomer.getId()))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].productName").value("Laptop"));
    }

//    @Test
//    @DisplayName("Should get order by id and return 200 OK")
//    void testGetOrderById_ReturnsOk() throws Exception {
//        // Given - Create order first
//        OrderItemDTO itemDTO = OrderItemDTO.builder()
//                .productName("Mouse")
//                .sku("MOUSE-001")
//                .quantity(2)
//                .unitPrice(BigDecimal.valueOf(25.00))
//                .build();
//
//        OrderDTO orderDTO = OrderDTO.builder()
//                .orderDate(LocalDateTime.now())
//                .status(OrderStatus.PENDING)
//                .totalAmount(BigDecimal.valueOf(50.00))
//                .customerId(testCustomer.getId())
//                .items(List.of(itemDTO))
//                .build();
//
//        String createResponse = mockMvc.perform(post("/api/orders")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(orderDTO)))
//                .andExpect(status().isCreated())
//                .andReturn().getResponse().getContentAsString();
//
//        OrderDTO createdOrder = objectMapper.readValue(createResponse, OrderDTO.class);
//
//        // When & Then
//        mockMvc.perform(get("/api/orders/{id}", createdOrder.getId()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(createdOrder.getId()))
//                .andExpect(jsonPath("$.orderNumber").value(createdOrder.getOrderNumber()))
//                .andExpect(jsonPath("$.items", hasSize(1)));
//    }
//
//    @Test
//    @DisplayName("Should return 404 when order not found")
//    void testGetOrderById_NotFound() throws Exception {
//        // When & Then
//        mockMvc.perform(get("/api/orders/99999"))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.message").value(containsString("Order not found")));
//    }
//
//    @Test
//    @DisplayName("Should get all orders and return 200 OK")
//    void testGetAllOrders_ReturnsOk() throws Exception {
//        // Given - Create multiple orders
//        OrderItemDTO item1 = OrderItemDTO.builder()
//                .productName("Product 1")
//                .sku("PROD-001")
//                .quantity(1)
//                .unitPrice(BigDecimal.valueOf(100.00))
//                .build();
//
//        OrderDTO order1 = OrderDTO.builder()
//                .orderDate(LocalDateTime.now())
//                .status(OrderStatus.PENDING)
//                .totalAmount(BigDecimal.valueOf(100.00))
//                .customerId(testCustomer.getId())
//                .items(List.of(item1))
//                .build();
//
//        mockMvc.perform(post("/api/orders")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(order1)))
//                .andExpect(status().isCreated());
//
//        // When & Then
//        mockMvc.perform(get("/api/orders"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
//    }
//
//    @Test
//    @DisplayName("Should update order and return 200 OK")
//    void testUpdateOrder_ReturnsOk() throws Exception {
//        // Given - Create order first
//        OrderItemDTO itemDTO = OrderItemDTO.builder()
//                .productName("Keyboard")
//                .sku("KB-001")
//                .quantity(1)
//                .unitPrice(BigDecimal.valueOf(75.00))
//                .build();
//
//        OrderDTO orderDTO = OrderDTO.builder()
//                .orderDate(LocalDateTime.now())
//                .status(OrderStatus.PENDING)
//                .totalAmount(BigDecimal.valueOf(75.00))
//                .customerId(testCustomer.getId())
//                .items(List.of(itemDTO))
//                .build();
//
//        String createResponse = mockMvc.perform(post("/api/orders")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(orderDTO)))
//                .andExpect(status().isCreated())
//                .andReturn().getResponse().getContentAsString();
//
//        OrderDTO createdOrder = objectMapper.readValue(createResponse, OrderDTO.class);
//
//        // Update the order
//        createdOrder.setStatus(OrderStatus.PAID);
//        OrderItemDTO updatedItem = OrderItemDTO.builder()
//                .productName("Keyboard Premium")
//                .sku("KB-002")
//                .quantity(1)
//                .unitPrice(BigDecimal.valueOf(100.00))
//                .build();
//        createdOrder.setItems(List.of(updatedItem));
//        createdOrder.setTotalAmount(BigDecimal.valueOf(100.00));
//
//        // When & Then
//        mockMvc.perform(put("/api/orders/{id}", createdOrder.getId())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(createdOrder)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value("PAID"))
//                .andExpect(jsonPath("$.items[0].productName").value("Keyboard Premium"));
//    }
//
//    @Test
//    @DisplayName("Should delete order and return 204 No Content")
//    void testDeleteOrder_ReturnsNoContent() throws Exception {
//        // Given - Create order first
//        OrderItemDTO itemDTO = OrderItemDTO.builder()
//                .productName("Monitor")
//                .sku("MON-001")
//                .quantity(1)
//                .unitPrice(BigDecimal.valueOf(300.00))
//                .build();
//
//        OrderDTO orderDTO = OrderDTO.builder()
//                .orderDate(LocalDateTime.now())
//                .status(OrderStatus.PENDING)
//                .totalAmount(BigDecimal.valueOf(300.00))
//                .customerId(testCustomer.getId())
//                .items(List.of(itemDTO))
//                .build();
//
//        String createResponse = mockMvc.perform(post("/api/orders")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(orderDTO)))
//                .andExpect(status().isCreated())
//                .andReturn().getResponse().getContentAsString();
//
//        OrderDTO createdOrder = objectMapper.readValue(createResponse, OrderDTO.class);
//
//        // When & Then - Delete
//        mockMvc.perform(delete("/api/orders/{id}", createdOrder.getId()))
//                .andExpect(status().isNoContent());
//
//        // Verify deletion
//        mockMvc.perform(get("/api/orders/{id}", createdOrder.getId()))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    @DisplayName("Should update order status and return 200 OK")
//    void testUpdateOrderStatus_ReturnsOk() throws Exception {
//        // Given - Create order first
//        OrderItemDTO itemDTO = OrderItemDTO.builder()
//                .productName("Headphones")
//                .sku("HP-001")
//                .quantity(1)
//                .unitPrice(BigDecimal.valueOf(50.00))
//                .build();
//
//        OrderDTO orderDTO = OrderDTO.builder()
//                .orderDate(LocalDateTime.now())
//                .status(OrderStatus.PENDING)
//                .totalAmount(BigDecimal.valueOf(50.00))
//                .customerId(testCustomer.getId())
//                .items(List.of(itemDTO))
//                .build();
//
//        String createResponse = mockMvc.perform(post("/api/orders")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(orderDTO)))
//                .andExpect(status().isCreated())
//                .andReturn().getResponse().getContentAsString();
//
//        OrderDTO createdOrder = objectMapper.readValue(createResponse, OrderDTO.class);
//
//        // When & Then
//        mockMvc.perform(patch("/api/orders/{id}/status", createdOrder.getId())
//                        .param("status", "SHIPPED"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value("SHIPPED"));
//    }
//
//    @Test
//    @DisplayName("Should get orders by status and return 200 OK")
//    void testGetOrdersByStatus_ReturnsOk() throws Exception {
//        // Given - Create orders with different statuses
//        OrderItemDTO itemDTO = OrderItemDTO.builder()
//                .productName("Test Product")
//                .sku("TEST-001")
//                .quantity(1)
//                .unitPrice(BigDecimal.valueOf(50.00))
//                .build();
//
//        OrderDTO pendingOrder = OrderDTO.builder()
//                .orderDate(LocalDateTime.now())
//                .status(OrderStatus.PENDING)
//                .totalAmount(BigDecimal.valueOf(50.00))
//                .customerId(testCustomer.getId())
//                .items(List.of(itemDTO))
//                .build();
//
//        mockMvc.perform(post("/api/orders")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(pendingOrder)))
//                .andExpect(status().isCreated());
//
//        // When & Then
//        mockMvc.perform(get("/api/orders")
//                        .param("status", "PENDING"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
//                .andExpect(jsonPath("$[0].status").value("PENDING"));
//    }
//
//    @Test
//    @DisplayName("Should get orders by customer id and return 200 OK")
//    void testGetOrdersByCustomerId_ReturnsOk() throws Exception {
//        // Given - Create order
//        OrderItemDTO itemDTO = OrderItemDTO.builder()
//                .productName("Test Product")
//                .sku("TEST-001")
//                .quantity(1)
//                .unitPrice(BigDecimal.valueOf(50.00))
//                .build();
//
//        OrderDTO orderDTO = OrderDTO.builder()
//                .orderDate(LocalDateTime.now())
//                .status(OrderStatus.PENDING)
//                .totalAmount(BigDecimal.valueOf(50.00))
//                .customerId(testCustomer.getId())
//                .items(List.of(itemDTO))
//                .build();
//
//        mockMvc.perform(post("/api/orders")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(orderDTO)))
//                .andExpect(status().isCreated());
//
//        // When & Then
//        mockMvc.perform(get("/api/orders")
//                        .param("customerId", testCustomer.getId().toString()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
//                .andExpect(jsonPath("$[0].customerId").value(testCustomer.getId()));
//    }
//
//    @Test
//    @DisplayName("Should return 400 for invalid order data")
//    void testCreateOrder_ValidationError() throws Exception {
//        // Given - Invalid order (missing required fields)
//        OrderDTO invalidOrder = OrderDTO.builder()
//                .customerId(testCustomer.getId())
//                .items(List.of()) // Empty items
//                .build();
//
//        // When & Then
//        mockMvc.perform(post("/api/orders")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidOrder)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.validationErrors").isArray());
//    }
//
//    @Test
//    @DisplayName("Should handle multiple order status transitions")
//    void testMultipleStatusTransitions() throws Exception {
//        // Given - Create order
//        OrderItemDTO itemDTO = OrderItemDTO.builder()
//                .productName("Tablet")
//                .sku("TAB-001")
//                .quantity(1)
//                .unitPrice(BigDecimal.valueOf(500.00))
//                .build();
//
//        OrderDTO orderDTO = OrderDTO.builder()
//                .orderDate(LocalDateTime.now())
//                .status(OrderStatus.PENDING)
//                .totalAmount(BigDecimal.valueOf(500.00))
//                .customerId(testCustomer.getId())
//                .items(List.of(itemDTO))
//                .build();
//
//        String createResponse = mockMvc.perform(post("/api/orders")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(orderDTO)))
//                .andExpect(status().isCreated())
//                .andReturn().getResponse().getContentAsString();
//
//        OrderDTO createdOrder = objectMapper.readValue(createResponse, OrderDTO.class);
//
//        // Transition: PENDING -> PAID
//        mockMvc.perform(patch("/api/orders/{id}/status", createdOrder.getId())
//                        .param("status", "PAID"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value("PAID"));
//
//        // Transition: PAID -> SHIPPED
//        mockMvc.perform(patch("/api/orders/{id}/status", createdOrder.getId())
//                        .param("status", "SHIPPED"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value("SHIPPED"));
//
//        // Transition: SHIPPED -> DELIVERED
//        mockMvc.perform(patch("/api/orders/{id}/status", createdOrder.getId())
//                        .param("status", "DELIVERED"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value("DELIVERED"));
//    }
//
//    @Test
//    @DisplayName("Should return 404 when updating non-existent order")
//    void testUpdateOrder_NotFound() throws Exception {
//        // Given
//        OrderItemDTO itemDTO = OrderItemDTO.builder()
//                .productName("Product")
//                .sku("PROD-001")
//                .quantity(1)
//                .unitPrice(BigDecimal.valueOf(100.00))
//                .build();
//
//        OrderDTO orderDTO = OrderDTO.builder()
//                .orderNumber("ORD-20250101-99999")
//                .orderDate(LocalDateTime.now())
//                .status(OrderStatus.PENDING)
//                .totalAmount(BigDecimal.valueOf(100.00))
//                .customerId(testCustomer.getId())
//                .items(List.of(itemDTO))
//                .build();
//
//        // When & Then
//        mockMvc.perform(put("/api/orders/99999")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(orderDTO)))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    @DisplayName("Should create order with multiple items")
//    void testCreateOrder_MultipleItems() throws Exception {
//        // Given
//        OrderItemDTO item1 = OrderItemDTO.builder()
//                .productName("Mouse")
//                .sku("MOUSE-001")
//                .quantity(2)
//                .unitPrice(BigDecimal.valueOf(25.00))
//                .build();
//
//        OrderItemDTO item2 = OrderItemDTO.builder()
//                .productName("Keyboard")
//                .sku("KB-001")
//                .quantity(1)
//                .unitPrice(BigDecimal.valueOf(75.00))
//                .build();
//
//        OrderItemDTO item3 = OrderItemDTO.builder()
//                .productName("Monitor")
//                .sku("MON-001")
//                .quantity(1)
//                .unitPrice(BigDecimal.valueOf(300.00))
//                .build();
//
//        OrderDTO orderDTO = OrderDTO.builder()
//                .orderDate(LocalDateTime.now())
//                .status(OrderStatus.PENDING)
//                .totalAmount(BigDecimal.valueOf(425.00))
//                .customerId(testCustomer.getId())
//                .items(List.of(item1, item2, item3))
//                .build();
//
//        // When & Then
//        mockMvc.perform(post("/api/orders")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(orderDTO)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.items", hasSize(3)))
//                .andExpect(jsonPath("$.items[0].productName").value("Mouse"))
//                .andExpect(jsonPath("$.items[1].productName").value("Keyboard"))
//                .andExpect(jsonPath("$.items[2].productName").value("Monitor"));
//    }
}
