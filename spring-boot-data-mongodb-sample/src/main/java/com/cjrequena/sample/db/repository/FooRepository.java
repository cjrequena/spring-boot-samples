package com.cjrequena.sample.db.repository;

import com.cjrequena.sample.db.entity.FooEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface FooRepository extends ReactiveCrudRepository<FooEntity, String> {
}
