package com.cjrequena.sample.persistence.repository;

import com.cjrequena.sample.domain.exception.RedisOperationException;
import com.cjrequena.sample.persistence.entity.BookEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;


@Slf4j
@Repository
@RequiredArgsConstructor
public class BookRedisValueOpsRepository {

  private final RedisTemplate<String, Object> redisTemplate;

  /* =========================================================
   * Key helpers & constants
   * ========================================================= */
  private static final String KEY_PREFIX = "books:";

  private String key(String id) {
    Objects.requireNonNull(id, "ID cannot be null");
    return KEY_PREFIX + id;
  }

  /* =========================================================
   * STRING (Value) Operations
   * ========================================================= */

  public void save(BookEntity book) {
    validateBook(book);
    try {
      String key = key(book.getId());
      redisTemplate.opsForValue().set(key, book);
      log.debug("Saved book with Id: {}", book.getId());
    } catch (Exception e) {
      log.error("Failed to save book with Id: {}", book.getId(), e);
      throw new RedisOperationException("Failed to save book", e);
    }
  }

  public void saveWithTTL(BookEntity book, Duration ttl) {
    validateBook(book);
    Objects.requireNonNull(ttl, "TTL cannot be null");

    try {
      String key = key(book.getId());
      redisTemplate.opsForValue().set(key, book, ttl);
      log.debug("Saved book with Id: {} and TTL: {}", book.getId(), ttl);
    } catch (Exception e) {
      log.error("Failed to save book with TTL for Id: {}", book.getId(), e);
      throw new RedisOperationException("Failed to save book with TTL", e);
    }
  }

  public Optional<BookEntity> retrieve(String id) {
    Objects.requireNonNull(id, "Id cannot be null");
    try {
      String key = key(id);
      BookEntity book = (BookEntity) redisTemplate.opsForValue().get(key);
      return Optional.ofNullable(book);
    } catch (Exception e) {
      log.error("Failed to retrieve book with Id: {}", id, e);
      return Optional.empty();
    }
  }

  public boolean delete(String id) {
    Objects.requireNonNull(id, "Id cannot be null");

    try {
      String key = key(id);
      Boolean deleted = redisTemplate.delete(key);
      boolean result = Boolean.TRUE.equals(deleted);
      log.debug("Delete book with ID: {} - Result: {}", id, result);
      return result;
    } catch (Exception e) {
      log.error("Failed to delete book with ID: {}", id, e);
      return false;
    }
  }

  public boolean exists(String id) {
    Objects.requireNonNull(id, "Id cannot be null");

    try {
      String key = key(id);
      return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    } catch (Exception e) {
      log.error("Failed to check existence for Id: {}", id, e);
      return false;
    }
  }

  /* =========================================================
   * HELPER / VALIDATION METHODS
   * ========================================================= */

  /**
   * Validates a book entity.
   *
   * @param book the book to validate
   * @throws IllegalArgumentException if book or required fields are null
   */
  private void validateBook(BookEntity book) {
    Objects.requireNonNull(book, "Book cannot be null");
    Objects.requireNonNull(book.getId(), "Book Id cannot be null");
  }

}
