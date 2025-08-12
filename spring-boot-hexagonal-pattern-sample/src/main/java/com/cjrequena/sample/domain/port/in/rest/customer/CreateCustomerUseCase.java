package com.cjrequena.sample.domain.port.in.rest.customer;

import com.cjrequena.sample.domain.model.aggregate.Customer;
import com.cjrequena.sample.infrastructure.adapter.in.rest.dto.CustomerDTO;

public interface CreateCustomerUseCase {

   CustomerDTO create(Customer customer);

}
