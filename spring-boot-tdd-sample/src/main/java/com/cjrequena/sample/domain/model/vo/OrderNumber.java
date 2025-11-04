package com.cjrequena.sample.domain.model.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

public record OrderNumber(String value) {

    private static final AtomicLong counter = new AtomicLong(0);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public OrderNumber {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Order number cannot be null or empty");
        }
        if (!value.matches("^ORD-\\d{8}-\\d{5}$")) {
            throw new IllegalArgumentException("Invalid order number format. Expected: ORD-YYYYMMDD-XXXXX");
        }
    }

    public static OrderNumber generate() {
        String date = LocalDateTime.now().format(formatter);
        long sequence = counter.incrementAndGet();
        String orderNumber = String.format("ORD-%s-%05d", date, sequence % 100000);
        return new OrderNumber(orderNumber);
    }

    public static OrderNumber of(String value) {
        return new OrderNumber(value);
    }

    @JsonValue
    @Override
    public String toString() {
        return value;
    }
}
