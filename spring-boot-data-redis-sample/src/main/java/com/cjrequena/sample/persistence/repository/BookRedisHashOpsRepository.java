package com.cjrequena.sample.persistence.repository;

import com.cjrequena.sample.persistence.entity.BookEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BookRedisHashOpsRepository {

  private final RedisTemplate<String, Object> redisTemplate;

  /* =========================================================
   * Key helpers & constants
   * ========================================================= */
  private static final String KEY_PREFIX = "books:hash:";

  /* =========================================================
   * HASH Operations
   * ========================================================= */

  public void save(BookEntity book) {
    validateBook(book);

    try {
      redisTemplate.opsForHash().put(KEY_PREFIX, book.getId(), book);
      log.debug("Saved book to hash with Id: {}", book.getId());
    } catch (Exception e) {
      log.error("Failed to save book to hash with Id: {}", book.getId(), e);
      throw new BookRedisRepository.RedisOperationException("Failed to save book to hash", e);
    }
  }

  public Optional<BookEntity> retrieve(String id) {
    Objects.requireNonNull(id, "Id cannot be null");

    try {
      BookEntity book = (BookEntity) redisTemplate.opsForHash().get(KEY_PREFIX, id);
      return Optional.ofNullable(book);
    } catch (Exception e) {
      log.error("Failed to retrieve book from hash with Id: {}", id, e);
      return Optional.empty();
    }
  }

  public Map<String, BookEntity> retrieve() {
    try {
      Map<Object, Object> raw = redisTemplate.opsForHash().entries(KEY_PREFIX);

      return raw.entrySet().stream()
        .collect(Collectors.toMap(
          entry -> (String) entry.getKey(),
          entry -> (BookEntity) entry.getValue()
        ));
    } catch (Exception e) {
      log.error("Failed to retrieve all books from hash", e);
      return Collections.emptyMap();
    }
  }

  public Long delete(String id) {
    Objects.requireNonNull(id, "Id cannot be null");

    try {
      Long deleted = redisTemplate.opsForHash().delete(KEY_PREFIX, id);
      log.debug("Deleted book from hash with Id: {} - Count: {}", id, deleted);
      return deleted;
    } catch (Exception e) {
      log.error("Failed to delete book from hash with Id: {}", id, e);
      return 0L;
    }
  }

  /* =========================================================
   * HELPER / VALIDATION METHODS
   * ========================================================= */

  private void validateBook(BookEntity book) {
    Objects.requireNonNull(book, "Book cannot be null");
    Objects.requireNonNull(book.getId(), "Book Id cannot be null");
  }

}
