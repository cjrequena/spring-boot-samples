package com.cjrequena.sample.domain.port.in.rest.customer;

import com.cjrequena.sample.domain.model.aggregate.Customer;

public interface CreateCustomerUseCase {

   Customer create(Customer customer);

}
