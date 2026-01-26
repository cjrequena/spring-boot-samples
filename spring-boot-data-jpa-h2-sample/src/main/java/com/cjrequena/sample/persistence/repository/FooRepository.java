package com.cjrequena.sample.persistence.repository;

import com.cjrequena.sample.persistence.entity.FooEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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

  List<FooEntity> findByNameNamedNativeQueryExample(String name);

  List<FooEntity> findByNameNameQueryExample(String name);

}
