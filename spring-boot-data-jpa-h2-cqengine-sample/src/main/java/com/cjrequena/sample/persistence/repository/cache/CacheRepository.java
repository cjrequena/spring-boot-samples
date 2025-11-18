package com.cjrequena.sample.persistence.repository.cache;

import java.util.List;

public interface CacheRepository<K, T> {

  void load(List<T> entities);

  void add(T entity);

  List<T> retrieve();

  T retrieveById(K id);

  void removeById(K id);

  boolean isEmpty();
}
