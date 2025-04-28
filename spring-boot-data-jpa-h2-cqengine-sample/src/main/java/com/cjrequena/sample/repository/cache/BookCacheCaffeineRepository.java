package com.cjrequena.sample.repository.cache;

import com.cjrequena.sample.domain.Book;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Qualifier("bookCacheCaffeineService")
public class BookCacheCaffeineRepository implements CacheRepository<String,Book> {

  private final Cache<String, Book> cache;

  public BookCacheCaffeineRepository() {
    this.cache = Caffeine.newBuilder()
      .expireAfterAccess(1, TimeUnit.HOURS)
      .maximumSize(10_000)
      .build();
  }

  public void load(List<Book> books) {
    cache.invalidateAll(); // Clear cache
    books.forEach(book -> cache.put(book.getIsbn(), book));
    System.out.println("Cache loaded with " + books.size() + " books.");
  }

  public void add(Book book) {
    cache.put(book.getIsbn(), book);
  }

  public List<Book> retrieve() {
    return new ArrayList<>(cache.asMap().values());
  }

  public Book retrieveById(String isbn) {
    return cache.getIfPresent(isbn);
  }

  public void removeById(String isbn) {
    cache.invalidate(isbn);
  }

  public List<Book> retrieveByAuthor(String author) {
    return cache.asMap().values().stream()
      .filter(book -> book.getAuthor().equalsIgnoreCase(author))
      .collect(Collectors.toList());
  }

  public boolean isEmpty() {
    return cache.asMap().isEmpty();
  }
}
