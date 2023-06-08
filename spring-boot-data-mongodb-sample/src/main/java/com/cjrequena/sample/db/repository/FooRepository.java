package com.cjrequena.sample.db.repository;

import com.cjrequena.sample.db.entity.FooEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface FooRepository extends ReactiveMongoRepository<FooEntity, String> {
}
