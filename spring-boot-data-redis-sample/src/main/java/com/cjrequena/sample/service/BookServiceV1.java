package com.cjrequena.sample.service;

import com.cjrequena.sample.domain.exception.BookNotFoundException;
import com.cjrequena.sample.domain.mapper.BookMapper;
import com.cjrequena.sample.domain.model.Book;
import com.cjrequena.sample.persistence.repository.BookRepository;
import com.cjrequena.sample.persistence.repository.cache.BookCacheRedisRepository;
import com.cjrequena.sample.persistence.repository.cache.CacheRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class BookServiceV1 {

  private final BookRepository bookRepository;
  private final BookCacheRedisRepository bookCacheRedisRepository;
  private final BookMapper bookMapper;


  public BookServiceV1(
    BookMapper bookMapper,
    BookRepository bookRepository,
    @Qualifier("bookCacheRedisRepository") CacheRepository<String, Book> bookCacheRedisRepository
  ) {
    this.bookRepository = bookRepository;
    this.bookMapper = bookMapper;
    this.bookCacheRedisRepository = (BookCacheRedisRepository) bookCacheRedisRepository;
  }

  @PostConstruct
  public void loadUpCache() {
    List<Book> books = this.bookMapper.toDomain(bookRepository.findAll());
    bookCacheRedisRepository.load(books);
  }

  public void create(Book book) {
    bookRepository.save(this.bookMapper.toEntity(book));
    bookCacheRedisRepository.add(book); // write-through
  }

  public List<Book> retrieve() {
    if (bookCacheRedisRepository.isEmpty()) {
      loadUpCache(); // recovery logic
    }
    return bookCacheRedisRepository.retrieve();
  }

  public Book retrieveById(String isbn) throws BookNotFoundException {
    Book book = bookCacheRedisRepository.retrieveById(isbn);
    if (book == null) {
      book = bookRepository
        .findById(isbn)
        .map(bookMapper::toDomain)
        .orElseThrow(() -> new BookNotFoundException("Book not found with ISBN: " + isbn));
      if (book != null) {
        bookCacheRedisRepository.add(book); // cache update
      }
    }
    return book;
  }

  public List<Book> retrieveByAuthor(String author) {
    List<Book> books = bookCacheRedisRepository.retrieveByAuthor(author);
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
      bookCacheRedisRepository.removeById(book.getIsbn()); // Cleanly replace in cache
      bookCacheRedisRepository.add(book);
    } else {
      throw new BookNotFoundException("Book with ISBN " + book.getIsbn() + " was not Found");
    }
  }

  public boolean deleteByIsbn(String isbn) throws BookNotFoundException {
    bookCacheRedisRepository.removeById(isbn);
    if (bookRepository.existsById(isbn)) {
      bookRepository.deleteById(isbn);
      return true;
    } else {
      throw new BookNotFoundException("Book with ISBN " + isbn + " was not Found");
    }
  }
}
