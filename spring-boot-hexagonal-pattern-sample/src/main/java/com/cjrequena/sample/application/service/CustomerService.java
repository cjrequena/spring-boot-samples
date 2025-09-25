package com.cjrequena.sample.application.service;

import com.cjrequena.sample.domain.exception.domain.CustomerNotFoundException;
import com.cjrequena.sample.domain.exception.domain.DomainException;
import com.cjrequena.sample.domain.exception.domain.DomainRuntimeException;
import com.cjrequena.sample.domain.mapper.CustomerMapper;
import com.cjrequena.sample.domain.model.aggregate.Customer;
import com.cjrequena.sample.domain.port.in.customer.CreateCustomerUseCase;
import com.cjrequena.sample.domain.port.in.customer.RetrieveCustomerUseCase;
import com.cjrequena.sample.infrastructure.adapter.out.persistence.jpa.entity.CustomerEntity;
import com.cjrequena.sample.infrastructure.adapter.out.persistence.jpa.repository.CustomerRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {DomainException.class, DomainRuntimeException.class, ConcurrentException.class})
public class CustomerService implements CreateCustomerUseCase, RetrieveCustomerUseCase {

  private final CustomerRepositoryAdapter customerRepositoryAdapter;
  private final CustomerMapper customerMapper;

  @Override
  public Customer create(Customer customer) {
    CustomerEntity entity = this.customerMapper.toEntity(customer);
    entity = customerRepositoryAdapter.save(entity);
    return this.customerMapper.toAggregate(entity);
  }

  @Override
  public List<Customer> retrieve() {
    return this.customerRepositoryAdapter
      .retrieve()
      .stream()
      .map(this.customerMapper::toAggregate)
      .toList();
  }

  @Override
  public Customer retrieveById(Long id) {
    return this.customerRepositoryAdapter
      .retrieveById(id)
      .map(this.customerMapper::toAggregate)
      .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + id));
  }
}
