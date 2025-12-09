package com.cjrequena.sample.service;

import com.cjrequena.sample.domain.excepption.CustomertNotFoundException;
import com.cjrequena.sample.domain.excepption.ResourceNotFoundException;
import com.cjrequena.sample.domain.mapper.OrderMapper;
import com.cjrequena.sample.domain.model.aggregate.Order;
import com.cjrequena.sample.domain.model.enums.OrderStatus;
import com.cjrequena.sample.domain.model.vo.OrderNumber;
import com.cjrequena.sample.persistence.entity.CustomerEntity;
import com.cjrequena.sample.persistence.entity.OrderEntity;
import com.cjrequena.sample.persistence.repository.CustomerRepository;
import com.cjrequena.sample.persistence.repository.OrderRepository;
import com.cjrequena.sample.shared.common.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final CustomerRepository customerRepository;
  private final OrderMapper orderMapper;

  @Override
  public Order create(Order order) {
    log.debug("Creating new order: {}", order);

    final long customerId = order.getCustomerId();

    // Validate customerEntity exists
    CustomerEntity customerEntity = customerRepository
      .findById(order.getCustomerId())
      .orElseThrow(() -> new CustomertNotFoundException("Customer not found with id: " + customerId));

    // Generate order number if not provided
    if (order.getOrderNumber() == null) {
      order = Order.builder()
        .orderNumber(OrderNumber.generate())
        .orderDate(order.getOrderDate())
        .status(order.getStatus())
        .totalAmount(order.getTotalAmount())
        .customerId(order.getCustomerId())
        .build();
    }

    OrderEntity orderEntity = orderMapper.toEntity(order);
    orderEntity.setCustomer(customerEntity);

    OrderEntity savedOrderEntity = orderRepository.save(orderEntity);
    log.info("Order created successfully with id: {}", savedOrderEntity.getId());
    return orderMapper.toDomain(savedOrderEntity);
  }

  @Override
  @Transactional(readOnly = true)
  public Order getOrderById(Long id) {
    log.debug("Fetching order with id: {}", id);
    OrderEntity orderEntity = orderRepository
      .findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: %d".formatted(id)));
    return orderMapper.toDomain(orderEntity);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Order> getAllOrders() {
    log.debug("Fetching all orders");
    return orderRepository
      .findAll()
      .stream()
      .map(orderMapper::toDomain)
      .collect(Collectors.toList());
  }

  @Override
  public Order updateOrder(Long id, Order order) {
    log.debug("Updating order with id: {}", id);

    OrderEntity existingOrderEntity = orderRepository
      .findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

    // Validate customer if changed
    if (!existingOrderEntity.getCustomer().getId().equals(order.getCustomerId())) {
      CustomerEntity customerEntity = customerRepository
        .findById(order.getCustomerId())
        .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + order.getCustomerId()));
      existingOrderEntity.setCustomer(customerEntity);
    }

    // Update fields
    existingOrderEntity.setOrderNumber(order.getOrderNumber().value());
    existingOrderEntity.setOrderDate(order.getOrderDate());
    existingOrderEntity.setStatus(order.getStatus());

    OrderEntity updatedEntity = orderRepository.save(existingOrderEntity);
    log.info("Order updated successfully with id: {}", id);
    return orderMapper.toDomain(updatedEntity);
  }

  @Override
  public Order patchOrder(Long id, JsonPatch patch) {
    log.debug("Patching order with id: {}", id);

    try {
      Order existingOrder = getOrderById(id);

      // Convert order to JSON
      //JsonNode patched = patch.apply(objectMapper.convertValue(existingOrder, JsonNode.class));
      JsonNode patched = patch.apply(JsonUtil.objectToJsonNode(existingOrder));

      // Convert back to Order
      //Order patchedOrder = objectMapper.treeToValue(patched, Order.class);
      Order patchedOrder = JsonUtil.jsonNodeToObject(patched, Order.class);

      // Update the order
      return updateOrder(id, patchedOrder);

    } catch (JsonPatchException | JsonProcessingException e) {
      log.error("Error applying patch to order: {}", id, e);
      throw new IllegalArgumentException("Invalid patch operation: " + e.getMessage());
    }
  }

  @Override
  public void deleteOrder(Long id) {
    log.debug("Deleting order with id: {}", id);

    if (!orderRepository.existsById(id)) {
      throw new ResourceNotFoundException("Order not found with id: " + id);
    }

    orderRepository.deleteById(id);
    log.info("Order deleted successfully with id: {}", id);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Order> getOrdersByStatus(OrderStatus status) {
    log.debug("Fetching orders with status: {}", status);
    return orderRepository
      .findByStatus(status)
      .stream()
      .map(orderMapper::toDomain)
      .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<Order> getOrdersByCustomerId(Long customerId) {
    log.debug("Fetching orders for customer: {}", customerId);

    if (!customerRepository.existsById(customerId)) {
      throw new ResourceNotFoundException("Customer not found with id: " + customerId);
    }

    return orderRepository
      .findByCustomerId(customerId)
      .stream()
      .map(orderMapper::toDomain)
      .collect(Collectors.toList());
  }

  @Override
  public Order updateOrderStatus(Long id, OrderStatus status) {
    log.debug("Updating order status for id: {} to {}", id, status);

    OrderEntity orderEntity = orderRepository
      .findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

    Order order = orderMapper.toDomain(orderEntity);
    order.updateStatus(status);

    orderEntity.setStatus(status);
    OrderEntity updatedOrderEntity = orderRepository.save(orderEntity);

    log.info("Order status updated successfully for id: {}", id);
    return orderMapper.toDomain(updatedOrderEntity);
  }
}
