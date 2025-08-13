package com.cjrequena.sample.domain.mapper;

import com.cjrequena.sample.domain.model.aggregate.Customer;
import com.cjrequena.sample.infrastructure.adapter.in.rest.dto.CustomerDTO;
import com.cjrequena.sample.infrastructure.adapter.out.persistence.jpa.entity.CustomerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(
  componentModel = "spring",
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface CustomerMapper {

  @Mapping(source = "email", target = "email")
  CustomerEntity toEntity(Customer customer);

  @Mapping(source = "email", target = "email")
  Customer toAggregate(CustomerEntity entity);

  @Mapping(source = "email", target = "email")
  CustomerDTO toDTO (Customer customer);
}
