package com.cjrequena.sample.adapter.out.persistence;

import com.cjrequena.sample.adapter.out.persistence.entity.CustomerEntity;
import com.cjrequena.sample.adapter.out.persistence.repository.CustomerJpaRepository;
import com.cjrequena.sample.application.port.out.persistence.CustomerJpaPort;
import com.cjrequena.sample.domain.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
public class CustomerJpaAdapter implements CustomerJpaPort {

    private final CustomerJpaRepository customerJpaRepository;

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
