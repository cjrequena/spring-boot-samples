package com.cjrequena.sample.application.service;

import com.cjrequena.sample.application.port.out.persistence.CustomerJpaPort;
import com.cjrequena.sample.domain.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor=@__(@Autowired))
public class CustomerService  {

    private final CustomerJpaPort customerJpaPort;

    public Customer create(Customer customer) {
        return customerJpaPort.save(customer);
    }
}
