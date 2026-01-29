package com.cjrequena.sample.persistence.repository.cache;

import com.cjrequena.sample.domain.exception.CacheException;
import com.cjrequena.sample.domain.model.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Comprehensive Redis cache repository for Book entities.
 *
 * @author cjrequena
 */
@Repository
@Qualifier("bookCacheRedisRepository")
@Slf4j
public class BookCacheRedisHashOpsRepository implements CacheRepository<String, Book> {

  /* =========================================================
   * Redis Key Constants
   * ========================================================= */
  private static final String KEY_PREFIX = "books:";
  private static final String HASH_KEY = KEY_PREFIX + "hash";           // Primary storage


  /* =========================================================
   * Redis Operations
   * ========================================================= */
  private final RedisTemplate<String, Book> redisTemplate;
  private final HashOperations<String, String, Book> hashOps;


  @Autowired
  public BookCacheRedisHashOpsRepository(RedisTemplate<String, Book> redisTemplate) {
    this.redisTemplate = redisTemplate;
    this.hashOps = redisTemplate.opsForHash();
  }

  /* =========================================================
   * HASH Operations - Primary Storage
   * ========================================================= */

  @Override
  public void load(List<Book> books) {
    Objects.requireNonNull(books, "Books list cannot be null");

    try {
      // Clear existing hash
      redisTemplate.delete(HASH_KEY);

      if (books.isEmpty()) {
        log.info("No books to load into Redis cache");
        return;
      }

      // Convert to map and bulk insert
      Map<String, Book> bookMap = books.stream()
        .filter(Objects::nonNull)
        .filter(book -> book.getId() != null)
        .collect(Collectors.toMap(
          Book::getId,
          book -> book,
          (existing, replacement) -> replacement
        ));

      hashOps.putAll(HASH_KEY, bookMap);

      log.info("Loaded {} books into hash storage", bookMap.size());
    } catch (Exception e) {
      log.error("Failed to load books into hash", e);
      throw new CacheException("Failed to load books", e);
    }
  }

  @Override
  public void add(Book book) {
    validateBook(book);

    try {
      hashOps.put(HASH_KEY, book.getId(), book);
      log.debug("Added book to hash: {}", book.getId());
    } catch (Exception e) {
      log.error("Failed to add book to hash: {}", book.getId(), e);
      throw new CacheException("Failed to add book", e);
    }
  }

  @Override
  public List<Book> retrieve() {
    try {
      Map<String, Book> bookMap = hashOps.entries(HASH_KEY);

      if (bookMap == null || bookMap.isEmpty()) {
        return Collections.emptyList();
      }

      return new ArrayList<>(bookMap.values());
    } catch (Exception e) {
      log.error("Failed to retrieve books from hash", e);
      return Collections.emptyList();
    }
  }

  @Override
  public Book retrieveById(String id) {
    Objects.requireNonNull(id, "Id cannot be null");

    try {
      return hashOps.get(HASH_KEY, id);
    } catch (Exception e) {
      log.error("Failed to retrieve book from hash: {}", id, e);
      return null;
    }
  }

  @Override
  public void removeById(String id) {
    Objects.requireNonNull(id, "Id cannot be null");

    try {
      hashOps.delete(HASH_KEY, id);
      log.debug("Removed book from hash: {}", id);
    } catch (Exception e) {
      log.error("Failed to remove book from hash: {}", id, e);
      throw new CacheException("Failed to remove book", e);
    }
  }

  @Override
  public boolean isEmpty() {
    try {
      Long size = hashOps.size(HASH_KEY);
      return size == null || size == 0;
    } catch (Exception e) {
      log.error("Failed to check if hash is empty", e);
      return true;
    }
  }

  public List<Book> retrieveByAuthor(String author) {
    Objects.requireNonNull(author, "Author cannot be null");

    return retrieve().stream()
      .filter(book -> book.getAuthor() != null)
      .filter(book -> book.getAuthor().equalsIgnoreCase(author))
      .collect(Collectors.toList());
  }

  /* =========================================================
   * VALIDATION METHODS
   * ========================================================= */

  private void validateBook(Book book) {
    Objects.requireNonNull(book, "Book cannot be null");
    Objects.requireNonNull(book.getId(), "Book Id cannot be null");
  }

}
