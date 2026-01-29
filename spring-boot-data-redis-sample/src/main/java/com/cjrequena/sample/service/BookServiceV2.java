package com.cjrequena.sample.service;

import com.cjrequena.sample.domain.exception.BookNotFoundException;
import com.cjrequena.sample.domain.mapper.BookMapper;
import com.cjrequena.sample.domain.model.Book;
import com.cjrequena.sample.persistence.repository.BookJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceV2 {

  private static final String CACHE_PREFIX = "books";

  private final BookJpaRepository bookJpaRepository;
  private final BookMapper bookMapper;

  // --------------------------
  // CREATE
  // --------------------------

  @Caching(evict = {
    @CacheEvict(value = CACHE_PREFIX, key = "'ALL'"),
    @CacheEvict(value = CACHE_PREFIX, key = "#book.id"),
    @CacheEvict(value = CACHE_PREFIX, key = "'author_' + #book.author")
  })
  public void create(Book book) {
    log.info("Creating book {}", book.getId());
    bookJpaRepository.save(bookMapper.toEntity(book));
  }


  // --------------------------
  // RETRIEVE ALL BOOKS
  // --------------------------

  @Cacheable(value = CACHE_PREFIX, key = "'ALL'")
  public List<Book> retrieve() {
    log.info("Cache MISS → fetching ALL books from DB");
    return bookMapper.toDomain(bookJpaRepository.findAll());
  }


  // --------------------------
  // RETRIEVE BY ID
  // --------------------------

  @Cacheable(value = CACHE_PREFIX, key = "#id")
  public Book retrieveById(String id) throws BookNotFoundException {
    log.info("Cache MISS → fetching book {} from DB", id);
    return bookJpaRepository
      .findById(id)
      .map(bookMapper::toDomain)
      .orElseThrow(() -> new BookNotFoundException("Book not found with Id: " + id));
  }


  // --------------------------
  // RETRIEVE BY AUTHOR
  // --------------------------

  @Cacheable(value = CACHE_PREFIX, key = "'author_' + #author")
  public List<Book> retrieveByAuthor(String author) {
    log.info("Cache MISS → fetching books by author {} from DB", author);

    return bookJpaRepository
      .findByAuthor(author)
      .map(bookMapper::toDomain)
      .orElseGet(Collections::emptyList);
  }


  // --------------------------
  // UPDATE
  // --------------------------

  @Caching(
    put = {
      @CachePut(value = CACHE_PREFIX, key = "#book.id", unless = "#book == null")
    },
    evict = {
      @CacheEvict(value = CACHE_PREFIX, key = "'ALL'"),
      @CacheEvict(value = CACHE_PREFIX, key = "'author_' + #book.author")
    }
  )
  public Book update(Book book) throws BookNotFoundException {
    if (!bookJpaRepository.existsById(book.getId())) {
      throw new BookNotFoundException("Book with Id " + book.getId() + " was not found");
    }

    bookJpaRepository.save(bookMapper.toEntity(book));
    return book; // @CachePut stores this in cache
  }


  // --------------------------
  // DELETE
  // --------------------------

  @Caching(evict = {
    @CacheEvict(value = CACHE_PREFIX, key = "#id"),
    @CacheEvict(value = CACHE_PREFIX, key = "'ALL'"),
    @CacheEvict(value = CACHE_PREFIX, allEntries = false) // optional: catch author caches
  })
  public boolean deleteById(String id) throws BookNotFoundException {

    if (!bookJpaRepository.existsById(id)) {
      throw new BookNotFoundException("Book with Id " + id + " was not found");
    }

    bookJpaRepository.deleteById(id);
    return true;
  }

}
