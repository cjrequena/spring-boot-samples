package com.cjrequena.sample.domain.model.aggregate;

import com.cjrequena.sample.domain.model.enums.OrderStatus;
import com.cjrequena.sample.domain.model.vo.Money;
import com.cjrequena.sample.domain.model.vo.OrderNumber;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

  private Long id;

  @Builder.Default
  private OrderNumber orderNumber = OrderNumber.generate();

  @Builder.Default
  private LocalDateTime orderDate = LocalDateTime.now();

  @Builder.Default
  private OrderStatus status = OrderStatus.PENDING;

  @Builder.Default
  private Money totalAmount = Money.zero();

  private Long customerId;

  @Builder.Default
  private List<OrderItem> items = new ArrayList<>();

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public void addItem(OrderItem item) {
    if (item == null) {
      throw new IllegalArgumentException("Order item cannot be null");
    }
    this.items.add(item);
    recalculateTotalAmount();
  }

  public void removeItem(OrderItem item) {
    this.items.remove(item);
    recalculateTotalAmount();
  }

  public void updateStatus(OrderStatus newStatus) {
    validateStatusTransition(this.status, newStatus);
    this.status = newStatus;
  }

  public List<OrderItem> getItems() {
    return Collections.unmodifiableList(items);
  }

  private void recalculateTotalAmount() {
    this.totalAmount = items.stream()
      .map(OrderItem::getSubtotal)
      .reduce(Money.zero(), Money::add);
  }

  private void validateStatusTransition(OrderStatus current, OrderStatus next) {
    if (current == OrderStatus.CANCELLED) {
      throw new IllegalStateException("Cannot change status of a cancelled order");
    }
    if (current == OrderStatus.DELIVERED && next != OrderStatus.CANCELLED) {
      throw new IllegalStateException("Delivered order can only be cancelled");
    }
  }

  public boolean canBeModified() {
    return status == OrderStatus.PENDING || status == OrderStatus.PAID;
  }

  public boolean canBeCancelled() {
    return status != OrderStatus.DELIVERED && status != OrderStatus.CANCELLED;
  }
}
