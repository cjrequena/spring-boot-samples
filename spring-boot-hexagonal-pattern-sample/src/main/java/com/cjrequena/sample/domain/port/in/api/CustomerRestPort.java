package com.cjrequena.sample.domain.port.in.api;

import com.cjrequena.sample.domain.model.Customer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
public interface CustomerRestPort {
  @PostMapping
  Customer create(Customer customer);
}
