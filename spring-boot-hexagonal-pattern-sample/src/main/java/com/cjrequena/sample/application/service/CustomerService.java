package com.cjrequena.sample.application.service;

import com.cjrequena.sample.domain.model.Customer;
import com.cjrequena.sample.domain.port.in.CustomerServicePort;
import com.cjrequena.sample.domain.port.out.CustomerRepositoryPort;

public class CustomerService implements CustomerServicePort {

    private final CustomerRepositoryPort customerRepositoryPort;

    public CustomerService(CustomerRepositoryPort customerRepositoryPort) {
        this.customerRepositoryPort = customerRepositoryPort;
    }

    @Override
    public Customer createCustomer(Customer customer) {
        return customerRepositoryPort.save(customer);
    }
}
