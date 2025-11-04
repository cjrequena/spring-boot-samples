package com.cjrequena.sample.domain.model.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.DecimalMin;
import lombok.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Money(
  @NonNull
  @DecimalMin(value = "0.00", message = "Account balance cannot be negative")
  BigDecimal amount
) {
  private static final int SCALE = 2;

  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  public Money {
    if (amount == null) {
      throw new IllegalArgumentException("Amount cannot be null");
    }
    if (amount.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Amount cannot be negative");
    }
    amount = amount.setScale(SCALE, RoundingMode.HALF_UP);
  }

  public static Money of(BigDecimal amount) {
    return new Money(amount);
  }

  public static Money of(double amount) {
    return new Money(BigDecimal.valueOf(amount));
  }

  public static Money zero() {
    return new Money(BigDecimal.ZERO);
  }

  public Money add(Money other) {
    return new Money(this.amount.add(other.amount));
  }

  public Money subtract(Money other) {
    BigDecimal result = this.amount.subtract(other.amount);
    if (result.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Resulting amount cannot be negative");
    }
    return new Money(result);  }

  public Money multiply(int quantity) {
    return new Money(this.amount.multiply(BigDecimal.valueOf(quantity)));
  }

  public boolean isGreaterThan(Money other) {
    return this.amount.compareTo(other.amount) > 0;
  }

  public boolean isLessThan(Money other) {
    return this.amount.compareTo(other.amount) < 0;
  }

  @JsonValue
  @Override
  public String toString() {
    return amount.toString();
  }
}
