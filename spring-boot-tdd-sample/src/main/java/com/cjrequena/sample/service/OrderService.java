package com.cjrequena.sample.service;

import com.cjrequena.sample.domain.model.aggregate.Order;
import com.cjrequena.sample.domain.model.enums.OrderStatus;
import com.github.fge.jsonpatch.JsonPatch;

import java.util.List;

public interface OrderService {
    
    Order create(Order order);
    
    Order getOrderById(Long id);
    
    List<Order> getAllOrders();
    
    Order updateOrder(Long id, Order order);
    
    Order patchOrder(Long id, JsonPatch patch);
    
    void deleteOrder(Long id);
    
    List<Order> getOrdersByStatus(OrderStatus status);
    
    List<Order> getOrdersByCustomerId(Long customerId);
    
    Order updateOrderStatus(Long id, OrderStatus status);
}
