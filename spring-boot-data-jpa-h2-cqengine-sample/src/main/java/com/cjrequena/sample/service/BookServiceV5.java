package com.cjrequena.sample.service;

import com.cjrequena.sample.domain.exception.BookNotFoundException;
import com.cjrequena.sample.domain.mapper.BookMapper;
import com.cjrequena.sample.domain.model.Book;
import com.cjrequena.sample.persistence.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceV5 {

  private static final String CACHE_PREFIX = "books";

  private final BookRepository bookRepository;
  private final BookMapper bookMapper;

  // --------------------------
  // CREATE
  // --------------------------

  /**
   * New book: save to DB and evict all relevant caches.
   */
  @Caching(evict = {
    @CacheEvict(value = CACHE_PREFIX, key = "'ALL'"),
    @CacheEvict(value = CACHE_PREFIX, key = "#book.isbn"),
    @CacheEvict(value = CACHE_PREFIX, key = "'author_' + #book.author")
  })
  public void create(Book book) {
    log.info("Creating book {}", book.getIsbn());
    bookRepository.save(bookMapper.toEntity(book));
  }


  // --------------------------
  // RETRIEVE ALL BOOKS
  // --------------------------

  @Cacheable(value = CACHE_PREFIX, key = "'ALL'")
  public List<Book> retrieve() {
    log.info("Cache MISS → fetching ALL books from DB");
    return bookMapper.toDomain(bookRepository.findAll());
  }


  // --------------------------
  // RETRIEVE BY ISBN
  // --------------------------

  @Cacheable(value = CACHE_PREFIX, key = "#isbn")
  public Book retrieveById(String isbn) throws BookNotFoundException {
    log.info("Cache MISS → fetching book {} from DB", isbn);
    return bookRepository
      .findById(isbn)
      .map(bookMapper::toDomain)
      .orElseThrow(() -> new BookNotFoundException("Book not found with ISBN: " + isbn));
  }


  // --------------------------
  // RETRIEVE BY AUTHOR
  // --------------------------

  @Cacheable(value = CACHE_PREFIX, key = "'author_' + #author")
  public List<Book> retrieveByAuthor(String author) {
    log.info("Cache MISS → fetching books by author {} from DB", author);

    return bookRepository
      .findByAuthor(author)
      .map(bookMapper::toDomain)
      .orElseGet(Collections::emptyList);
  }


  // --------------------------
  // UPDATE
  // --------------------------

  /**
   * Update DB and update cache entry using @CachePut.
   * Also evicts ALL + author caches.
   */
  @Caching(
    put = {
      @CachePut(value = CACHE_PREFIX, key = "#book.isbn", unless = "#book == null")
    },
    evict = {
      @CacheEvict(value = CACHE_PREFIX, key = "'ALL'"),
      @CacheEvict(value = CACHE_PREFIX, key = "'author_' + #book.author")
    }
  )
  public Book update(Book book) throws BookNotFoundException {
    if (!bookRepository.existsById(book.getIsbn())) {
      throw new BookNotFoundException("Book with ISBN " + book.getIsbn() + " was not found");
    }

    bookRepository.save(bookMapper.toEntity(book));
    return book; // @CachePut stores this in cache
  }


  // --------------------------
  // DELETE
  // --------------------------

  @Caching(evict = {
    @CacheEvict(value = CACHE_PREFIX, key = "#isbn"),
    @CacheEvict(value = CACHE_PREFIX, key = "'ALL'"),
    @CacheEvict(value = CACHE_PREFIX, allEntries = false) // optional: catch author caches
  })
  public boolean deleteByIsbn(String isbn) throws BookNotFoundException {

    if (!bookRepository.existsById(isbn)) {
      throw new BookNotFoundException("Book with ISBN " + isbn + " was not found");
    }

    bookRepository.deleteById(isbn);
    return true;
  }

}
