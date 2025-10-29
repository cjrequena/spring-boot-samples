package com.cjrequena.sample.domain.model.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@EqualsAndHashCode
public class OrderNumber {
    private static final AtomicLong counter = new AtomicLong(0);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    
    private final String value;

    private OrderNumber(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Order number cannot be null or empty");
        }
        if (!value.matches("^ORD-\\d{8}-\\d{5}$")) {
            throw new IllegalArgumentException("Invalid order number format. Expected: ORD-YYYYMMDD-XXXXX");
        }
        this.value = value;
    }

    public static OrderNumber of(String value) {
        return new OrderNumber(value);
    }

    public static OrderNumber generate() {
        String date = LocalDateTime.now().format(formatter);
        long sequence = counter.incrementAndGet();
        String orderNumber = String.format("ORD-%s-%05d", date, sequence % 100000);
        return new OrderNumber(orderNumber);
    }

    @Override
    public String toString() {
        return value;
    }
}
