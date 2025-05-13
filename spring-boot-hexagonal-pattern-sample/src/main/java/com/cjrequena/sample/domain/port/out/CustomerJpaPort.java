package com.cjrequena.sample.domain.port.out;

import com.cjrequena.sample.domain.model.Customer;

public interface CustomerJpaPort {
    Customer save(Customer customer);
}
