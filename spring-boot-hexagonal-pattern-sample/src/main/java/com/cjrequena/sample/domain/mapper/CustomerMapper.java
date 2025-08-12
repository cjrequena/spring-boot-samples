package com.cjrequena.sample.domain.mapper;

import com.cjrequena.sample.domain.model.aggregate.Customer;
import com.cjrequena.sample.infrastructure.adapter.in.rest.dto.CustomerDTO;
import com.cjrequena.sample.infrastructure.adapter.out.persistence.jpa.entity.CustomerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(
  componentModel = "spring",
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface CustomerMapper {

  CustomerEntity toEntity(Customer customer);

  Customer toAggregate(CustomerEntity entity);

  CustomerDTO toDTO (Customer customer);
}
