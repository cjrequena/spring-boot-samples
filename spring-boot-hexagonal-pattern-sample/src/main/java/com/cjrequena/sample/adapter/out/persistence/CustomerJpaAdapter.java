package com.cjrequena.sample.adapter.out.persistence;

import com.cjrequena.sample.adapter.out.persistence.entity.CustomerEntity;
import com.cjrequena.sample.adapter.out.persistence.repository.CustomerJpaRepository;
import com.cjrequena.sample.domain.model.Customer;
import com.cjrequena.sample.domain.port.out.CustomerJpaPort;

public class CustomerJpaAdapter implements CustomerJpaPort {

    private final CustomerJpaRepository customerJpaRepository;

    public CustomerJpaAdapter(CustomerJpaRepository customerJpaRepository) {
        this.customerJpaRepository = customerJpaRepository;
    }

    @Override
    public Customer save(Customer customer) {
        CustomerEntity entity = new CustomerEntity();
        entity.setName(customer.getName());
        entity.setEmail(customer.getEmail());
        CustomerEntity saved = customerJpaRepository.save(entity);
        customer.setId(saved.getId());
        return customer;
    }
}
