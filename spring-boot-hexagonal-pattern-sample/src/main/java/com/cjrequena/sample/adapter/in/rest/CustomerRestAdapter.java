package com.cjrequena.sample.adapter.in.rest;

import com.cjrequena.sample.application.port.in.rest.CustomerRestPort;
import com.cjrequena.sample.application.service.CustomerService;
import com.cjrequena.sample.domain.model.Customer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
public class CustomerRestAdapter implements CustomerRestPort {

  private final CustomerService customerService;

  public CustomerRestAdapter(CustomerService customerService) {
    this.customerService = customerService;
  }
  @PostMapping
  public Customer create(@RequestBody Customer customer) {
    return customerService.create(customer);
  }
}
