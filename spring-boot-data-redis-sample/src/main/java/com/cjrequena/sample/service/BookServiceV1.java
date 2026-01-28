package com.cjrequena.sample.service;

import com.cjrequena.sample.domain.exception.BookNotFoundException;
import com.cjrequena.sample.domain.mapper.BookMapper;
import com.cjrequena.sample.domain.model.Book;
import com.cjrequena.sample.persistence.repository.BookRepository;
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

  private final BookRepository bookRepository;
  private final BookCacheRedisHashOpsRepository bookCacheRedisHashOpsRepository;
  private final BookCacheRedisValueOpsRepository bookCacheRedisValueOpsRepository;
  private final BookMapper bookMapper;

  public BookServiceV1(
    BookMapper bookMapper,
    BookRepository bookRepository,
    @Qualifier("bookCacheRedisHashOpsRepository") CacheRepository<String, Book> bookCacheRedisHashOpsRepository,
    @Qualifier("bookCacheRedisValueOpsRepository") CacheRepository<String, Book> bookCacheRedisValueOpsRepository

  ) {
    this.bookRepository = bookRepository;
    this.bookMapper = bookMapper;
    this.bookCacheRedisHashOpsRepository = (BookCacheRedisHashOpsRepository) bookCacheRedisHashOpsRepository;
    this.bookCacheRedisValueOpsRepository = (BookCacheRedisValueOpsRepository) bookCacheRedisValueOpsRepository;
  }

  @PostConstruct
  public void loadUpCache() {
    List<Book> books = this.bookMapper.toDomain(bookRepository.findAll());
    bookCacheRedisHashOpsRepository.load(books);
  }

  public void create(Book book) {
    bookRepository.save(this.bookMapper.toEntity(book));
    bookCacheRedisHashOpsRepository.add(book); // write-through
  }

  public List<Book> retrieve() {
    if (bookCacheRedisHashOpsRepository.isEmpty()) {
      loadUpCache(); // recovery logic
    }
    return bookCacheRedisHashOpsRepository.retrieve();
  }

  public Book retrieveById(String isbn) throws BookNotFoundException {
    Book book = bookCacheRedisHashOpsRepository.retrieveById(isbn);
    if (book == null) {
      book = bookRepository
        .findById(isbn)
        .map(bookMapper::toDomain)
        .orElseThrow(() -> new BookNotFoundException("Book not found with ISBN: " + isbn));
      if (book != null) {
        bookCacheRedisHashOpsRepository.add(book); // cache update
      }
    }
    return book;
  }

  public List<Book> retrieveByAuthor(String author) {
    List<Book> books = bookCacheRedisHashOpsRepository.retrieveByAuthor(author);
    if (books == null) {
      books = bookRepository
        .findByAuthor(author)
        .map(bookMapper::toDomain)
        .orElseGet(Collections::emptyList);
    }
    return books;
  }

  public void update(Book book) throws BookNotFoundException {
    if (bookRepository.findById(book.getIsbn()).isPresent()) {
      bookRepository.save(bookMapper.toEntity(book));
      bookCacheRedisHashOpsRepository.removeById(book.getIsbn()); // Cleanly replace in cache
      bookCacheRedisHashOpsRepository.add(book);
    } else {
      throw new BookNotFoundException("Book with ISBN " + book.getIsbn() + " was not Found");
    }
  }

  public boolean deleteByIsbn(String isbn) throws BookNotFoundException {
    bookCacheRedisHashOpsRepository.removeById(isbn);
    if (bookRepository.existsById(isbn)) {
      bookRepository.deleteById(isbn);
      return true;
    } else {
      throw new BookNotFoundException("Book with ISBN " + isbn + " was not Found");
    }
  }
}
