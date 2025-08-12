package com.cjrequena.sample.domain.port.out.persistence.jpa.repository;

import com.cjrequena.sample.infrastructure.adapter.out.persistence.jpa.entity.CustomerEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepositoryPort {
  CustomerEntity save(CustomerEntity entity);
}
