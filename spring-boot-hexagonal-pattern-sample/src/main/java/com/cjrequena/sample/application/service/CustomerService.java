package com.cjrequena.sample.application.service;

import com.cjrequena.sample.domain.model.Customer;
import com.cjrequena.sample.domain.port.out.persistence.CustomerJpaPort;
import org.springframework.stereotype.Service;

@Service
public class CustomerService  {

    private final CustomerJpaPort customerJpaPort;

    public CustomerService(CustomerJpaPort customerJpaPort) {
        this.customerJpaPort = customerJpaPort;
    }

    public Customer create(Customer customer) {
        return customerJpaPort.save(customer);
    }
}
