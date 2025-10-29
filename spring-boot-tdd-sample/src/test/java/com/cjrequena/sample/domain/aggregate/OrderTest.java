package com.cjrequena.sample.domain.aggregate;

import com.cjrequena.sample.domain.model.aggregate.Order;
import com.cjrequena.sample.domain.model.aggregate.OrderItem;
import com.cjrequena.sample.domain.model.enums.OrderStatus;
import com.cjrequena.sample.domain.model.vo.Money;
import com.cjrequena.sample.domain.model.vo.OrderNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Order Aggregate Tests")
class OrderTest {

    private Order order;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        orderItem = OrderItem.builder()
                .productName("Test Product")
                .sku("TEST-001")
                .quantity(2)
                .unitPrice(Money.of(50.00))
                .subtotal(Money.of(100.00))
                .build();

        order = Order.builder()
                .id(1L)
                .orderNumber(OrderNumber.of("ORD-20250101-00001"))
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .totalAmount(Money.zero())
                .customerId(1L)
                .items(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Should add item to order")
    void testAddItem() {
        order.addItem(orderItem);
        
        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getItems().get(0)).isEqualTo(orderItem);
    }

    @Test
    @DisplayName("Should throw exception when adding null item")
    void testAddItem_Null() {
        assertThatThrownBy(() -> order.addItem(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Order item cannot be null");
    }

    @Test
    @DisplayName("Should remove item from order")
    void testRemoveItem() {
        order.addItem(orderItem);
        order.removeItem(orderItem);
        
        assertThat(order.getItems()).isEmpty();
    }

    @Test
    @DisplayName("Should recalculate total when adding items")
    void testRecalculateTotalAmount() {
        OrderItem item1 = OrderItem.builder()
                .productName("Product 1")
                .quantity(2)
                .unitPrice(Money.of(50.00))
                .subtotal(Money.of(100.00))
                .build();

        OrderItem item2 = OrderItem.builder()
                .productName("Product 2")
                .quantity(1)
                .unitPrice(Money.of(30.00))
                .subtotal(Money.of(30.00))
                .build();

        order.addItem(item1);
        order.addItem(item2);

        assertThat(order.getTotalAmount().getAmount())
                .isEqualByComparingTo(Money.of(130.00).getAmount());
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
                .items(new ArrayList<>())
                .build();
        assertThat(newOrder.canBeCancelled()).isFalse();
    }

    @Test
    @DisplayName("Should return immutable items list")
    void testGetItems_Immutable() {
        order.addItem(orderItem);
        
        assertThatThrownBy(() -> order.getItems().add(orderItem))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("Should handle multiple items correctly")
    void testMultipleItems() {
        OrderItem item1 = OrderItem.builder()
                .productName("Item 1")
                .quantity(1)
                .unitPrice(Money.of(10.00))
                .subtotal(Money.of(10.00))
                .build();

        OrderItem item2 = OrderItem.builder()
                .productName("Item 2")
                .quantity(2)
                .unitPrice(Money.of(20.00))
                .subtotal(Money.of(40.00))
                .build();

        OrderItem item3 = OrderItem.builder()
                .productName("Item 3")
                .quantity(3)
                .unitPrice(Money.of(30.00))
                .subtotal(Money.of(90.00))
                .build();

        order.addItem(item1);
        order.addItem(item2);
        order.addItem(item3);

        assertThat(order.getItems()).hasSize(3);
        assertThat(order.getTotalAmount().getAmount())
                .isEqualByComparingTo(Money.of(140.00).getAmount());
    }

    @Test
    @DisplayName("Should recalculate total when removing items")
    void testRecalculateTotal_RemoveItem() {
        OrderItem item1 = OrderItem.builder()
                .productName("Item 1")
                .quantity(1)
                .unitPrice(Money.of(50.00))
                .subtotal(Money.of(50.00))
                .build();

        OrderItem item2 = OrderItem.builder()
                .productName("Item 2")
                .quantity(1)
                .unitPrice(Money.of(30.00))
                .subtotal(Money.of(30.00))
                .build();

        order.addItem(item1);
        order.addItem(item2);
        assertThat(order.getTotalAmount().getAmount())
                .isEqualByComparingTo(Money.of(80.00).getAmount());

        order.removeItem(item1);
        assertThat(order.getTotalAmount().getAmount())
                .isEqualByComparingTo(Money.of(30.00).getAmount());
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
                .items(new ArrayList<>())
                .build();
        
        assertThat(newOrder.getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    @DisplayName("Should generate order number if not provided")
    void testDefaultOrderNumber() {
        Order newOrder = Order.builder()
                .items(new ArrayList<>())
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
                .items(new ArrayList<>())
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
    @DisplayName("Should handle empty items list")
    void testEmptyItems() {
        assertThat(order.getItems()).isEmpty();
        assertThat(order.getTotalAmount()).isEqualTo(Money.zero());
    }

    @Test
    @DisplayName("Should add multiple items and calculate correct total")
    void testAddMultipleItems_CorrectTotal() {
        OrderItem item1 = OrderItem.builder()
                .productName("Item 1")
                .quantity(2)
                .unitPrice(Money.of(25.50))
                .subtotal(Money.of(51.00))
                .build();

        OrderItem item2 = OrderItem.builder()
                .productName("Item 2")
                .quantity(3)
                .unitPrice(Money.of(10.00))
                .subtotal(Money.of(30.00))
                .build();

        OrderItem item3 = OrderItem.builder()
                .productName("Item 3")
                .quantity(1)
                .unitPrice(Money.of(99.99))
                .subtotal(Money.of(99.99))
                .build();

        order.addItem(item1);
        order.addItem(item2);
        order.addItem(item3);

        assertThat(order.getItems()).hasSize(3);
        assertThat(order.getTotalAmount().getAmount())
                .isEqualByComparingTo(Money.of(180.99).getAmount());
    }

    @Test
    @DisplayName("Should remove all items and have zero total")
    void testRemoveAllItems() {
        OrderItem item1 = OrderItem.builder()
                .productName("Item 1")
                .quantity(1)
                .unitPrice(Money.of(10.00))
                .subtotal(Money.of(10.00))
                .build();

        OrderItem item2 = OrderItem.builder()
                .productName("Item 2")
                .quantity(1)
                .unitPrice(Money.of(20.00))
                .subtotal(Money.of(20.00))
                .build();

        order.addItem(item1);
        order.addItem(item2);
        
        order.removeItem(item1);
        order.removeItem(item2);

        assertThat(order.getItems()).isEmpty();
        assertThat(order.getTotalAmount()).isEqualTo(Money.zero());
    }

    @Test
    @DisplayName("Should handle complete order lifecycle")
    void testOrderLifecycle() {
        // 1. Create order
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.canBeModified()).isTrue();

        // 2. Add items
        order.addItem(orderItem);
        assertThat(order.getItems()).hasSize(1);

        // 3. Mark as paid
        order.updateStatus(OrderStatus.PAID);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(order.canBeModified()).isTrue();

        // 4. Ship order
        order.updateStatus(OrderStatus.SHIPPED);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
        assertThat(order.canBeModified()).isFalse();

        // 5. Deliver order
        order.updateStatus(OrderStatus.DELIVERED);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        assertThat(order.canBeModified()).isFalse();
        assertThat(order.canBeCancelled()).isFalse();
    }
}
