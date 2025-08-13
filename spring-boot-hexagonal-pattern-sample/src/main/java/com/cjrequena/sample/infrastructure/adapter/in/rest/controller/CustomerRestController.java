package com.cjrequena.sample.infrastructure.adapter.in.rest.controller;

import com.cjrequena.sample.domain.mapper.CustomerMapper;
import com.cjrequena.sample.domain.model.aggregate.Customer;
import com.cjrequena.sample.domain.port.in.customer.CreateCustomerUseCase;
import com.cjrequena.sample.domain.service.CustomerService;
import com.cjrequena.sample.infrastructure.adapter.in.rest.dto.CustomerDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor(onConstructor=@__(@Autowired))
public class CustomerRestController  {

  private final CustomerService customerService;
  private final CreateCustomerUseCase createCustomerUseCase;
  private final CustomerMapper customerMapper;

  @PostMapping
  public CustomerDTO create(@RequestBody Customer customer) {
    customer = createCustomerUseCase.create(customer);
    return this.customerMapper.toDTO(customer);
  }
}
