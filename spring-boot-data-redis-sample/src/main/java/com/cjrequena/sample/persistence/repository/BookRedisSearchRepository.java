package com.cjrequena.sample.persistence.repository;

import com.cjrequena.sample.domain.model.Book;
import io.lettuce.core.RedisCommandExecutionException;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.output.ArrayOutput;
import io.lettuce.core.output.StatusOutput;
import io.lettuce.core.protocol.CommandArgs;
import io.lettuce.core.protocol.ProtocolKeyword;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BookRedisSearchRepository {

  private final RedisCommands<String, String> redis;

  /* =========================================================
   * Constants
   * ========================================================= */

  private static final String KEY_PREFIX = "books:search:";
  private static final String INDEX_NAME = "idx:books";
  private static final boolean RECREATE_INDEX = true;

  /* =========================================================
   * RediSearch keyword helper
   * ========================================================= */

  private static ProtocolKeyword cmd(String name) {
    return () -> name.getBytes(StandardCharsets.UTF_8);
  }

  /* =========================================================
   * Index initialization
   * ========================================================= */

  @PostConstruct
  public void createIndexIfNotExists() {
    if (RECREATE_INDEX) {
      dropIndex();
      createIndex();
      return;
    }

    try {
      // FT.INFO returns an ARRAY → must use ArrayOutput
      redis.dispatch(
        cmd("FT.INFO"),
        new ArrayOutput<>(StringCodec.UTF8),
        new CommandArgs<>(StringCodec.UTF8).add(INDEX_NAME)
      );
      log.info("RediSearch index '{}' already exists", INDEX_NAME);
    } catch (RedisCommandExecutionException e) {
      log.info("RediSearch index '{}' not found, creating it", INDEX_NAME);
      createIndex();
    }
  }

  private void createIndex() {
    redis.dispatch(
      cmd("FT.CREATE"),
      new StatusOutput<>(StringCodec.UTF8),
      new CommandArgs<>(StringCodec.UTF8)
        .add(INDEX_NAME)
        .add("ON").add("HASH")
        .add("PREFIX").add("1").add(KEY_PREFIX)
        .add("SCHEMA")
        .add("id").add("TEXT")
        .add("title").add("TEXT").add("WEIGHT").add("5.0")
        .add("author").add("TEXT")
    );

    log.info("RediSearch index '{}' created", INDEX_NAME);
  }

  private void dropIndex() {
    try {
      redis.dispatch(
        cmd("FT.DROPINDEX"),
        new StatusOutput<>(StringCodec.UTF8),
        new CommandArgs<>(StringCodec.UTF8)
          .add(INDEX_NAME)
          .add("DD")
      );
      log.info("RediSearch index '{}' dropped", INDEX_NAME);
    } catch (Exception ignored) {
    }
  }

  /* =========================================================
   * Write
   * ========================================================= */

  public void load(List<Book> books) {
    if (books == null || books.isEmpty()) {
      log.warn("Attempted to load empty book list");
      return;
    }

    int successCount = 0;
    for (Book book : books) {
      try {
        save(book);
        successCount++;
      } catch (Exception e) {
        log.error("Failed to save book to search index: {}", book.getId(), e);
      }
    }

    log.info("Loaded {}/{} books into RediSearch index", successCount, books.size());
  }

  public void save(Book book) {
    validateBook(book);

    Map<String, String> hash = new HashMap<>();
    hash.put("id", book.getId());
    hash.put("title", book.getTitle() != null ? book.getTitle() : "");
    hash.put("author", book.getAuthor() != null ? book.getAuthor() : "");

    redis.hset(key(book.getId()), hash);
    log.debug("Saved book to search index: {}", book.getId());
  }

  /* =========================================================
   * Read
   * ========================================================= */

  public Optional<Book> retrieve(String id) {
    Map<String, String> hash = redis.hgetall(key(id));
    return hash.isEmpty() ? Optional.empty() : Optional.of(mapFromHash(hash));
  }

  /* =========================================================
   * Search
   * ========================================================= */

  public List<Book> search(String query) {
    if (query == null || query.trim().isEmpty()) {
      return Collections.emptyList();
    }

    try {
      List<Object> result = redis.dispatch(
        cmd("FT.SEARCH"),
        new ArrayOutput<>(StringCodec.UTF8),
        new CommandArgs<>(StringCodec.UTF8)
          .add(INDEX_NAME)
          .add(query)
          .add("LIMIT").add("0").add("50") // Return top 50 results
      );

      return parseSearchResult(result);
    } catch (Exception e) {
      log.error("Full-text search failed for query: {}", query, e);
      return Collections.emptyList();
    }
  }

  /* =========================================================
   * Delete
   * ========================================================= */

  public boolean delete(String id) {
    return redis.del(key(id)) > 0;
  }

  /* =========================================================
   * Helpers
   * ========================================================= */

  private String key(String id) {
    return KEY_PREFIX + id;
  }

  private Book mapFromHash(Map<String, String> hash) {
    Book book = new Book();
    book.setId(hash.get("id"));
    book.setTitle(hash.get("title"));
    book.setAuthor(hash.get("author"));
    return book;
  }

  @SuppressWarnings("unchecked")
  private Book mapFromResult(List<?> result) {
    for (int i = 0; i < result.size() - 1; i++) {
      if (!"extra_attributes".equals(result.get(i))) {
        continue;
      }

      Object attrsObj = result.get(i + 1);
      if (!(attrsObj instanceof List<?> attrs)) {
        return null;
      }

      Book book = new Book();

      for (int j = 0; j < attrs.size() - 1; j += 2) {
        String key = String.valueOf(attrs.get(j));
        String value = String.valueOf(attrs.get(j + 1));

        switch (key) {
          case "id" -> book.setId(value);
          case "author" -> book.setAuthor(value);
          case "title" -> book.setTitle(value);
        }
      }

      return book;
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  private List<Book> parseSearchResult(List<Object> searchResult) {
    if (searchResult == null || searchResult.isEmpty()) {
      return Collections.emptyList();
    }

    List<Book> books = new ArrayList<>();

    for (int i = 0; i < searchResult.size() - 1; i++) {
      if (!"results".equals(searchResult.get(i))) {
        continue;
      }

      Object resultsObj = searchResult.get(i + 1);
      if (!(resultsObj instanceof List<?> results)) {
        continue;
      }

      for (Object resultObj : results) {
        if (!(resultObj instanceof List<?> result)) {
          continue;
        }

        Book book = mapFromResult(result);
        if (book != null) {
          books.add(book);
        }
      }
    }

    log.debug("Parsed {} books from search result", books.size());
    return books;
  }

  private void validateBook(Book book) {
    Objects.requireNonNull(book);
    Objects.requireNonNull(book.getId());
  }
}
