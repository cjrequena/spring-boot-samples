package com.cjrequena.sample.domain.port.in.customer;

import com.cjrequena.sample.domain.model.aggregate.Customer;

import java.util.List;

public interface RetrieveCustomerUseCase {

  List<Customer> retrieve();
  Customer retrieveById(Long id);
}
