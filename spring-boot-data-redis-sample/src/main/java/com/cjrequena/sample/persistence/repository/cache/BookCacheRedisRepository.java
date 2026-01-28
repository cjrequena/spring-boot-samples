package com.cjrequena.sample.persistence.repository.cache;

import com.cjrequena.sample.domain.exception.CacheException;
import com.cjrequena.sample.domain.model.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Comprehensive Redis cache repository for Book entities.
 *
 * @author cjrequena
 */
@Repository
@Qualifier("bookCacheRedisRepository")
@Slf4j
public class BookCacheRedisRepository implements CacheRepository<String, Book> {

  /* =========================================================
   * Redis Key Constants
   * ========================================================= */
  private static final String KEY_PREFIX = "books:";
  private static final String BOOK_HASH_KEY = KEY_PREFIX + "hash";           // Primary storage
  private static final String BOOK_LIST_KEY = KEY_PREFIX + "list";           // Recent books
  private static final String BOOK_SET_KEY = KEY_PREFIX + "set";             // Favorites
  private static final String BOOK_ZSET_KEY = KEY_PREFIX + "zset";           // Rankings
  private static final String BOOK_BITMAP_KEY = KEY_PREFIX + "bitmap";       // Availability
  private static final String BOOK_HLL_KEY = KEY_PREFIX + "hll";             // View counting
  private static final String BOOK_GEO_KEY = KEY_PREFIX + "geo";             // Locations
  private static final String BOOK_STREAM_KEY = KEY_PREFIX + "stream";       // Event log
  private static final String BOOK_PUBSUB_CHANNEL = KEY_PREFIX + "pubsub";   // Notifications

  /* =========================================================
   * Redis Operations
   * ========================================================= */

  private final RedisTemplate<String, Book> redisTemplate;
  private final HashOperations<String, String, Book> hashOps;
  private final ListOperations<String, Book> listOps;
  private final SetOperations<String, Book> setOps;
  private final ZSetOperations<String, Book> zSetOps;
  private final ValueOperations<String, Book> valueOps;
  private final GeoOperations<String, Book> geoOps;
  private final StreamOperations<String, Object, Book> streamOps;
  private final HyperLogLogOperations<String, Book> hllOps;

  @Autowired
  public BookCacheRedisRepository(RedisTemplate<String, Book> redisTemplate) {
    this.redisTemplate = redisTemplate;
    this.hashOps = redisTemplate.opsForHash();
    this.listOps = redisTemplate.opsForList();
    this.setOps = redisTemplate.opsForSet();
    this.zSetOps = redisTemplate.opsForZSet();
    this.valueOps = redisTemplate.opsForValue();
    this.geoOps = redisTemplate.opsForGeo();
    this.streamOps = redisTemplate.opsForStream();
    this.hllOps = redisTemplate.opsForHyperLogLog();
  }

  /* =========================================================
   * HASH Operations - Primary Storage
   * ========================================================= */

  /**
   * Loads books into the hash (primary storage).
   * Clears existing data and loads fresh data.
   *
   * @param books the list of books to load
   */
  @Override
  public void load(List<Book> books) {
    Objects.requireNonNull(books, "Books list cannot be null");

    try {
      // Clear existing hash
      redisTemplate.delete(BOOK_HASH_KEY);

      if (books.isEmpty()) {
        log.info("No books to load into Redis cache");
        return;
      }

      // Convert to map and bulk insert
      Map<String, Book> bookMap = books.stream()
        .filter(Objects::nonNull)
        .filter(book -> book.getIsbn() != null)
        .collect(Collectors.toMap(
          Book::getIsbn,
          book -> book,
          (existing, replacement) -> replacement
        ));

      hashOps.putAll(BOOK_HASH_KEY, bookMap);

      log.info("Loaded {} books into hash storage", bookMap.size());
    } catch (Exception e) {
      log.error("Failed to load books into hash", e);
      throw new CacheException("Failed to load books", e);
    }
  }

  /**
   * Adds a book to the hash (primary storage).
   *
   * @param book the book to add
   */
  @Override
  public void add(Book book) {
    validateBook(book);

    try {
      hashOps.put(BOOK_HASH_KEY, book.getIsbn(), book);
      log.debug("Added book to hash: {}", book.getIsbn());
    } catch (Exception e) {
      log.error("Failed to add book to hash: {}", book.getIsbn(), e);
      throw new CacheException("Failed to add book", e);
    }
  }

  /**
   * Retrieves all books from the hash.
   *
   * @return list of all books
   */
  @Override
  public List<Book> retrieve() {
    try {
      Map<String, Book> bookMap = hashOps.entries(BOOK_HASH_KEY);

      if (bookMap == null || bookMap.isEmpty()) {
        return Collections.emptyList();
      }

      return new ArrayList<>(bookMap.values());
    } catch (Exception e) {
      log.error("Failed to retrieve books from hash", e);
      return Collections.emptyList();
    }
  }

  /**
   * Retrieves a book by ISBN from the hash.
   *
   * @param isbn the book's ISBN
   * @return the book, or null if not found
   */
  @Override
  public Book retrieveById(String isbn) {
    Objects.requireNonNull(isbn, "ISBN cannot be null");

    try {
      return hashOps.get(BOOK_HASH_KEY, isbn);
    } catch (Exception e) {
      log.error("Failed to retrieve book from hash: {}", isbn, e);
      return null;
    }
  }

  /**
   * Removes a book from the hash by ISBN.
   *
   * @param isbn the book's ISBN
   */
  @Override
  public void removeById(String isbn) {
    Objects.requireNonNull(isbn, "ISBN cannot be null");

    try {
      hashOps.delete(BOOK_HASH_KEY, isbn);
      log.debug("Removed book from hash: {}", isbn);
    } catch (Exception e) {
      log.error("Failed to remove book from hash: {}", isbn, e);
      throw new CacheException("Failed to remove book", e);
    }
  }

  /**
   * Checks if the hash is empty.
   *
   * @return true if empty, false otherwise
   */
  @Override
  public boolean isEmpty() {
    try {
      Long size = hashOps.size(BOOK_HASH_KEY);
      return size == null || size == 0;
    } catch (Exception e) {
      log.error("Failed to check if hash is empty", e);
      return true;
    }
  }

  /**
   * Retrieves books by author from the hash.
   *
   * @param author the author name
   * @return list of books by the author
   */
  public List<Book> retrieveByAuthor(String author) {
    Objects.requireNonNull(author, "Author cannot be null");

    return retrieve().stream()
      .filter(book -> book.getAuthor() != null)
      .filter(book -> book.getAuthor().equalsIgnoreCase(author))
      .collect(Collectors.toList());
  }

  /* =========================================================
   * STRING (Value) Operations
   * ========================================================= */

  public void loadWithValueOps(List<Book> books) {
    redisTemplate.delete(redisTemplate.keys(KEY_PREFIX + "*"));
    books.forEach(book -> valueOps.set(KEY_PREFIX + book.getIsbn(), book));
    log.info("Redis cache loaded with {} books.", books.size());
  }

  public void addWithValueOps(Book book) {
    valueOps.set(KEY_PREFIX + book.getIsbn(), book, 1, TimeUnit.HOURS); // Optional expiration
  }

  public List<Book> retrieveWithValueOps() {
    return redisTemplate
      .keys(KEY_PREFIX + "*")
      .stream()
      .map(valueOps::get)
      .collect(Collectors.toList());
  }

  public Book retrieveByIdWithValueOps(String id) {
    return valueOps.get(KEY_PREFIX + id);

  }

  public void removeByIdWithValueOps(String id) {
    redisTemplate.delete(KEY_PREFIX + id);
  }

  public boolean isEmptyWithValueOps() {
    return redisTemplate.keys(KEY_PREFIX + "*").isEmpty();
  }

  public List<Book> retrieveByAuthorWithValueOps(String author) {
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
