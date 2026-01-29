package com.cjrequena.sample.persistence.repository;

import com.cjrequena.sample.persistence.entity.BookEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Comprehensive Redis service demonstrating all major Redis data structures
 * and operations using Spring Data Redis.
 *
 * <p>This service provides methods for:
 * <ul>
 *   <li>String operations (basic key-value storage)</li>
 *   <li>Hash operations (field-value pairs)</li>
 *   <li>List operations (ordered collections)</li>
 *   <li>Set operations (unique collections)</li>
 *   <li>Sorted Set operations (scored collections)</li>
 *   <li>Bitmap operations (bit-level data)</li>
 *   <li>HyperLogLog operations (cardinality estimation)</li>
 *   <li>Geospatial operations (location-based queries)</li>
 *   <li>Stream operations (append-only logs)</li>
 *   <li>Pub/Sub operations (messaging)</li>
 *   <li>Transactions and pipelining</li>
 *   <li>Lua scripting</li>
 * </ul>
 *
 * @author cjrequena
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class BookRedisRepository {

  private final RedisTemplate<String, Object> redisTemplate;
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
   * Key helpers & constants
   * ========================================================= */

  /**
   * Generates a Redis key for a specific book by ID.
   *
   * @param id the book's id
   * @return the Redis key (e.g., "book:978-0-123456-78-9")
   */
  private String key(String id) {
    Objects.requireNonNull(id, "ID cannot be null");
    return KEY_PREFIX + id;
  }

  /* =========================================================
   * STRING (Value) Operations
   * ========================================================= */

  /**
   * Saves a book to Redis using a simple key-value structure.
   *
   * @param book the book entity to save
   * @throws IllegalArgumentException if book or Id is null
   */
  public void save(BookEntity book) {
    validateBook(book);
    try {
      redisTemplate.opsForValue().set(key(book.getId()), book);
      log.debug("Saved book with Id: {}", book.getId());
    } catch (Exception e) {
      log.error("Failed to save book with Id: {}", book.getId(), e);
      throw new RedisOperationException("Failed to save book", e);
    }
  }

  /**
   * Saves a book with an expiration time (TTL).
   *
   * @param book the book entity to save
   * @param ttl the time-to-live duration
   * @throws IllegalArgumentException if book, Id, or TTL is null
   */
  public void saveWithTTL(BookEntity book, Duration ttl) {
    validateBook(book);
    Objects.requireNonNull(ttl, "TTL cannot be null");

    try {
      redisTemplate.opsForValue().set(key(book.getId()), book, ttl);
      log.debug("Saved book with Id: {} and TTL: {}", book.getId(), ttl);
    } catch (Exception e) {
      log.error("Failed to save book with TTL for Id: {}", book.getId(), e);
      throw new RedisOperationException("Failed to save book with TTL", e);
    }
  }

  /**
   * Retrieves a book by its Id.
   *
   * @param id the book's Id
   * @return an Optional containing the book if found, empty otherwise
   * @throws IllegalArgumentException if Id is null
   */
  public Optional<BookEntity> retrieve(String id) {
    Objects.requireNonNull(id, "Id cannot be null");

    try {
      BookEntity book = (BookEntity) redisTemplate.opsForValue().get(key(id));
      return Optional.ofNullable(book);
    } catch (Exception e) {
      log.error("Failed to retrieve book with Id: {}", id, e);
      return Optional.empty();
    }
  }

  /**
   * Deletes a book by its Id.
   *
   * @param id the book's Id
   * @return true if the book was deleted, false otherwise
   * @throws IllegalArgumentException if Id is null
   */
  public boolean delete(String id) {
    Objects.requireNonNull(id, "Id cannot be null");

    try {
      Boolean deleted = redisTemplate.delete(key(id));
      boolean result = Boolean.TRUE.equals(deleted);
      log.debug("Delete book with Id: {} - Result: {}", id, result);
      return result;
    } catch (Exception e) {
      log.error("Failed to delete book with Id: {}", id, e);
      return false;
    }
  }

  /**
   * Checks if a book exists in Redis.
   *
   * @param id the book's Id
   * @return true if the book exists, false otherwise
   * @throws IllegalArgumentException if Id is null
   */
  public boolean exists(String id) {
    Objects.requireNonNull(id, "Id cannot be null");

    try {
      return Boolean.TRUE.equals(redisTemplate.hasKey(key(id)));
    } catch (Exception e) {
      log.error("Failed to check existence for Id: {}", id, e);
      return false;
    }
  }

  /* =========================================================
   * HASH Operations
   * ========================================================= */

  /**
   * Saves a book to a Redis hash structure.
   * All books are stored in a single hash with Id as the field name.
   *
   * @param book the book entity to save
   * @throws IllegalArgumentException if book or Id is null
   */
  public void saveOpsForHash(BookEntity book) {
    validateBook(book);

    try {
      redisTemplate.opsForHash().put(BOOK_HASH_KEY, book.getId(), book);
      log.debug("Saved book to hash with Id: {}", book.getId());
    } catch (Exception e) {
      log.error("Failed to save book to hash with Id: {}", book.getId(), e);
      throw new RedisOperationException("Failed to save book to hash", e);
    }
  }

  /**
   * Retrieves a book from the hash by Id.
   *
   * @param id the book's Id
   * @return an Optional containing the book if found, empty otherwise
   * @throws IllegalArgumentException if Id is null
   */
  public Optional<BookEntity> retrieveOpsForHash(String id) {
    Objects.requireNonNull(id, "Id cannot be null");

    try {
      BookEntity book = (BookEntity) redisTemplate.opsForHash().get(BOOK_HASH_KEY, id);
      return Optional.ofNullable(book);
    } catch (Exception e) {
      log.error("Failed to retrieve book from hash with Id: {}", id, e);
      return Optional.empty();
    }
  }

  /**
   * Retrieves all books from the hash.
   *
   * @return a map of Id to BookEntity
   */
  public Map<String, BookEntity> retrieveOpsForHash() {
    try {
      Map<Object, Object> raw = redisTemplate.opsForHash().entries(BOOK_HASH_KEY);

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

  /**
   * Deletes a book from the hash by Id.
   *
   * @param id the book's Id
   * @return the number of fields removed (0 or 1)
   * @throws IllegalArgumentException if Id is null
   */
  public Long deleteOpsForHash(String id) {
    Objects.requireNonNull(id, "Id cannot be null");

    try {
      Long deleted = redisTemplate.opsForHash().delete(BOOK_HASH_KEY, id);
      log.debug("Deleted book from hash with Id: {} - Count: {}", id, deleted);
      return deleted;
    } catch (Exception e) {
      log.error("Failed to delete book from hash with Id: {}", id, e);
      return 0L;
    }
  }

  /* =========================================================
   * LIST Operations
   * ========================================================= */

  /**
   * Pushes a book to the right end of the list (RPUSH).
   *
   * @param book the book entity to push
   * @throws IllegalArgumentException if book is null
   */
  public void pushOpsForList(BookEntity book) {
    Objects.requireNonNull(book, "Book cannot be null");

    try {
      redisTemplate.opsForList().rightPush(BOOK_LIST_KEY, book);
      log.debug("Pushed book to list: {}", book.getId());
    } catch (Exception e) {
      log.error("Failed to push book to list", e);
      throw new RedisOperationException("Failed to push book to list", e);
    }
  }

  /**
   * Retrieves books from the list within the specified range.
   *
   * @param start the starting index (0-based)
   * @param end the ending index (-1 for all)
   * @return a list of books
   */
  public List<BookEntity> retrieveOpsForList(long start, long end) {
    try {
      List<Object> values = redisTemplate.opsForList().range(BOOK_LIST_KEY, start, end);

      if (values == null || values.isEmpty()) {
        return Collections.emptyList();
      }

      return values.stream()
        .map(v -> (BookEntity) v)
        .collect(Collectors.toList());
    } catch (Exception e) {
      log.error("Failed to retrieve books from list", e);
      return Collections.emptyList();
    }
  }

  /**
   * Pops a book from the left end of the list (LPOP).
   *
   * @return an Optional containing the popped book, or empty if list is empty
   */
  public Optional<BookEntity> popOpsForList() {
    try {
      BookEntity book = (BookEntity) redisTemplate.opsForList().leftPop(BOOK_LIST_KEY);
      return Optional.ofNullable(book);
    } catch (Exception e) {
      log.error("Failed to pop book from list", e);
      return Optional.empty();
    }
  }

  /**
   * Gets the size of the book list.
   *
   * @return the number of books in the list
   */
  public long getListSizeOpsForList() {
    try {
      Long size = redisTemplate.opsForList().size(BOOK_LIST_KEY);
      return size != null ? size : 0L;
    } catch (Exception e) {
      log.error("Failed to get list size", e);
      return 0L;
    }
  }

  /* =========================================================
   * SET Operations
   * ========================================================= */

  /**
   * Adds a book to the set (ensures uniqueness).
   *
   * @param book the book entity to add
   * @return the number of elements added (0 if already exists, 1 if new)
   * @throws IllegalArgumentException if book is null
   */
  public Long addOpsForSet(BookEntity book) {
    Objects.requireNonNull(book, "Book cannot be null");

    try {
      Long added = redisTemplate.opsForSet().add(BOOK_SET_KEY, book);
      log.debug("Added book to set: {} - Result: {}", book.getId(), added);
      return added;
    } catch (Exception e) {
      log.error("Failed to add book to set", e);
      throw new RedisOperationException("Failed to add book to set", e);
    }
  }

  /**
   * Retrieves all books from the set.
   *
   * @return a set of books
   */
  public Set<BookEntity> retrieveOpsForSet() {
    try {
      Set<Object> raw = redisTemplate.opsForSet().members(BOOK_SET_KEY);

      if (raw == null || raw.isEmpty()) {
        return Collections.emptySet();
      }

      return raw.stream()
        .map(v -> (BookEntity) v)
        .collect(Collectors.toSet());
    } catch (Exception e) {
      log.error("Failed to retrieve books from set", e);
      return Collections.emptySet();
    }
  }

  /**
   * Checks if a book is a member of the set.
   *
   * @param book the book to check
   * @return true if the book is in the set, false otherwise
   * @throws IllegalArgumentException if book is null
   */
  public boolean isBookInSet(BookEntity book) {
    Objects.requireNonNull(book, "Book cannot be null");

    try {
      return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(BOOK_SET_KEY, book));
    } catch (Exception e) {
      log.error("Failed to check book membership in set", e);
      return false;
    }
  }

  /**
   * Removes a book from the set.
   *
   * @param book the book to remove
   * @return the number of elements removed
   * @throws IllegalArgumentException if book is null
   */
  public Long removeOpsForSet(BookEntity book) {
    Objects.requireNonNull(book, "Book cannot be null");

    try {
      Long removed = redisTemplate.opsForSet().remove(BOOK_SET_KEY, book);
      log.debug("Removed book from set: {} - Count: {}", book.getId(), removed);
      return removed;
    } catch (Exception e) {
      log.error("Failed to remove book from set", e);
      return 0L;
    }
  }

  /* =========================================================
   * SORTED SET (ZSET) Operations
   * ========================================================= */

  /**
   * Adds a book to the sorted set with a score (e.g., rating, popularity).
   *
   * @param book the book entity
   * @param score the score for sorting
   * @return true if added, false if updated
   * @throws IllegalArgumentException if book is null
   */
  public Boolean addOpsForZSet(BookEntity book, double score) {
    Objects.requireNonNull(book, "Book cannot be null");

    try {
      Boolean added = redisTemplate.opsForZSet().add(BOOK_ZSET_KEY, book, score);
      log.debug("Added book to sorted set: {} with score: {}", book.getId(), score);
      return added;
    } catch (Exception e) {
      log.error("Failed to add book to sorted set", e);
      throw new RedisOperationException("Failed to add book to sorted set", e);
    }
  }

  /**
   * Retrieves top-ranked books (highest scores first).
   *
   * @param limit the maximum number of books to return
   * @return a set of books in descending score order
   */
  public Set<BookEntity> retrieveOpsForZSet(int limit) {
    if (limit <= 0) {
      throw new IllegalArgumentException("Limit must be positive");
    }

    try {
      Set<Object> raw = redisTemplate.opsForZSet()
        .reverseRange(BOOK_ZSET_KEY, 0, limit - 1);

      if (raw == null || raw.isEmpty()) {
        return Collections.emptySet();
      }

      return raw.stream()
        .map(v -> (BookEntity) v)
        .collect(Collectors.toCollection(LinkedHashSet::new));
    } catch (Exception e) {
      log.error("Failed to retrieve top books", e);
      return Collections.emptySet();
    }
  }

  /**
   * Gets the score of a specific book in the sorted set.
   *
   * @param book the book to query
   * @return an Optional containing the score, or empty if not found
   * @throws IllegalArgumentException if book is null
   */
  public Optional<Double> getScoreOpsForZSet(BookEntity book) {
    Objects.requireNonNull(book, "Book cannot be null");

    try {
      Double score = redisTemplate.opsForZSet().score(BOOK_ZSET_KEY, book);
      return Optional.ofNullable(score);
    } catch (Exception e) {
      log.error("Failed to get book score", e);
      return Optional.empty();
    }
  }

  /**
   * Gets books within a score range.
   *
   * @param minScore minimum score (inclusive)
   * @param maxScore maximum score (inclusive)
   * @return set of books within the score range
   */
  public Set<BookEntity> getByScoreRangeOpsForZSet(double minScore, double maxScore) {
    try {
      Set<Object> raw = redisTemplate.opsForZSet()
        .rangeByScore(BOOK_ZSET_KEY, minScore, maxScore);

      if (raw == null || raw.isEmpty()) {
        return Collections.emptySet();
      }

      return raw.stream()
        .map(v -> (BookEntity) v)
        .collect(Collectors.toSet());
    } catch (Exception e) {
      log.error("Failed to get books by score range", e);
      return Collections.emptySet();
    }
  }

  /* =========================================================
   * BITMAP Operations
   * ========================================================= */

  /**
   * Marks a book as available or unavailable using a bitmap.
   * Uses the Id hashcode as the bit offset.
   *
   * @param id the book's Id
   * @param available true for available, false for unavailable
   * @throws IllegalArgumentException if Id is null
   */
  public void markBookAvailable(String id, boolean available) {
    Objects.requireNonNull(id, "Id cannot be null");

    try {
      long offset = Math.abs((long) id.hashCode());
      redisTemplate.opsForValue().setBit(BOOK_BITMAP_KEY, offset, available);
      log.debug("Marked book {} as available: {}", id, available);
    } catch (Exception e) {
      log.error("Failed to mark book availability for Id: {}", id, e);
      throw new RedisOperationException("Failed to set book availability", e);
    }
  }

  /**
   * Checks if a book is marked as available in the bitmap.
   *
   * @param id the book's Id
   * @return true if available, false otherwise
   * @throws IllegalArgumentException if Id is null
   */
  public boolean isBookAvailable(String id) {
    Objects.requireNonNull(id, "Id cannot be null");

    try {
      long offset = Math.abs((long) id.hashCode());
      return Boolean.TRUE.equals(
        redisTemplate.opsForValue().getBit(BOOK_BITMAP_KEY, offset)
      );
    } catch (Exception e) {
      log.error("Failed to check book availability for Id: {}", id, e);
      return false;
    }
  }

  /* =========================================================
   * HYPERLOGLOG Operations
   * ========================================================= */

  /**
   * Tracks a book view using HyperLogLog for approximate counting.
   *
   * @param id the book's Id
   * @throws IllegalArgumentException if Id is null
   */
  public void trackBookView(String id) {
    Objects.requireNonNull(id, "Id cannot be null");

    try {
      redisTemplate.opsForHyperLogLog().add(BOOK_HLL_KEY, id);
      log.debug("Tracked view for book: {}", id);
    } catch (Exception e) {
      log.error("Failed to track book view for Id: {}", id, e);
      throw new RedisOperationException("Failed to track book view", e);
    }
  }

  /**
   * Gets the approximate count of unique book views.
   *
   * @return the estimated number of unique books viewed
   */
  public long countUniqueBookViews() {
    try {
      Long count = redisTemplate.opsForHyperLogLog().size(BOOK_HLL_KEY);
      return count != null ? count : 0L;
    } catch (Exception e) {
      log.error("Failed to count unique book views", e);
      return 0L;
    }
  }

  /* =========================================================
   * GEO (Geospatial) Operations
   * ========================================================= */

  /**
   * Adds a book's location to the geospatial index.
   *
   * @param id the book's Id
   * @param longitude the longitude coordinate
   * @param latitude the latitude coordinate
   * @throws IllegalArgumentException if Id is null or coordinates are invalid
   */
  public void addBookLocation(String id, double longitude, double latitude) {
    Objects.requireNonNull(id, "Id cannot be null");
    validateCoordinates(longitude, latitude);

    try {
      Long added = redisTemplate.opsForGeo().add(
        BOOK_GEO_KEY,
        new Point(longitude, latitude),
        id
      );
      log.debug("Added location for book {}: ({}, {}) - Result: {}",
        id, longitude, latitude, added);
    } catch (Exception e) {
      log.error("Failed to add book location for Id: {}", id, e);
      throw new RedisOperationException("Failed to add book location", e);
    }
  }

  /**
   * Calculates the distance between two books.
   *
   * @param id1 the first book's Id
   * @param id2 the second book's Id
   * @return an Optional containing the distance, or empty if not found
   * @throws IllegalArgumentException if either Id is null
   */
  public Optional<Distance> distanceBetweenBooks(String id1, String id2) {
    Objects.requireNonNull(id1, "First Id cannot be null");
    Objects.requireNonNull(id2, "Second Id cannot be null");

    try {
      Distance distance = redisTemplate.opsForGeo()
        .distance(BOOK_GEO_KEY, id1, id2, Metrics.KILOMETERS);
      return Optional.ofNullable(distance);
    } catch (Exception e) {
      log.error("Failed to calculate distance between {} and {}", id1, id2, e);
      return Optional.empty();
    }
  }

  /**
   * Finds books within a radius of a given location.
   *
   * @param lon the longitude of the center point
   * @param lat the latitude of the center point
   * @param radiusKm the search radius in kilometers
   * @return geo results containing nearby books
   * @throws IllegalArgumentException if coordinates are invalid or radius is non-positive
   */
  public GeoResults<RedisGeoCommands.GeoLocation<Object>> findBooksNearby(
    double lon, double lat, double radiusKm) {

    validateCoordinates(lon, lat);
    if (radiusKm <= 0) {
      throw new IllegalArgumentException("Radius must be positive");
    }

    try {
      Circle area = new Circle(
        new Point(lon, lat),
        new Distance(radiusKm, Metrics.KILOMETERS)
      );

      GeoResults<RedisGeoCommands.GeoLocation<Object>> results =
        redisTemplate.opsForGeo().radius(BOOK_GEO_KEY, area);

      log.debug("Found {} books within {} km of ({}, {})",
        results != null ? results.getContent().size() : 0,
        radiusKm, lon, lat);

      return results;
    } catch (Exception e) {
      log.error("Failed to find nearby books", e);
      throw new RedisOperationException("Failed to find nearby books", e);
    }
  }

  /* =========================================================
   * STREAMS Operations
   * ========================================================= */

  /**
   * Publishes a book event to a Redis stream.
   *
   * @param book the book entity
   * @return the record ID of the published event
   * @throws IllegalArgumentException if book is null
   */
  public RecordId publishBookEventToStream(BookEntity book) {
    validateBook(book);

    try {
      Map<String, String> body = new HashMap<>();
      body.put("id", book.getId());
      body.put("title", book.getTitle());
      body.put("author", book.getAuthor());

      RecordId recordId = redisTemplate.opsForStream().add(BOOK_STREAM_KEY, body);
      log.debug("Published book event to stream: {} - RecordId: {}",
        book.getId(), recordId);
      return recordId;
    } catch (Exception e) {
      log.error("Failed to publish book event for Id: {}", book.getId(), e);
      throw new RedisOperationException("Failed to publish book event", e);
    }
  }

  /**
   * Reads book events from the stream.
   *
   * @param count the maximum number of events to read
   * @return a list of stream records
   */
  public List<MapRecord<String, Object, Object>> readBookEvents(int count) {
    if (count <= 0) {
      throw new IllegalArgumentException("Count must be positive");
    }

    try {
      List<MapRecord<String, Object, Object>> records =
        redisTemplate.opsForStream().read(
          StreamReadOptions.empty().count(count),
          StreamOffset.fromStart(BOOK_STREAM_KEY)
        );

      return records != null ? records : Collections.emptyList();
    } catch (Exception e) {
      log.error("Failed to read book events", e);
      return Collections.emptyList();
    }
  }

  /**
   * Reads book events from the stream (default count of 10).
   *
   * @return a list of stream records
   */
  public List<MapRecord<String, Object, Object>> readBookEvents() {
    return readBookEvents(10);
  }

  /* =========================================================
   * PUB/SUB Operations
   * ========================================================= */

  /**
   * Publishes a message to the book pub/sub channel.
   *
   * @param message the message to publish
   * @throws IllegalArgumentException if message is null
   */
  public void publishBookMessage(String message) {
    Objects.requireNonNull(message, "Message cannot be null");

    try {
      redisTemplate.convertAndSend(BOOK_PUBSUB_CHANNEL, message);
      log.debug("Published message to channel: {}", message);
    } catch (Exception e) {
      log.error("Failed to publish message", e);
      throw new RedisOperationException("Failed to publish message", e);
    }
  }

  /* =========================================================
   * TRANSACTIONS
   * ========================================================= */

  /**
   * Saves a book transactionally to multiple data structures.
   * Uses Redis MULTI/EXEC to ensure atomicity.
   *
   * @param book the book entity to save
   * @throws IllegalArgumentException if book is null
   */
  public List<Object> saveBookTransactionally(BookEntity book) {
    validateBook(book);

    try {
      List<Object> result = redisTemplate.execute(new SessionCallback<List<Object>>() {
        @Override
        @SuppressWarnings("unchecked")
        public List<Object> execute(RedisOperations operations) {
          operations.multi();

          operations.opsForValue().set(key(book.getId()), book);
          operations.opsForHash().put(BOOK_HASH_KEY, book.getId(), book);
          operations.opsForSet().add(BOOK_SET_KEY, book);

          return operations.exec();
        }
      });

      log.debug("Saved book transactionally: {}", book.getId());
      return result != null ? result : Collections.emptyList();
    } catch (Exception e) {
      log.error("Failed to save book transactionally for Id: {}", book.getId(), e);
      throw new RedisOperationException("Transaction failed", e);
    }
  }

  /* =========================================================
   * PIPELINING
   * ========================================================= */

  /**
   * Saves multiple books using pipelining for better performance.
   *
   * @param books the list of books to save
   * @return the list of results from the pipeline
   * @throws IllegalArgumentException if books list is null
   */
  @SuppressWarnings("unchecked")
  public List<Object> saveBooksPipelined(List<BookEntity> books) {
    Objects.requireNonNull(books, "Books list cannot be null");

    if (books.isEmpty()) {
      return Collections.emptyList();
    }

    try {
      List<Object> results = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
        var valueSerializer = (org.springframework.data.redis.serializer.RedisSerializer<Object>)
          redisTemplate.getValueSerializer();
        var keySerializer = redisTemplate.getStringSerializer();

        for (BookEntity book : books) {
          if (book != null && book.getId() != null) {
            byte[] key = keySerializer.serialize(key(book.getId()));
            byte[] value = valueSerializer.serialize(book);

            if (key != null && value != null) {
              connection.stringCommands().set(key, value);
            }
          }
        }
        return null;
      });

      log.debug("Saved {} books using pipeline", books.size());
      return results != null ? results : Collections.emptyList();
    } catch (Exception e) {
      log.error("Failed to save books in pipeline", e);
      throw new RedisOperationException("Pipeline operation failed", e);
    }
  }

  /* =========================================================
   * LUA SCRIPTING
   * ========================================================= */

  private static final String LUA_INCREMENT_IF_EXISTS = """
        if redis.call('EXISTS', KEYS[1]) == 1 then
            return redis.call('INCR', KEYS[1])
        else
            return -1
        end
    """;

  /**
   * Increments a counter only if the key exists, using Lua script.
   *
   * @param key the key to increment
   * @return the new value if incremented, -1 if key doesn't exist, null on error
   * @throws IllegalArgumentException if key is null
   */
  public Long incrementIfExists(String key) {
    Objects.requireNonNull(key, "Key cannot be null");

    try {
      DefaultRedisScript<Long> script = new DefaultRedisScript<>();
      script.setScriptText(LUA_INCREMENT_IF_EXISTS);
      script.setResultType(Long.class);

      Long result = redisTemplate.execute(script, List.of(key));
      log.debug("Increment if exists for key {}: {}", key, result);
      return result;
    } catch (Exception e) {
      log.error("Failed to execute Lua script for key: {}", key, e);
      return null;
    }
  }

  /* =========================================================
   * KEY / TTL / SCAN Operations
   * ========================================================= */

  /**
   * Sets an expiration time on a key.
   *
   * @param key the key to expire
   * @param seconds the expiration time in seconds
   * @return true if expiration was set, false otherwise
   * @throws IllegalArgumentException if key is null or seconds is negative
   */
  public boolean expire(String key, long seconds) {
    Objects.requireNonNull(key, "Key cannot be null");
    if (seconds < 0) {
      throw new IllegalArgumentException("Seconds must be non-negative");
    }

    try {
      Boolean result = redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
      return Boolean.TRUE.equals(result);
    } catch (Exception e) {
      log.error("Failed to set expiration for key: {}", key, e);
      return false;
    }
  }

  /**
   * Gets the time-to-live for a key.
   *
   * @param key the key to check
   * @return TTL in seconds, -1 if no expiration, -2 if key doesn't exist
   * @throws IllegalArgumentException if key is null
   */
  public long ttl(String key) {
    Objects.requireNonNull(key, "Key cannot be null");

    try {
      Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
      return ttl != null ? ttl : -2L;
    } catch (Exception e) {
      log.error("Failed to get TTL for key: {}", key, e);
      return -2L;
    }
  }

  /**
   * Scans for keys matching a pattern using SCAN command.
   *
   * @param pattern the pattern to match (e.g., "book:*")
   * @return a set of matching keys
   * @throws IllegalArgumentException if pattern is null
   */
  public Set<String> scanKeys(String pattern) {
    Objects.requireNonNull(pattern, "Pattern cannot be null");

    Set<String> keys = new HashSet<>();

    try {
      redisTemplate.execute((RedisCallback<Void>) connection -> {
        try (Cursor<byte[]> cursor = connection.scan(
          ScanOptions.scanOptions()
            .match(pattern)
            .count(100)
            .build())) {

          while (cursor.hasNext()) {
            byte[] keyBytes = cursor.next();
            String key = redisTemplate.getStringSerializer()
              .deserialize(keyBytes);
            if (key != null) {
              keys.add(key);
            }
          }
        }
        return null;
      });

      log.debug("Scanned keys with pattern '{}': found {}", pattern, keys.size());
    } catch (Exception e) {
      log.error("Failed to scan keys with pattern: {}", pattern, e);
    }

    return keys;
  }

  /* =========================================================
   * DANGEROUS OPERATIONS (use carefully)
   * ========================================================= */

  /**
   * Flushes the current Redis database.
   * WARNING: This will delete all keys in the current database!
   * Should only be used in development/testing environments.
   */
  public void flushDb() {
    log.warn("DANGEROUS OPERATION: Flushing Redis database");

    try {
      redisTemplate.execute((RedisCallback<Void>) connection -> {
        connection.flushDb();
        return null;
      });
      log.info("Redis database flushed");
    } catch (Exception e) {
      log.error("Failed to flush database", e);
      throw new RedisOperationException("Failed to flush database", e);
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

  /**
   * Validates geographic coordinates.
   *
   * @param longitude the longitude (-180 to 180)
   * @param latitude the latitude (-90 to 90)
   * @throws IllegalArgumentException if coordinates are out of range
   */
  private void validateCoordinates(double longitude, double latitude) {
    if (longitude < -180 || longitude > 180) {
      throw new IllegalArgumentException(
        "Longitude must be between -180 and 180");
    }
    if (latitude < -90 || latitude > 90) {
      throw new IllegalArgumentException(
        "Latitude must be between -90 and 90");
    }
  }

  /**
   * Custom exception for Redis operation failures.
   */
  public static class RedisOperationException extends RuntimeException {
    public RedisOperationException(String message) {
      super(message);
    }

    public RedisOperationException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
