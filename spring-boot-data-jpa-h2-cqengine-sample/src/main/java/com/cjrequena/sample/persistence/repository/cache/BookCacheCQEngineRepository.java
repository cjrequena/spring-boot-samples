package com.cjrequena.sample.persistence.repository.cache;

import com.cjrequena.sample.domain.model.Book;
import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.index.hash.HashIndex;
import com.googlecode.cqengine.query.Query;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.googlecode.cqengine.query.QueryFactory.equal;

@Repository
@Qualifier("bookCacheCQEngineService")
@Primary
@Log4j2
public class BookCacheCQEngineRepository implements CacheRepository<String, Book> {

  private final IndexedCollection<Book> cache = new ConcurrentIndexedCollection<>();

  public BookCacheCQEngineRepository() {
    cache.addIndex(HashIndex.onAttribute(Book.ISBN));
    cache.addIndex(HashIndex.onAttribute(Book.TITLE));
    cache.addIndex(HashIndex.onAttribute(Book.AUTHOR));
  }

  public void load(List<Book> books) {
    cache.clear();
    cache.addAll(books);
    log.info("CQEngine cache loaded with {} books.", books.size());

  }

  public void add(Book book) {
    cache.add(book);
  }

  public List<Book> retrieve() {
    return new ArrayList<>(cache);
  }

  public Book retrieveById(String isbn) {
    Query<Book> retrieveByIsbnQry = equal(Book.ISBN, isbn);
    return cache.retrieve(retrieveByIsbnQry).stream().findFirst().orElse(null);
  }

  public void removeById(String isbn) {
    cache.removeIf(book -> book.getIsbn().equals(isbn));
  }

  public List<Book> retrieveByAuthor(String author) {
    Query<Book> retrieveByAuthorQry = equal(Book.AUTHOR, author);
    return cache.retrieve(retrieveByAuthorQry).stream().collect(Collectors.toList());
  }

  public boolean isEmpty() {
    return cache.isEmpty();
  }
}
