package com.cjrequena.sample.db.repository;

import com.cjrequena.sample.db.entity.FooEntity;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

public interface FooRepository extends ReactiveSortingRepository<FooEntity, String> {
}
