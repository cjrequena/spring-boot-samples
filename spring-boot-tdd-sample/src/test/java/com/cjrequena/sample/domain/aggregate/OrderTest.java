package com.cjrequena.sample.domain.aggregate;

import com.cjrequena.sample.domain.model.aggregate.Order;
import com.cjrequena.sample.domain.model.enums.OrderStatus;
import com.cjrequena.sample.domain.model.vo.Money;
import com.cjrequena.sample.domain.model.vo.OrderNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Order Aggregate Tests")
class OrderTest {

    private Order order;

    @BeforeEach
    void setUp() {
        order = Order.builder()
                .id(1L)
                .orderNumber(OrderNumber.of("ORD-20250101-00001"))
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .totalAmount(Money.zero())
                .customerId(1L)
                .build();
    }


    @Test
    @DisplayName("Should update order status")
    void testUpdateStatus() {
        order.updateStatus(OrderStatus.PAID);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    @DisplayName("Should not allow status change on cancelled order")
    void testUpdateStatus_Cancelled() {
        order.updateStatus(OrderStatus.CANCELLED);
        
        assertThatThrownBy(() -> order.updateStatus(OrderStatus.PAID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot change status of a cancelled order");
    }

    @Test
    @DisplayName("Should allow cancellation of delivered order")
    void testUpdateStatus_DeliveredToCancelled() {
        order.updateStatus(OrderStatus.PAID);
        order.updateStatus(OrderStatus.SHIPPED);
        order.updateStatus(OrderStatus.DELIVERED);
        
        assertThatCode(() -> order.updateStatus(OrderStatus.CANCELLED))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should not allow non-cancellation status change on delivered order")
    void testUpdateStatus_DeliveredToOther() {
        order.updateStatus(OrderStatus.DELIVERED);
        
        assertThatThrownBy(() -> order.updateStatus(OrderStatus.SHIPPED))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Delivered order can only be cancelled");
    }

    @Test
    @DisplayName("Should allow modification when pending or paid")
    void testCanBeModified() {
        assertThat(order.canBeModified()).isTrue();
        
        order.updateStatus(OrderStatus.PAID);
        assertThat(order.canBeModified()).isTrue();
        
        order.updateStatus(OrderStatus.SHIPPED);
        assertThat(order.canBeModified()).isFalse();
    }

    @Test
    @DisplayName("Should allow cancellation except for delivered and already cancelled")
    void testCanBeCancelled() {
        assertThat(order.canBeCancelled()).isTrue();
        
        order.updateStatus(OrderStatus.DELIVERED);
        assertThat(order.canBeCancelled()).isFalse();
        
        Order newOrder = Order.builder()
                .status(OrderStatus.CANCELLED)
                .build();
        assertThat(newOrder.canBeCancelled()).isFalse();
    }


    @Test
    @DisplayName("Should handle status transitions correctly")
    void testStatusTransitions() {
        // PENDING -> PAID
        order.updateStatus(OrderStatus.PAID);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);

        // PAID -> SHIPPED
        order.updateStatus(OrderStatus.SHIPPED);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);

        // SHIPPED -> DELIVERED
        order.updateStatus(OrderStatus.DELIVERED);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    @DisplayName("Should start with zero total amount")
    void testInitialTotalAmount() {
        assertThat(order.getTotalAmount()).isEqualTo(Money.zero());
    }

    @Test
    @DisplayName("Should have default status as PENDING")
    void testDefaultStatus() {
        Order newOrder = Order.builder()
                .build();
        
        assertThat(newOrder.getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    @DisplayName("Should generate order number if not provided")
    void testDefaultOrderNumber() {
        Order newOrder = Order.builder()
                .build();
        
        assertThat(newOrder.getOrderNumber()).isNotNull();
        assertThat(newOrder.getOrderNumber().getValue()).matches("^ORD-\\d{8}-\\d{5}$");
    }

    @Test
    @DisplayName("Should maintain order date")
    void testOrderDate() {
        LocalDateTime now = LocalDateTime.now();
        Order newOrder = Order.builder()
                .orderDate(now)
                .build();
        
        assertThat(newOrder.getOrderDate()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should not allow modification when shipped")
    void testCannotModify_Shipped() {
        order.updateStatus(OrderStatus.SHIPPED);
        assertThat(order.canBeModified()).isFalse();
    }

    @Test
    @DisplayName("Should not allow modification when delivered")
    void testCannotModify_Delivered() {
        order.updateStatus(OrderStatus.DELIVERED);
        assertThat(order.canBeModified()).isFalse();
    }

    @Test
    @DisplayName("Should not allow modification when cancelled")
    void testCannotModify_Cancelled() {
        order.updateStatus(OrderStatus.CANCELLED);
        assertThat(order.canBeModified()).isFalse();
    }

    @Test
    @DisplayName("Should allow cancellation when pending")
    void testCanCancel_Pending() {
        assertThat(order.canBeCancelled()).isTrue();
    }

    @Test
    @DisplayName("Should allow cancellation when paid")
    void testCanCancel_Paid() {
        order.updateStatus(OrderStatus.PAID);
        assertThat(order.canBeCancelled()).isTrue();
    }

    @Test
    @DisplayName("Should allow cancellation when shipped")
    void testCanCancel_Shipped() {
        order.updateStatus(OrderStatus.SHIPPED);
        assertThat(order.canBeCancelled()).isTrue();
    }


    @Test
    @DisplayName("Should handle complete order lifecycle")
    void testOrderLifecycle() {
        // 1. Create order
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.canBeModified()).isTrue();

        // 2. Mark as paid
        order.updateStatus(OrderStatus.PAID);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(order.canBeModified()).isTrue();

        // 3. Ship order
        order.updateStatus(OrderStatus.SHIPPED);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
        assertThat(order.canBeModified()).isFalse();

        // 4. Deliver order
        order.updateStatus(OrderStatus.DELIVERED);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        assertThat(order.canBeModified()).isFalse();
        assertThat(order.canBeCancelled()).isFalse();
    }
}
