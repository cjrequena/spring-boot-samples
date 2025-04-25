package com.cjrequena.sample.service;

import com.cjrequena.sample.domain.Book;
import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.index.hash.HashIndex;
import com.googlecode.cqengine.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.googlecode.cqengine.query.QueryFactory.equal;

@Service
public class BookCacheService {

  private final IndexedCollection<Book> cache = new ConcurrentIndexedCollection<>();

  public BookCacheService() {
    cache.addIndex(HashIndex.onAttribute(Book.ISBN));
    cache.addIndex(HashIndex.onAttribute(Book.TITLE));
    cache.addIndex(HashIndex.onAttribute(Book.AUTHOR));
  }

  public void load(List<Book> books) {
    cache.clear();
    cache.addAll(books);
    System.out.println("Cache loaded with " + books.size() + " books.");
  }

  public void add(Book book) {
    cache.add(book);
  }

  public List<Book> retrieve() {
    return new ArrayList<>(cache);
  }

  public Book retrieveByIsbn(String isbn) {
    Query<Book> retrieveByIsbnQry = equal(Book.ISBN, isbn);
    return cache.retrieve(retrieveByIsbnQry).stream().findFirst().orElse(null);
  }

  public List<Book> retrieveByAuthor(String author) {
    Query<Book> retrieveByAuthorQry = equal(Book.AUTHOR, author);
    return cache.retrieve(retrieveByAuthorQry).stream().collect(Collectors.toList());
  }

  public void removeByIsbn(String isbn) {
    cache.removeIf(book -> book.getIsbn().equals(isbn));
  }

  public boolean isEmpty() {
    return cache.isEmpty();
  }
}
