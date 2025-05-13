package com.cjrequena.sample.domain.port.out;

import com.cjrequena.sample.domain.model.Customer;

public interface CustomerRepositoryPort {
    Customer save(Customer customer);
}
