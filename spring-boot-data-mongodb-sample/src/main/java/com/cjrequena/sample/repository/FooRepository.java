package com.cjrequena.sample.repository;

import com.cjrequena.sample.entity.FooEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface FooRepository extends ReactiveMongoRepository<FooEntity, String> {
}
