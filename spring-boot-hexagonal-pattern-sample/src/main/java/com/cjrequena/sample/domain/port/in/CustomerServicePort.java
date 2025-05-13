package com.cjrequena.sample.domain.port.in;

import com.cjrequena.sample.domain.model.Customer;

public interface CustomerServicePort {
    Customer createCustomer(Customer customer);
}
