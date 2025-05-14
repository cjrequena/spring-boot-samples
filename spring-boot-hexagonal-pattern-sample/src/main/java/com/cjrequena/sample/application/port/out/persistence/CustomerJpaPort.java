package com.cjrequena.sample.application.port.out.persistence;

import com.cjrequena.sample.domain.model.Customer;

public interface CustomerJpaPort {
    Customer save(Customer customer);
}
