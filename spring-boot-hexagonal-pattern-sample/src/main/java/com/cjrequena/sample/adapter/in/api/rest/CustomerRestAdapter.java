package com.cjrequena.sample.adapter.in.api.rest;

import com.cjrequena.sample.application.service.CustomerService;
import com.cjrequena.sample.domain.model.Customer;
import com.cjrequena.sample.domain.port.in.api.CustomerRestPort;
import org.springframework.web.bind.annotation.RequestBody;


public class CustomerRestAdapter implements CustomerRestPort {

  private final CustomerService customerService;

  public CustomerRestAdapter(CustomerService customerService) {
    this.customerService = customerService;
  }

  public Customer create(@RequestBody Customer customer) {
    return customerService.create(customer);
  }
}
