package com.cjrequena.sample.controller.dto;

import com.cjrequena.sample.domain.model.enums.OrderStatus;
import com.cjrequena.sample.shared.common.util.Constant;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private Long id;

    @Pattern(regexp = "^ORD-\\d{8}-\\d{5}$", message = "Order number must match format: ORD-YYYYMMDD-XXXXX")
    //@JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String orderNumber;

    @NotNull(message = "Order date is required")
    @JsonFormat(pattern = Constant.ISO_LOCAL_DATE_TIME)
    private LocalDateTime orderDate;

    @NotNull(message = "Status is required")
    private OrderStatus status;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than 0")
    @Digits(integer = 17, fraction = 2, message = "Total amount must have max 2 decimal places")
    private BigDecimal totalAmount;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
