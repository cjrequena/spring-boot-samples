package com.cjrequena.sample.infrastructure.adapter.out.persistence.jpa.repository;

import com.cjrequena.sample.domain.port.out.persistence.repository.CustomerRepositoryPort;
import com.cjrequena.sample.infrastructure.adapter.out.persistence.jpa.entity.CustomerEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomerRepositoryAdapter implements CustomerRepositoryPort {
  private final CustomerJpaRepository customerJpaRepository;

  @Override
  public CustomerEntity save(CustomerEntity entity) {
    return this.customerJpaRepository.save(entity);
  }

  @Override
  public List<CustomerEntity> retrieve() {
    return this.customerJpaRepository.findAll();
  }

  @Override
  public Optional<CustomerEntity> retrieveById(Long id) {
    return this.customerJpaRepository.findById(id);
  }
}
