package com.cjrequena.sample.persistence.jpa.repository;

import com.cjrequena.sample.persistence.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    
    Optional<CustomerEntity> findByEmail(String email);
    
    boolean existsByEmail(String email);
}
