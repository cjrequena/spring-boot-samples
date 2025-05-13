package com.cjrequena.sample.repository;

import com.cjrequena.sample.entity.FooEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * <p>
 * <p>
 * <p>
 * @author cjrequena
 *
 */
@Repository
public interface FooRepository extends JpaRepository<FooEntity, Long> {
}
