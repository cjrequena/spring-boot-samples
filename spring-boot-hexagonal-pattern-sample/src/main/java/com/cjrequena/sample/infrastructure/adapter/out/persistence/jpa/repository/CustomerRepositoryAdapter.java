package com.cjrequena.sample.infrastructure.adapter.out.persistence.jpa.repository;

import com.cjrequena.sample.domain.port.out.persistence.repository.CustomerRepositoryPort;
import com.cjrequena.sample.infrastructure.adapter.out.persistence.jpa.entity.CustomerEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomerRepositoryAdapter implements CustomerRepositoryPort {
  private final CustomerRepository customerRepository;

  @Override
  public CustomerEntity save(CustomerEntity entity) {
    return this.customerRepository.save(entity);
  }
}
