package com.cjrequena.sample.adapter.in.api.rest;

import com.cjrequena.sample.application.service.CustomerService;
import com.cjrequena.sample.domain.model.Customer;
import com.cjrequena.sample.domain.port.in.CustomerAPIPort;
import org.springframework.web.bind.annotation.RequestBody;


public class CustomerAPIAdapter implements CustomerAPIPort {

  private final CustomerService customerService;

  public CustomerAPIAdapter(CustomerService customerService) {
    this.customerService = customerService;
  }

  public Customer create(@RequestBody Customer customer) {
    return customerService.create(customer);
  }
}
