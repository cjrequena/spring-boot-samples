package com.cjrequena.sample.domain.model.aggregate;

import com.cjrequena.sample.domain.model.vo.Money;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"productName", "sku"})
public class OrderItem {
    
    private Long id;
    private String productName;
    private String sku;
    private Integer quantity;
    private Money unitPrice;
    
    @Builder.Default
    private Money subtotal = Money.zero();

    public OrderItem(String productName, String sku, Integer quantity, Money unitPrice) {
        validateFields(productName, quantity, unitPrice);
        this.productName = productName;
        this.sku = sku;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = calculateSubtotal();
    }

    public void updateQuantity(Integer newQuantity) {
        if (newQuantity == null || newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        this.quantity = newQuantity;
        this.subtotal = calculateSubtotal();
    }

    public void updateUnitPrice(Money newPrice) {
        if (newPrice == null) {
            throw new IllegalArgumentException("Unit price cannot be null");
        }
        this.unitPrice = newPrice;
        this.subtotal = calculateSubtotal();
    }

    private Money calculateSubtotal() {
        return unitPrice.multiply(quantity);
    }

    private void validateFields(String productName, Integer quantity, Money unitPrice) {
        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        if (unitPrice == null) {
            throw new IllegalArgumentException("Unit price cannot be null");
        }
    }
}
