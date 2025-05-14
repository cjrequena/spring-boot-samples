package com.cjrequena.sample.application.port.in.api;

import com.cjrequena.sample.domain.model.Customer;


public interface CustomerRestPort {
  Customer create(Customer customer);
}
