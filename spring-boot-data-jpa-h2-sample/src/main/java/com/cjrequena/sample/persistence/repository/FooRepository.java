package com.cjrequena.sample.persistence.repository;

import com.cjrequena.sample.persistence.entity.FooEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

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

  List<FooEntity> findByNameNamedQueryExample(String name);

  @Query(value =
    """ 
    SELECT * FROM Foo WHERE name = :name
    """, nativeQuery = true)
  List<FooEntity> findByNameNativeQueryExample(@Param("name") String name);

}
