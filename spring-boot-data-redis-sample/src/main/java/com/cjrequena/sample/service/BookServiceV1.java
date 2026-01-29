package com.cjrequena.sample.service;

import com.cjrequena.sample.domain.exception.BookNotFoundException;
import com.cjrequena.sample.domain.mapper.BookMapper;
import com.cjrequena.sample.domain.model.Book;
import com.cjrequena.sample.persistence.repository.BookJpaRepository;
import com.cjrequena.sample.persistence.repository.BookRedisSearchRepository;
import com.cjrequena.sample.persistence.repository.cache.BookCacheRedisHashOpsRepository;
import com.cjrequena.sample.persistence.repository.cache.BookCacheRedisValueOpsRepository;
import com.cjrequena.sample.persistence.repository.cache.CacheRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class BookServiceV1 {

  private final BookJpaRepository bookJpaRepository;
  private final BookCacheRedisHashOpsRepository bookCacheRedisHashOpsRepository;
  private final BookCacheRedisValueOpsRepository bookCacheRedisValueOpsRepository;
  private final BookRedisSearchRepository bookRedisSearchRepository;
  private final BookMapper bookMapper;

  public BookServiceV1(
    BookMapper bookMapper,
    BookJpaRepository bookJpaRepository,
    BookRedisSearchRepository bookRedisSearchRepository,
    @Qualifier("bookCacheRedisHashOpsRepository") CacheRepository<String, Book> bookCacheRedisHashOpsRepository,
    @Qualifier("bookCacheRedisValueOpsRepository") CacheRepository<String, Book> bookCacheRedisValueOpsRepository

  ) {
    this.bookMapper = bookMapper;
    this.bookJpaRepository = bookJpaRepository;
    this.bookRedisSearchRepository = bookRedisSearchRepository;
    this.bookCacheRedisHashOpsRepository = (BookCacheRedisHashOpsRepository) bookCacheRedisHashOpsRepository;
    this.bookCacheRedisValueOpsRepository = (BookCacheRedisValueOpsRepository) bookCacheRedisValueOpsRepository;
  }

  @PostConstruct
  public void loadUpCache() {
    List<Book> books = this.bookMapper.toDomain(bookJpaRepository.findAll());
    bookCacheRedisHashOpsRepository.load(books);
    bookRedisSearchRepository.load(books);
  }

  public void create(Book book) {
    bookJpaRepository.save(this.bookMapper.toEntity(book));
    bookCacheRedisHashOpsRepository.add(book); // write-through
  }

  public List<Book> retrieve() {
    if (bookCacheRedisHashOpsRepository.isEmpty()) {
      loadUpCache(); // recovery logic
    }
    return bookCacheRedisHashOpsRepository.retrieve();
  }

  public Book retrieveById(String id) throws BookNotFoundException {
    Book book = bookCacheRedisHashOpsRepository.retrieveById(id);
    if (book == null) {
      book = bookJpaRepository
        .findById(id)
        .map(bookMapper::toDomain)
        .orElseThrow(() -> new BookNotFoundException("Book not found with Id: " + id));
      if (book != null) {
        bookCacheRedisHashOpsRepository.add(book); // cache update
      }
    }
    return book;
  }

  public List<Book> retrieveByAuthor(String author) {
    List<Book> books = bookCacheRedisHashOpsRepository.retrieveByAuthor(author);
    if (books == null) {
      books = bookJpaRepository
        .findByAuthor(author)
        .map(bookMapper::toDomain)
        .orElseGet(Collections::emptyList);
    }
    return books;
  }

  public void update(Book book) throws BookNotFoundException {
    if (bookJpaRepository.findById(book.getId()).isPresent()) {
      bookJpaRepository.save(bookMapper.toEntity(book));
      bookCacheRedisHashOpsRepository.removeById(book.getId()); // Cleanly replace in cache
      bookCacheRedisHashOpsRepository.add(book);
    } else {
      throw new BookNotFoundException("Book with Id " + book.getId() + " was not Found");
    }
  }

  public boolean deleteById(String id) throws BookNotFoundException {
    bookCacheRedisHashOpsRepository.removeById(id);
    if (bookJpaRepository.existsById(id)) {
      bookJpaRepository.deleteById(id);
      return true;
    } else {
      throw new BookNotFoundException("Book with Id " + id + " was not Found");
    }
  }
}
