package com.cjrequena.sample.infrastructure.adapter.in.rest.controller;

import com.cjrequena.sample.application.service.CustomerService;
import com.cjrequena.sample.domain.model.aggregate.Customer;
import com.cjrequena.sample.domain.port.in.rest.customer.CreateCustomerUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
public class CustomerRestController implements CreateCustomerUseCase {

  private final CustomerService customerService;

  public CustomerRestController(CustomerService customerService) {
    this.customerService = customerService;
  }

  @PostMapping
  public Customer create(@RequestBody Customer customer) {
    return customerService.create(customer);
  }
}
