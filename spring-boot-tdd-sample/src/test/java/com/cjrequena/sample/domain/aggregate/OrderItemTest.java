package com.cjrequena.sample.domain.aggregate;

import com.cjrequena.sample.domain.model.aggregate.OrderItem;
import com.cjrequena.sample.domain.model.vo.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("OrderItem Entity Tests")
class OrderItemTest {

    @Test
    @DisplayName("Should create order item with valid data")
    void testCreate_ValidData() {
        OrderItem item = new OrderItem("Laptop", "LAPTOP-001", 2, Money.of(500.00));
        
        assertThat(item.getProductName()).isEqualTo("Laptop");
        assertThat(item.getSku()).isEqualTo("LAPTOP-001");
        assertThat(item.getQuantity()).isEqualTo(2);
        assertThat(item.getUnitPrice()).isEqualTo(Money.of(500.00));
        assertThat(item.getSubtotal().getAmount())
                .isEqualByComparingTo(Money.of(1000.00).getAmount());
    }

    @Test
    @DisplayName("Should throw exception for null product name")
    void testCreate_NullProductName() {
        assertThatThrownBy(() -> new OrderItem(null, "SKU", 1, Money.of(10.00)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product name cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw exception for empty product name")
    void testCreate_EmptyProductName() {
        assertThatThrownBy(() -> new OrderItem("", "SKU", 1, Money.of(10.00)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product name cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw exception for blank product name")
    void testCreate_BlankProductName() {
        assertThatThrownBy(() -> new OrderItem("   ", "SKU", 1, Money.of(10.00)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product name cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw exception for invalid quantity")
    void testCreate_InvalidQuantity() {
        assertThatThrownBy(() -> new OrderItem("Product", "SKU", 0, Money.of(10.00)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantity must be greater than zero");
    }

    @Test
    @DisplayName("Should throw exception for negative quantity")
    void testCreate_NegativeQuantity() {
        assertThatThrownBy(() -> new OrderItem("Product", "SKU", -1, Money.of(10.00)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantity must be greater than zero");
    }

    @Test
    @DisplayName("Should throw exception for null quantity")
    void testCreate_NullQuantity() {
        assertThatThrownBy(() -> new OrderItem("Product", "SKU", null, Money.of(10.00)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantity must be greater than zero");
    }

    @Test
    @DisplayName("Should throw exception for null unit price")
    void testCreate_NullUnitPrice() {
        assertThatThrownBy(() -> new OrderItem("Product", "SKU", 1, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unit price cannot be null");
    }

    @Test
    @DisplayName("Should update quantity and recalculate subtotal")
    void testUpdateQuantity() {
        OrderItem item = new OrderItem("Product", "SKU", 2, Money.of(50.00));
        item.updateQuantity(5);
        
        assertThat(item.getQuantity()).isEqualTo(5);
        assertThat(item.getSubtotal().getAmount())
                .isEqualByComparingTo(Money.of(250.00).getAmount());
    }

    @Test
    @DisplayName("Should throw exception for invalid quantity update")
    void testUpdateQuantity_Invalid() {
        OrderItem item = new OrderItem("Product", "SKU", 2, Money.of(50.00));
        
        assertThatThrownBy(() -> item.updateQuantity(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantity must be greater than zero");
    }

    @Test
    @DisplayName("Should throw exception for negative quantity update")
    void testUpdateQuantity_Negative() {
        OrderItem item = new OrderItem("Product", "SKU", 2, Money.of(50.00));
        
        assertThatThrownBy(() -> item.updateQuantity(-5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantity must be greater than zero");
    }

    @Test
    @DisplayName("Should throw exception for null quantity update")
    void testUpdateQuantity_Null() {
        OrderItem item = new OrderItem("Product", "SKU", 2, Money.of(50.00));
        
        assertThatThrownBy(() -> item.updateQuantity(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantity must be greater than zero");
    }

    @Test
    @DisplayName("Should update unit price and recalculate subtotal")
    void testUpdateUnitPrice() {
        OrderItem item = new OrderItem("Product", "SKU", 3, Money.of(50.00));
        item.updateUnitPrice(Money.of(75.00));
        
        assertThat(item.getUnitPrice()).isEqualTo(Money.of(75.00));
        assertThat(item.getSubtotal().getAmount())
                .isEqualByComparingTo(Money.of(225.00).getAmount());
    }

    @Test
    @DisplayName("Should throw exception for null price update")
    void testUpdateUnitPrice_Null() {
        OrderItem item = new OrderItem("Product", "SKU", 2, Money.of(50.00));
        
        assertThatThrownBy(() -> item.updateUnitPrice(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unit price cannot be null");
    }

    @Test
    @DisplayName("Should be equal when product name and SKU match")
    void testEquals() {
        OrderItem item1 = new OrderItem("Product", "SKU-001", 2, Money.of(50.00));
        OrderItem item2 = new OrderItem("Product", "SKU-001", 3, Money.of(60.00));
        
        assertThat(item1).isEqualTo(item2);
    }

    @Test
    @DisplayName("Should not be equal when product names differ")
    void testNotEquals_DifferentName() {
        OrderItem item1 = new OrderItem("Product A", "SKU-001", 2, Money.of(50.00));
        OrderItem item2 = new OrderItem("Product B", "SKU-001", 2, Money.of(50.00));
        
        assertThat(item1).isNotEqualTo(item2);
    }

    @Test
    @DisplayName("Should not be equal when SKUs differ")
    void testNotEquals_DifferentSKU() {
        OrderItem item1 = new OrderItem("Product", "SKU-001", 2, Money.of(50.00));
        OrderItem item2 = new OrderItem("Product", "SKU-002", 2, Money.of(50.00));
        
        assertThat(item1).isNotEqualTo(item2);
    }

    @Test
    @DisplayName("Should calculate subtotal correctly for single item")
    void testSubtotalCalculation_Single() {
        OrderItem item = new OrderItem("Product", "SKU", 1, Money.of(99.99));
        assertThat(item.getSubtotal().getAmount())
                .isEqualByComparingTo(Money.of(99.99).getAmount());
    }

    @Test
    @DisplayName("Should calculate subtotal correctly for multiple items")
    void testSubtotalCalculation_Multiple() {
        OrderItem item = new OrderItem("Product", "SKU", 10, Money.of(25.50));
        assertThat(item.getSubtotal().getAmount())
                .isEqualByComparingTo(Money.of(255.00).getAmount());
    }

    @Test
    @DisplayName("Should allow null SKU")
    void testCreate_NullSKU() {
        OrderItem item = new OrderItem("Product", null, 1, Money.of(10.00));
        assertThat(item.getSku()).isNull();
        assertThat(item.getProductName()).isEqualTo("Product");
    }

    @Test
    @DisplayName("Should allow empty SKU")
    void testCreate_EmptySKU() {
        OrderItem item = new OrderItem("Product", "", 1, Money.of(10.00));
        assertThat(item.getSku()).isEmpty();
        assertThat(item.getProductName()).isEqualTo("Product");
    }

    @Test
    @DisplayName("Should recalculate subtotal when both quantity and price change")
    void testRecalculate_BothChanges() {
        OrderItem item = new OrderItem("Product", "SKU", 2, Money.of(50.00));
        assertThat(item.getSubtotal().getAmount())
                .isEqualByComparingTo(Money.of(100.00).getAmount());

        item.updateQuantity(3);
        assertThat(item.getSubtotal().getAmount())
                .isEqualByComparingTo(Money.of(150.00).getAmount());

        item.updateUnitPrice(Money.of(40.00));
        assertThat(item.getSubtotal().getAmount())
                .isEqualByComparingTo(Money.of(120.00).getAmount());
    }

    @Test
    @DisplayName("Should handle large quantities")
    void testLargeQuantity() {
        OrderItem item = new OrderItem("Product", "SKU", 1000, Money.of(5.00));
        assertThat(item.getSubtotal().getAmount())
                .isEqualByComparingTo(Money.of(5000.00).getAmount());
    }

    @Test
    @DisplayName("Should handle high precision prices")
    void testHighPrecisionPrice() {
        OrderItem item = new OrderItem("Product", "SKU", 3, Money.of(33.33));
        assertThat(item.getSubtotal().getAmount())
                .isEqualByComparingTo(Money.of(99.99).getAmount());
    }

    @Test
    @DisplayName("Should handle quantity update to 1")
    void testUpdateQuantity_ToOne() {
        OrderItem item = new OrderItem("Product", "SKU", 5, Money.of(20.00));
        item.updateQuantity(1);
        
        assertThat(item.getQuantity()).isEqualTo(1);
        assertThat(item.getSubtotal().getAmount())
                .isEqualByComparingTo(Money.of(20.00).getAmount());
    }

    @Test
    @DisplayName("Should handle price update to minimum")
    void testUpdatePrice_Minimum() {
        OrderItem item = new OrderItem("Product", "SKU", 10, Money.of(100.00));
        item.updateUnitPrice(Money.of(0.01));
        
        assertThat(item.getUnitPrice().getAmount())
                .isEqualByComparingTo(Money.of(0.01).getAmount());
        assertThat(item.getSubtotal().getAmount())
                .isEqualByComparingTo(Money.of(0.10).getAmount());
    }

    @Test
    @DisplayName("Should maintain consistency after multiple updates")
    void testMultipleUpdates_Consistency() {
        OrderItem item = new OrderItem("Product", "SKU", 1, Money.of(10.00));
        
        // Update 1
        item.updateQuantity(2);
        assertThat(item.getSubtotal().getAmount())
                .isEqualByComparingTo(Money.of(20.00).getAmount());
        
        // Update 2
        item.updateUnitPrice(Money.of(15.00));
        assertThat(item.getSubtotal().getAmount())
                .isEqualByComparingTo(Money.of(30.00).getAmount());
        
        // Update 3
        item.updateQuantity(5);
        assertThat(item.getSubtotal().getAmount())
                .isEqualByComparingTo(Money.of(75.00).getAmount());
        
        // Update 4
        item.updateUnitPrice(Money.of(20.00));
        assertThat(item.getSubtotal().getAmount())
                .isEqualByComparingTo(Money.of(100.00).getAmount());
    }

    @Test
    @DisplayName("Should have consistent hashCode for equal items")
    void testHashCode_Consistency() {
        OrderItem item1 = new OrderItem("Product", "SKU-001", 2, Money.of(50.00));
        OrderItem item2 = new OrderItem("Product", "SKU-001", 3, Money.of(60.00));
        
        // Equal items should have same hashCode
        assertThat(item1.hashCode()).isEqualTo(item2.hashCode());
    }

    @Test
    @DisplayName("Should handle builder pattern correctly")
    void testBuilder() {
        OrderItem item = OrderItem.builder()
                .id(1L)
                .productName("Test Product")
                .sku("TEST-SKU")
                .quantity(5)
                .unitPrice(Money.of(25.00))
                .subtotal(Money.of(125.00))
                .build();
        
        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getProductName()).isEqualTo("Test Product");
        assertThat(item.getSku()).isEqualTo("TEST-SKU");
        assertThat(item.getQuantity()).isEqualTo(5);
        assertThat(item.getUnitPrice()).isEqualTo(Money.of(25.00));
        assertThat(item.getSubtotal()).isEqualTo(Money.of(125.00));
    }

    @Test
    @DisplayName("Should handle long product names")
    void testLongProductName() {
        String longName = "This is a very long product name that exceeds normal expectations for product naming conventions in e-commerce systems";
        OrderItem item = new OrderItem(longName, "SKU", 1, Money.of(10.00));
        
        assertThat(item.getProductName()).isEqualTo(longName);
    }

    @Test
    @DisplayName("Should handle special characters in product name")
    void testSpecialCharactersInProductName() {
        String specialName = "Product™ with Special® Characters™";
        OrderItem item = new OrderItem(specialName, "SKU", 1, Money.of(10.00));
        
        assertThat(item.getProductName()).isEqualTo(specialName);
    }

    @Test
    @DisplayName("Should handle special characters in SKU")
    void testSpecialCharactersInSKU() {
        String specialSKU = "SKU-2025-#001-A/B";
        OrderItem item = new OrderItem("Product", specialSKU, 1, Money.of(10.00));
        
        assertThat(item.getSku()).isEqualTo(specialSKU);
    }
}
