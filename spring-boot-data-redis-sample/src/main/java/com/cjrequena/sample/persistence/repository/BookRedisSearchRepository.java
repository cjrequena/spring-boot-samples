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
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BookRedisSearchRepository {

  private final RedisCommands<String, String> redis;

  /* =========================================================
   * Constants
   * ========================================================= */

  private static final String KEY_PREFIX = "books:search";
  private static final String INDEX_NAME = "idx:books";
  private static final boolean RECREATE_INDEX = false;

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
        .add("year").add("NUMERIC").add("SORTABLE")
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
      return;
    }

    books.forEach(this::save);

    log.info("Loaded {} books into RediSearch", books.size());
  }

  public void save(Book book) {
    validateBook(book);

    Map<String, String> hash = new HashMap<>();
    hash.put("id", book.getId());
    hash.put("title", book.getTitle());
    hash.put("author", book.getAuthor());

    redis.hset(key(book.getId()), hash);
  }

  /* =========================================================
   * Read
   * ========================================================= */

  public Optional<Book> retrieve(String id) {
    Map<String, String> hash = redis.hgetall(key(id));
    return hash.isEmpty() ? Optional.empty() : Optional.of(fromHash(hash));
  }

  /* =========================================================
   * Search
   * ========================================================= */

  public List<Book> search(String query) {
    List<Object> result = redis.dispatch(
      cmd("FT.SEARCH"),
      new ArrayOutput<>(StringCodec.UTF8),
      new CommandArgs<>(StringCodec.UTF8)
        .add(INDEX_NAME)
        .add(query)
    );

    return parseSearchResult(result);
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

  private Book fromHash(Map<String, String> hash) {
    Book book = new Book();
    book.setId(hash.get("id"));
    book.setTitle(hash.get("title"));
    book.setAuthor(hash.get("author"));
    return book;
  }

  @SuppressWarnings("unchecked")
  private List<Book> parseSearchResult(List<Object> result) {
    if (result == null || result.size() < 2) return Collections.emptyList();

    return result.stream()
      .skip(1)
      .filter(e -> e instanceof List)
      .map(e -> {
        List<String> fields = (List<String>) e;
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < fields.size(); i += 2) {
          map.put(fields.get(i), fields.get(i + 1));
        }
        return fromHash(map);
      })
      .collect(Collectors.toList());
  }

  private void validateBook(Book book) {
    Objects.requireNonNull(book);
    Objects.requireNonNull(book.getId());
  }
}
