package com.cjrequena.sample.domain.port.out.persistence.repository;

import com.cjrequena.sample.infrastructure.adapter.out.persistence.jpa.entity.CustomerEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepositoryPort {
  CustomerEntity save(CustomerEntity entity);
  List<CustomerEntity> retrieve();
  Optional<CustomerEntity> retrieveById(Long id);
}
