package com.cjrequena.sample.application.service;

import com.cjrequena.sample.domain.mapper.CustomerMapper;
import com.cjrequena.sample.domain.model.aggregate.Customer;
import com.cjrequena.sample.infrastructure.adapter.out.persistence.jpa.entity.CustomerEntity;
import com.cjrequena.sample.infrastructure.adapter.out.persistence.jpa.repository.CustomerRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor=@__(@Autowired))
public class CustomerService {

    private final CustomerRepositoryAdapter customerRepositoryAdapter;
    private final CustomerMapper customerMapper;

    public Customer create(Customer customer) {
        CustomerEntity entity = this.customerMapper.toEntity(customer);
        entity = customerRepositoryAdapter.save(entity);
        return this.customerMapper.toAggregate(entity);
    }
}
