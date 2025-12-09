package com.cjrequena.sample.controller.rest;

import com.cjrequena.sample.controller.dto.OrderDTO;
import com.cjrequena.sample.controller.exception.BadRequestException;
import com.cjrequena.sample.controller.exception.NotFoundException;
import com.cjrequena.sample.domain.excepption.CustomertNotFoundException;
import com.cjrequena.sample.domain.mapper.OrderMapper;
import com.cjrequena.sample.domain.model.aggregate.Order;
import com.cjrequena.sample.domain.model.enums.OrderStatus;
import com.cjrequena.sample.service.OrderService;
import com.github.fge.jsonpatch.JsonPatch;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cjrequena.sample.shared.common.util.Constant.VND_SAMPLE_SERVICE_V1;

@Slf4j
@RestController
@RequestMapping(value = OrderController.ENDPOINT, headers = {OrderController.ACCEPT_VERSION})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderController {

  public static final String ENDPOINT = "/api/orders";
  public static final String ACCEPT_VERSION = "Accept-Version=" + VND_SAMPLE_SERVICE_V1;

  private final OrderService orderService;
  private final OrderMapper orderMapper;

  @PostMapping
  public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
    try {
      log.info("REST request to create order");

      Order order = orderMapper.toDomainFromDTO(orderDTO);
      Order createdOrder = orderService.create(order);
      OrderDTO responseDTO = orderMapper.toDTO(createdOrder);

      return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    } catch (CustomertNotFoundException ex) {
      throw new BadRequestException(ex.getMessage(), ex);
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
    try {
      log.info("REST request to get order by id: {}", id);

      Order order = orderService.getOrderById(id);
      OrderDTO orderDTO = orderMapper.toDTO(order);

      return ResponseEntity.ok(orderDTO);
    } catch (Exception ex) {
      throw new NotFoundException(ex.getMessage(), ex);
    }
  }

  @GetMapping
  public ResponseEntity<List<OrderDTO>> getAllOrders(
    @RequestParam(required = false) OrderStatus status,
    @RequestParam(required = false) Long customerId) {

    log.info("REST request to get all orders. Status: {}, CustomerId: {}", status, customerId);

    List<Order> orders;

    if (status != null) {
      orders = orderService.getOrdersByStatus(status);
    } else if (customerId != null) {
      orders = orderService.getOrdersByCustomerId(customerId);
    } else {
      orders = orderService.getAllOrders();
    }

    List<OrderDTO> orderDTOs = orderMapper.toDTOList(orders);
    return ResponseEntity.ok(orderDTOs);
  }

  @PutMapping("/{id}")
  public ResponseEntity<OrderDTO> updateOrder(
    @PathVariable Long id,
    @Valid @RequestBody OrderDTO orderDTO) {

    try {
      log.info("REST request to update order with id: {}", id);

      Order order = orderMapper.toDomainFromDTO(orderDTO);
      Order updatedOrder = orderService.updateOrder(id, order);
      OrderDTO responseDTO = orderMapper.toDTO(updatedOrder);

      return ResponseEntity.ok(responseDTO);
    } catch (Exception ex) {
      throw new NotFoundException(ex.getMessage(), ex);
    }
  }

  @PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
  public ResponseEntity<OrderDTO> patchOrder(@PathVariable Long id, @RequestBody JsonPatch patch) {

    log.info("REST request to patch order with id: {}", id);

    Order patchedOrder = orderService.patchOrder(id, patch);
    OrderDTO responseDTO = orderMapper.toDTO(patchedOrder);

    return ResponseEntity.ok(responseDTO);
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status) {

    log.info("REST request to update order status. Id: {}, Status: {}", id, status);

    Order updatedOrder = orderService.updateOrderStatus(id, status);
    OrderDTO responseDTO = orderMapper.toDTO(updatedOrder);

    return ResponseEntity.ok(responseDTO);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
    try {
      log.info("REST request to delete order with id: {}", id);

      orderService.deleteOrder(id);

      return ResponseEntity.noContent().build();
    } catch (Exception ex) {
      throw new NotFoundException(ex.getMessage(), ex);
    }
  }
}
