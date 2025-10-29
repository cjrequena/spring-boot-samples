package com.cjrequena.sample.controller.dto;

import com.cjrequena.sample.domain.model.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private Long id;

    @Pattern(regexp = "^ORD-\\d{8}-\\d{5}$", 
             message = "Order number must match format: ORD-YYYYMMDD-XXXXX")
    private String orderNumber;

    @NotNull(message = "Order date is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime orderDate;

    @NotNull(message = "Status is required")
    private OrderStatus status;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than 0")
    @Digits(integer = 17, fraction = 2, message = "Total amount must have max 2 decimal places")
    private BigDecimal totalAmount;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Items list is required")
    @Size(min = 1, message = "Order must have at least one item")
    @Valid
    private List<OrderItemDTO> items;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
