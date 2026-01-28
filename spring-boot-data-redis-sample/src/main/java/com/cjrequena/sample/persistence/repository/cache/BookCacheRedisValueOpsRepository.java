package com.cjrequena.sample.persistence.repository.cache;

import com.cjrequena.sample.domain.model.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Comprehensive Redis cache repository for Book entities.
 *
 * @author cjrequena
 */
@Repository
@Qualifier("bookCacheRedisValueOpsRepository")
@Slf4j
public class BookCacheRedisValueOpsRepository implements CacheRepository<String, Book> {

  /* =========================================================
   * Redis Key Constants
   * ========================================================= */
  private static final String KEY_PREFIX = "books:";


  /* =========================================================
   * Redis Operations
   * ========================================================= */

  private final RedisTemplate<String, Book> redisTemplate;
  private final ValueOperations<String, Book> valueOps;


  @Autowired
  public BookCacheRedisValueOpsRepository(RedisTemplate<String, Book> redisTemplate) {
    this.redisTemplate = redisTemplate;
    this.valueOps = redisTemplate.opsForValue();
  }

  /* =========================================================
   * STRING (Value) Operations
   * ========================================================= */
  @Override
  public void load(List<Book> books) {
    redisTemplate.delete(redisTemplate.keys(KEY_PREFIX + "*"));
    books.forEach(book -> valueOps.set(KEY_PREFIX + book.getIsbn(), book));
    log.info("Redis cache loaded with {} books.", books.size());
  }

  @Override
  public void add(Book book) {
    valueOps.set(KEY_PREFIX + book.getIsbn(), book, 1, TimeUnit.HOURS); // Optional expiration
  }

  @Override
  public List<Book> retrieve() {
    return redisTemplate
      .keys(KEY_PREFIX + "*")
      .stream()
      .map(valueOps::get)
      .collect(Collectors.toList());
  }

  @Override
  public Book retrieveById(String id) {
    return valueOps.get(KEY_PREFIX + id);

  }

  @Override
  public void removeById(String id) {
    redisTemplate.delete(KEY_PREFIX + id);
  }

  @Override
  public boolean isEmpty() {
    return redisTemplate.keys(KEY_PREFIX + "*").isEmpty();
  }

  public List<Book> retrieveByAuthor(String author) {
    return retrieve()
      .stream()
      .filter(book -> book.getAuthor().equalsIgnoreCase(author))
      .collect(Collectors.toList());
  }

  /* =========================================================
   * VALIDATION METHODS
   * ========================================================= */

  private void validateBook(Book book) {
    Objects.requireNonNull(book, "Book cannot be null");
    Objects.requireNonNull(book.getIsbn(), "Book ISBN cannot be null");
  }

}
