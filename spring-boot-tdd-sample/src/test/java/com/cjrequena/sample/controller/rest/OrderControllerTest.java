package com.cjrequena.sample.controller.rest;

import com.cjrequena.sample.controller.dto.OrderDTO;
import com.cjrequena.sample.controller.excepption.GlobalExceptionHandler;
import com.cjrequena.sample.domain.mapper.OrderMapper;
import com.cjrequena.sample.domain.model.aggregate.Order;
import com.cjrequena.sample.domain.model.enums.OrderStatus;
import com.cjrequena.sample.domain.model.vo.Money;
import com.cjrequena.sample.domain.model.vo.OrderNumber;
import com.cjrequena.sample.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@ContextConfiguration(classes = {OrderController.class, GlobalExceptionHandler.class})
@DisplayName("OrderController Unit Tests")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private OrderMapper orderMapper;

    private ObjectMapper objectMapper;
    private OrderDTO testOrderDTO;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testOrderDTO = OrderDTO.builder()
                .id(1L)
                .orderNumber("ORD-20250101-00001")
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.valueOf(100.00))
                .customerId(1L)
                .build();


        testOrder = Order.builder()
                .id(1L)
                .orderNumber(OrderNumber.of("ORD-20250101-00001"))
                .orderDate(testOrderDTO.getOrderDate())
                .status(OrderStatus.PENDING)
                .totalAmount(Money.of(100.00))
                .customerId(1L)
                .build();
    }

    @Test
    @DisplayName("POST /api/orders - Should create order successfully")
    void testCreateOrder_Success() throws Exception {
        // Given
        when(orderMapper.toDomainFromDTO(any(OrderDTO.class))).thenReturn(testOrder);
        when(orderService.createOrder(any(Order.class))).thenReturn(testOrder);
        when(orderMapper.toDTO(any(Order.class))).thenReturn(testOrderDTO);

        // When & Then
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testOrderDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderNumber").value("ORD-20250101-00001"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("POST /api/orders - Should return 400 for invalid order")
    void testCreateOrder_ValidationFailed() throws Exception {
        // Given
        OrderDTO invalidOrder = OrderDTO.builder()
                .customerId(1L)
                .build();

        // When & Then
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidOrder)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/orders/{id} - Should get order by id")
    void testGetOrderById_Success() throws Exception {
        // Given
        when(orderService.getOrderById(1L)).thenReturn(testOrder);
        when(orderMapper.toDTO(any(Order.class))).thenReturn(testOrderDTO);

        // When & Then
        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderNumber").value("ORD-20250101-00001"));
    }

    @Test
    @DisplayName("GET /api/orders - Should get all orders")
    void testGetAllOrders_Success() throws Exception {
        // Given
        when(orderService.getAllOrders()).thenReturn(List.of(testOrder));
        when(orderMapper.toDTOList(any())).thenReturn(List.of(testOrderDTO));

        // When & Then
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].orderNumber").value("ORD-20250101-00001"));
    }

    @Test
    @DisplayName("GET /api/orders?status=PENDING - Should get orders by status")
    void testGetOrdersByStatus_Success() throws Exception {
        // Given
        when(orderService.getOrdersByStatus(OrderStatus.PENDING)).thenReturn(List.of(testOrder));
        when(orderMapper.toDTOList(any())).thenReturn(List.of(testOrderDTO));

        // When & Then
        mockMvc.perform(get("/api/orders")
                        .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    @DisplayName("PUT /api/orders/{id} - Should update order")
    void testUpdateOrder_Success() throws Exception {
        // Given
        when(orderMapper.toDomainFromDTO(any(OrderDTO.class))).thenReturn(testOrder);
        when(orderService.updateOrder(eq(1L), any(Order.class))).thenReturn(testOrder);
        when(orderMapper.toDTO(any(Order.class))).thenReturn(testOrderDTO);

        // When & Then
        mockMvc.perform(put("/api/orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testOrderDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("DELETE /api/orders/{id} - Should delete order")
    void testDeleteOrder_Success() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/orders/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("PATCH /api/orders/{id}/status - Should update order status")
    void testUpdateOrderStatus_Success() throws Exception {
        // Given
        Order updatedOrder = Order.builder()
                .id(1L)
                .orderNumber(testOrder.getOrderNumber())
                .orderDate(testOrder.getOrderDate())
                .status(OrderStatus.PAID)
                .totalAmount(testOrder.getTotalAmount())
                .customerId(testOrder.getCustomerId())
                .build();

        OrderDTO updatedDTO = OrderDTO.builder()
                .id(1L)
                .orderNumber(testOrderDTO.getOrderNumber())
                .orderDate(testOrderDTO.getOrderDate())
                .status(OrderStatus.PAID)
                .totalAmount(testOrderDTO.getTotalAmount())
                .customerId(testOrderDTO.getCustomerId())
                .build();

        when(orderService.updateOrderStatus(1L, OrderStatus.PAID)).thenReturn(updatedOrder);
        when(orderMapper.toDTO(updatedOrder)).thenReturn(updatedDTO);

        // When & Then
        mockMvc.perform(patch("/api/orders/1/status")
                        .param("status", "PAID"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));
    }
}
