package com.cjrequena.sample.domain.vo;

import com.cjrequena.sample.domain.model.vo.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Money Value Object Tests")
class MoneyTest {

    @Test
    @DisplayName("Should create money with valid amount")
    void testCreate_ValidAmount() {
        Money money = Money.of(BigDecimal.valueOf(100.50));
        assertThat(money.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(100.50));
    }

    @Test
    @DisplayName("Should throw exception for null amount")
    void testCreate_NullAmount() {
        assertThatThrownBy(() -> Money.of((BigDecimal) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Amount cannot be null");
    }

    @Test
    @DisplayName("Should throw exception for negative amount")
    void testCreate_NegativeAmount() {
        assertThatThrownBy(() -> Money.of(BigDecimal.valueOf(-10)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Amount cannot be negative");
    }

    @Test
    @DisplayName("Should add money correctly")
    void testAdd() {
        Money money1 = Money.of(50.00);
        Money money2 = Money.of(30.00);
        Money result = money1.add(money2);
        assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(80.00));
    }

    @Test
    @DisplayName("Should subtract money correctly")
    void testSubtract() {
        Money money1 = Money.of(100.00);
        Money money2 = Money.of(30.00);
        Money result = money1.subtract(money2);
        assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(70.00));
    }

    @Test
    @DisplayName("Should multiply money correctly")
    void testMultiply() {
        Money money = Money.of(25.00);
        Money result = money.multiply(4);
        assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(100.00));
    }

    @Test
    @DisplayName("Should compare money amounts")
    void testCompare() {
        Money money1 = Money.of(100.00);
        Money money2 = Money.of(50.00);
        
        assertThat(money1.isGreaterThan(money2)).isTrue();
        assertThat(money2.isLessThan(money1)).isTrue();
        assertThat(money1.isGreaterThan(money1)).isFalse();
    }

    @Test
    @DisplayName("Should create zero money")
    void testZero() {
        Money zero = Money.zero();
        assertThat(zero.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should be equal when amounts are same")
    void testEquals() {
        Money money1 = Money.of(100.00);
        Money money2 = Money.of(100.00);
        assertThat(money1).isEqualTo(money2);
    }

    @Test
    @DisplayName("Should handle decimal precision correctly")
    void testDecimalPrecision() {
        Money money = Money.of(10.999);
        assertThat(money.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(11.00));
    }

    @Test
    @DisplayName("Should create money from double")
    void testCreateFromDouble() {
        Money money = Money.of(49.99);
        assertThat(money.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(49.99));
    }

    @Test
    @DisplayName("Should handle zero amount")
    void testZeroAmount() {
        Money money = Money.of(BigDecimal.ZERO);
        assertThat(money.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should maintain immutability")
    void testImmutability() {
        Money money1 = Money.of(100.00);
        Money money2 = money1.add(Money.of(50.00));
        
        assertThat(money1.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(100.00));
        assertThat(money2.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
    }

    @Test
    @DisplayName("Should have correct toString representation")
    void testToString() {
        Money money = Money.of(123.45);
        assertThat(money.toString()).isEqualTo("123.45");
    }
}
