package com.cjrequena.sample.domain.mapper;

import com.cjrequena.sample.controller.dto.OrderDTO;
import com.cjrequena.sample.controller.dto.OrderItemDTO;
import com.cjrequena.sample.domain.model.aggregate.Order;
import com.cjrequena.sample.domain.model.aggregate.OrderItem;
import com.cjrequena.sample.domain.model.vo.Money;
import com.cjrequena.sample.domain.model.vo.OrderNumber;
import com.cjrequena.sample.persistence.entity.OrderEntity;
import com.cjrequena.sample.persistence.entity.OrderItemEntity;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", 
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderMapper {

    // Domain <-> Entity mappings
    
    @Mapping(source = "orderNumber", target = "orderNumber", qualifiedByName = "orderNumberToString")
    @Mapping(source = "totalAmount", target = "totalAmount", qualifiedByName = "moneyToBigDecimal")
    @Mapping(source = "customerId", target = "customer.id")
    @Mapping(source = "items", target = "items")
    OrderEntity toEntity(Order order);

    @Mapping(source = "orderNumber", target = "orderNumber", qualifiedByName = "stringToOrderNumber")
    @Mapping(source = "totalAmount", target = "totalAmount", qualifiedByName = "bigDecimalToMoney")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "items", target = "items")
    Order toDomain(OrderEntity entity);

    List<Order> toDomainList(List<OrderEntity> entities);

    // OrderItem mappings
    
    @Mapping(source = "unitPrice", target = "unitPrice", qualifiedByName = "moneyToBigDecimal")
    @Mapping(source = "subtotal", target = "subtotal", qualifiedByName = "moneyToBigDecimal")
    OrderItemEntity toItemEntity(OrderItem item);

    @Mapping(source = "unitPrice", target = "unitPrice", qualifiedByName = "bigDecimalToMoney")
    @Mapping(source = "subtotal", target = "subtotal", qualifiedByName = "bigDecimalToMoney")
    OrderItem toItemDomain(OrderItemEntity entity);

    List<OrderItem> toItemDomainList(List<OrderItemEntity> entities);

    // DTO <-> Domain mappings
    
    @Mapping(source = "orderNumber", target = "orderNumber", qualifiedByName = "stringToOrderNumber")
    @Mapping(source = "totalAmount", target = "totalAmount", qualifiedByName = "bigDecimalToMoney")
    @Mapping(source = "items", target = "items")
    Order toDomainFromDTO(OrderDTO dto);

    @Mapping(source = "orderNumber", target = "orderNumber", qualifiedByName = "orderNumberToString")
    @Mapping(source = "totalAmount", target = "totalAmount", qualifiedByName = "moneyToBigDecimal")
    @Mapping(source = "items", target = "items")
    OrderDTO toDTO(Order order);

    List<OrderDTO> toDTOList(List<Order> orders);

    // OrderItem DTO mappings
    
    @Mapping(source = "unitPrice", target = "unitPrice", qualifiedByName = "bigDecimalToMoney")
    @Mapping(source = "subtotal", target = "subtotal", qualifiedByName = "bigDecimalToMoney")
    OrderItem toItemDomainFromDTO(OrderItemDTO dto);

    @Mapping(source = "unitPrice", target = "unitPrice", qualifiedByName = "moneyToBigDecimal")
    @Mapping(source = "subtotal", target = "subtotal", qualifiedByName = "moneyToBigDecimal")
    OrderItemDTO toItemDTO(OrderItem item);

    // Custom mapping methods
    
    @Named("orderNumberToString")
    default String orderNumberToString(OrderNumber orderNumber) {
        return orderNumber != null ? orderNumber.getValue() : null;
    }

    @Named("stringToOrderNumber")
    default OrderNumber stringToOrderNumber(String value) {
        return value != null ? OrderNumber.of(value) : null;
    }

    @Named("moneyToBigDecimal")
    default BigDecimal moneyToBigDecimal(Money money) {
        return money != null ? money.getAmount() : null;
    }

    @Named("bigDecimalToMoney")
    default Money bigDecimalToMoney(BigDecimal amount) {
        return amount != null ? Money.of(amount) : null;
    }
}
