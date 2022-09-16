package com.cjrequena.sample.db.repository;

import com.cjrequena.sample.db.entity.FooEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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
public interface FooRepository extends JpaRepository<FooEntity, Long>, JpaSpecificationExecutor<FooEntity> {
}
