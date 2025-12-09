package com.cjrequena.sample.service;

import com.cjrequena.sample.domain.excepption.CustomertNotFoundException;
import com.cjrequena.sample.domain.excepption.ResourceNotFoundException;
import com.cjrequena.sample.domain.mapper.OrderMapper;
import com.cjrequena.sample.domain.model.aggregate.Order;
import com.cjrequena.sample.domain.model.enums.OrderStatus;
import com.cjrequena.sample.domain.model.vo.Money;
import com.cjrequena.sample.domain.model.vo.OrderNumber;
import com.cjrequena.sample.persistence.entity.CustomerEntity;
import com.cjrequena.sample.persistence.entity.OrderEntity;
import com.cjrequena.sample.persistence.repository.CustomerRepository;
import com.cjrequena.sample.persistence.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Unit Tests")
class OrderServiceImplTest {

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private CustomerRepository customerRepository;

  @Mock
  private OrderMapper orderMapper;

  @InjectMocks
  private OrderServiceImpl orderService;

  private Order testOrder;
  private OrderEntity testOrderEntity;
  private CustomerEntity testCustomer;

  @BeforeEach
  void setUp() {
    testCustomer = CustomerEntity
      .builder()
      .id(1L)
      .firstName("John")
      .lastName("Doe")
      .email("john.doe@example.com")
      .build();

    testOrder = Order
      .builder()
      .id(1L)
      .orderNumber(OrderNumber.of("ORD-20250101-00001"))
      .orderDate(LocalDateTime.now())
      .status(OrderStatus.PENDING)
      .totalAmount(Money.of(100.00))
      .customerId(1L)
      .build();

    testOrderEntity = OrderEntity
      .builder()
      .id(1L)
      .orderNumber("ORD-20250101-00001")
      .orderDate(LocalDateTime.now())
      .status(OrderStatus.PENDING)
      .totalAmount(BigDecimal.valueOf(100.00))
      .customer(testCustomer)
      .build();
  }

  @Test
  @DisplayName("Should create order successfully")
  void testCreateOrder_Success() {
    // Given
    when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
    when(orderMapper.toEntity(any(Order.class))).thenReturn(testOrderEntity);
    when(orderRepository.save(any(OrderEntity.class))).thenReturn(testOrderEntity);
    when(orderMapper.toDomain(any(OrderEntity.class))).thenReturn(testOrder);

    // When
    Order createdOrder = orderService.create(testOrder);

    // Then
    assertThat(createdOrder).isNotNull();
    assertThat(createdOrder.getId()).isEqualTo(1L);
    assertThat(createdOrder.getStatus()).isEqualTo(OrderStatus.PENDING);

    verify(customerRepository).findById(1L);
    verify(orderRepository).save(any(OrderEntity.class));
    verify(orderMapper).toDomain(any(OrderEntity.class));
  }

  @Test
  @DisplayName("Should throw exception when customer not found")
  void testCreateOrder_CustomerNotFound() {
    // Given
    when(customerRepository.findById(1L)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> orderService.create(testOrder))
      .isInstanceOf(CustomertNotFoundException.class)
      .hasMessageContaining("Customer not found with id: 1");

    verify(customerRepository).findById(1L);
    verify(orderRepository, never()).save(any(OrderEntity.class));
  }

  @Test
  @DisplayName("Should get order by id successfully")
  void testGetOrderById_Success() {
    // Given
    when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrderEntity));
    when(orderMapper.toDomain(any(OrderEntity.class))).thenReturn(testOrder);

    // When
    Order foundOrder = orderService.getOrderById(1L);

    // Then
    assertThat(foundOrder).isNotNull();
    assertThat(foundOrder.getId()).isEqualTo(1L);
    assertThat(foundOrder.getOrderNumber().value()).isEqualTo("ORD-20250101-00001");

    verify(orderRepository).findById(1L);
    verify(orderMapper).toDomain(testOrderEntity);
  }

  @Test
  @DisplayName("Should throw exception when order not found by id")
  void testGetOrderById_NotFound() {
    // Given
    when(orderRepository.findById(1L)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> orderService.getOrderById(1L))
      .isInstanceOf(ResourceNotFoundException.class)
      .hasMessageContaining("Order not found with id: 1");

    verify(orderRepository).findById(1L);
  }

  @Test
  @DisplayName("Should get all orders successfully")
  void testGetAllOrders_Success() {
    // Given
    List<OrderEntity> entities = List.of(testOrderEntity);
    when(orderRepository.findAll()).thenReturn(entities);
    when(orderMapper.toDomain(any(OrderEntity.class))).thenReturn(testOrder);

    // When
    List<Order> orders = orderService.getAllOrders();

    // Then
    assertThat(orders).isNotEmpty();
    assertThat(orders).hasSize(1);

    verify(orderRepository).findAll();
  }

  @Test
  @DisplayName("Should update order successfully")
  void testUpdateOrder_Success() {
    // Given
    when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrderEntity));
    when(orderRepository.save(any(OrderEntity.class))).thenReturn(testOrderEntity);
    when(orderMapper.toDomain(any(OrderEntity.class))).thenReturn(testOrder);

    // When
    Order updatedOrder = orderService.updateOrder(1L, testOrder);

    // Then
    assertThat(updatedOrder).isNotNull();
    assertThat(updatedOrder.getId()).isEqualTo(1L);

    verify(orderRepository).findById(1L);
    verify(orderRepository).save(any(OrderEntity.class));
  }

  @Test
  @DisplayName("Should delete order successfully")
  void testDeleteOrder_Success() {
    // Given
    when(orderRepository.existsById(1L)).thenReturn(true);
    doNothing().when(orderRepository).deleteById(1L);

    // When
    orderService.deleteOrder(1L);

    // Then
    verify(orderRepository).existsById(1L);
    verify(orderRepository).deleteById(1L);
  }

  @Test
  @DisplayName("Should throw exception when deleting non-existent order")
  void testDeleteOrder_NotFound() {
    // Given
    when(orderRepository.existsById(1L)).thenReturn(false);

    // When & Then
    assertThatThrownBy(() -> orderService.deleteOrder(1L))
      .isInstanceOf(ResourceNotFoundException.class)
      .hasMessageContaining("Order not found with id: 1");

    verify(orderRepository).existsById(1L);
    verify(orderRepository, never()).deleteById(1L);
  }

  @Test
  @DisplayName("Should get orders by status")
  void testGetOrdersByStatus_Success() {
    // Given
    List<OrderEntity> entities = List.of(testOrderEntity);
    when(orderRepository.findByStatus(OrderStatus.PENDING)).thenReturn(entities);
    when(orderMapper.toDomain(any(OrderEntity.class))).thenReturn(testOrder);

    // When
    List<Order> orders = orderService.getOrdersByStatus(OrderStatus.PENDING);

    // Then
    assertThat(orders).isNotEmpty();
    assertThat(orders).hasSize(1);
    assertThat(orders.get(0).getStatus()).isEqualTo(OrderStatus.PENDING);

    verify(orderRepository).findByStatus(OrderStatus.PENDING);
  }

  @Test
  @DisplayName("Should get orders by customer id")
  void testGetOrdersByCustomerId_Success() {
    // Given
    when(customerRepository.existsById(1L)).thenReturn(true);
    List<OrderEntity> entities = List.of(testOrderEntity);
    when(orderRepository.findByCustomerId(1L)).thenReturn(entities);
    when(orderMapper.toDomain(any(OrderEntity.class))).thenReturn(testOrder);

    // When
    List<Order> orders = orderService.getOrdersByCustomerId(1L);

    // Then
    assertThat(orders).isNotEmpty();
    assertThat(orders).hasSize(1);

    verify(customerRepository).existsById(1L);
    verify(orderRepository).findByCustomerId(1L);
  }

  @Test
  @DisplayName("Should update order status successfully")
  void testUpdateOrderStatus_Success() {
    // Given
    when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrderEntity));
    when(orderMapper.toDomain(testOrderEntity)).thenReturn(testOrder);
    when(orderRepository.save(any(OrderEntity.class))).thenReturn(testOrderEntity);

    // When
    Order updatedOrder = orderService.updateOrderStatus(1L, OrderStatus.PAID);

    // Then
    assertThat(updatedOrder).isNotNull();

    verify(orderRepository).findById(1L);
    verify(orderRepository).save(testOrderEntity);
  }
}
