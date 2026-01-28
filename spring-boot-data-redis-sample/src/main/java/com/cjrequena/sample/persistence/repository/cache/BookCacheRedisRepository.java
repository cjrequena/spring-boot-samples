package com.cjrequena.sample.persistence.repository.cache;

import com.cjrequena.sample.domain.model.Book;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
@Qualifier("bookCacheRedisRepository")
@Log4j2
public class BookCacheRedisRepository implements CacheRepository<String, Book>{

  private static final String KEY_PREFIX = "books:";

  private final RedisTemplate<String, Book> redisTemplate;
  private final ValueOperations<String, Book> valueOps;

  @Autowired
  public BookCacheRedisRepository(RedisTemplate<String, Book> redisTemplate) {
    this.redisTemplate = redisTemplate;
    this.valueOps = redisTemplate.opsForValue();
  }

  private String key(String id) {
    return KEY_PREFIX + id;
  }

  @Override
  public void load(List<Book> books) {
    redisTemplate.delete(redisTemplate.keys(KEY_PREFIX + "*"));
    books.forEach(book -> valueOps.set(key(book.getIsbn()), book));
    log.info("Redis cache loaded with {} books.", books.size());
  }

  @Override
  public void add(Book book) {
    valueOps.set(key(book.getIsbn()), book, 1, TimeUnit.HOURS); // Optional expiration
  }

  @Override
  public List<Book> retrieve() {
    return redisTemplate.keys(KEY_PREFIX + "*").stream()
      .map(valueOps::get)
      .collect(Collectors.toList());
  }

  @Override
  public Book retrieveById(String id) {
    return valueOps.get(key(id));

  }

  @Override
  public void removeById(String id) {
    redisTemplate.delete(key(id));
  }

  @Override
  public boolean isEmpty() {
    return redisTemplate.keys(KEY_PREFIX + "*").isEmpty();
  }

  public List<Book> retrieveByAuthor(String author) {
    return retrieve().stream()
      .filter(book -> book.getAuthor().equalsIgnoreCase(author))
      .collect(Collectors.toList());
  }
}
