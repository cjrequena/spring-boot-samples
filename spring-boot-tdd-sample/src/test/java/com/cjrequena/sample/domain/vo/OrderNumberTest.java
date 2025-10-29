package com.cjrequena.sample.domain.vo;

import com.cjrequena.sample.domain.model.vo.OrderNumber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("OrderNumber Value Object Tests")
class OrderNumberTest {

    @Test
    @DisplayName("Should create order number with valid format")
    void testCreate_ValidFormat() {
        OrderNumber orderNumber = OrderNumber.of("ORD-20250101-00001");
        assertThat(orderNumber.getValue()).isEqualTo("ORD-20250101-00001");
    }

    @Test
    @DisplayName("Should throw exception for null value")
    void testCreate_NullValue() {
        assertThatThrownBy(() -> OrderNumber.of(null))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Order number cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw exception for empty value")
    void testCreate_EmptyValue() {
        assertThatThrownBy(() -> OrderNumber.of(""))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Order number cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw exception for blank value")
    void testCreate_BlankValue() {
        assertThatThrownBy(() -> OrderNumber.of("   "))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Order number cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw exception for invalid format")
    void testCreate_InvalidFormat() {
        assertThatThrownBy(() -> OrderNumber.of("INVALID-123"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Invalid order number format");
    }

    @Test
    @DisplayName("Should throw exception for wrong prefix")
    void testCreate_WrongPrefix() {
        assertThatThrownBy(() -> OrderNumber.of("INV-20250101-00001"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Invalid order number format");
    }

    @Test
    @DisplayName("Should throw exception for invalid date format")
    void testCreate_InvalidDateFormat() {
        assertThatThrownBy(() -> OrderNumber.of("ORD-2025-00001"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Invalid order number format");
    }

    @Test
    @DisplayName("Should throw exception for invalid sequence format")
    void testCreate_InvalidSequenceFormat() {
        assertThatThrownBy(() -> OrderNumber.of("ORD-20250101-001"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Invalid order number format");
    }

    @Test
    @DisplayName("Should generate valid order number")
    void testGenerate() {
        OrderNumber orderNumber = OrderNumber.generate();
        assertThat(orderNumber.getValue()).matches("^ORD-\\d{8}-\\d{5}$");
    }

    @Test
    @DisplayName("Should generate unique order numbers")
    void testGenerate_Unique() {
        OrderNumber orderNumber1 = OrderNumber.generate();
        OrderNumber orderNumber2 = OrderNumber.generate();
        assertThat(orderNumber1.getValue()).isNotEqualTo(orderNumber2.getValue());
    }

    @Test
    @DisplayName("Should generate order numbers with current date")
    void testGenerate_CurrentDate() {
        OrderNumber orderNumber = OrderNumber.generate();
        String value = orderNumber.getValue();

        // Check that date part exists
        assertThat(value).contains("ORD-2025");
    }

    @Test
    @DisplayName("Should be equal when values are same")
    void testEquals() {
        OrderNumber orderNumber1 = OrderNumber.of("ORD-20250101-00001");
        OrderNumber orderNumber2 = OrderNumber.of("ORD-20250101-00001");
        assertThat(orderNumber1).isEqualTo(orderNumber2);
    }

    @Test
    @DisplayName("Should not be equal when values are different")
    void testNotEquals() {
        OrderNumber orderNumber1 = OrderNumber.of("ORD-20250101-00001");
        OrderNumber orderNumber2 = OrderNumber.of("ORD-20250101-00002");
        assertThat(orderNumber1).isNotEqualTo(orderNumber2);
    }

    @Test
    @DisplayName("Should have correct string representation")
    void testToString() {
        OrderNumber orderNumber = OrderNumber.of("ORD-20250101-00001");
        assertThat(orderNumber.toString()).isEqualTo("ORD-20250101-00001");
    }

    @Test
    @DisplayName("Should have consistent hashCode")
    void testHashCode() {
        OrderNumber orderNumber1 = OrderNumber.of("ORD-20250101-00001");
        OrderNumber orderNumber2 = OrderNumber.of("ORD-20250101-00001");
        assertThat(orderNumber1.hashCode()).isEqualTo(orderNumber2.hashCode());
    }

    @Test
    @DisplayName("Should generate sequential numbers")
    void testGenerate_Sequential() {
        OrderNumber orderNumber1 = OrderNumber.generate();
        OrderNumber orderNumber2 = OrderNumber.generate();
        OrderNumber orderNumber3 = OrderNumber.generate();

        // All should be unique
        assertThat(orderNumber1).isNotEqualTo(orderNumber2);
        assertThat(orderNumber2).isNotEqualTo(orderNumber3);
        assertThat(orderNumber1).isNotEqualTo(orderNumber3);
    }

    @Test
    @DisplayName("Should reject order number with special characters")
    void testCreate_SpecialCharacters() {
        assertThatThrownBy(() -> OrderNumber.of("ORD-2025@101-00001"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Invalid order number format");
    }

    @Test
    @DisplayName("Should reject order number with lowercase")
    void testCreate_Lowercase() {
        assertThatThrownBy(() -> OrderNumber.of("ord-20250101-00001"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Invalid order number format");
    }

    @Test
    @DisplayName("Should reject order number with spaces")
    void testCreate_WithSpaces() {
        assertThatThrownBy(() -> OrderNumber.of("ORD-2025 0101-00001"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Invalid order number format");
    }

    @Test
    @DisplayName("Should reject order number too short")
    void testCreate_TooShort() {
        assertThatThrownBy(() -> OrderNumber.of("ORD-2025-001"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Invalid order number format");
    }

    @Test
    @DisplayName("Should reject order number too long")
    void testCreate_TooLong() {
        assertThatThrownBy(() -> OrderNumber.of("ORD-20250101-000001"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Invalid order number format");
    }
}
