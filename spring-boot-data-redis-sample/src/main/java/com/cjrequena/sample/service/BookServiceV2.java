package com.cjrequena.sample.service;

import com.cjrequena.sample.domain.exception.BookNotFoundException;
import com.cjrequena.sample.domain.mapper.BookMapper;
import com.cjrequena.sample.domain.model.Book;
import com.cjrequena.sample.persistence.repository.BookRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookServiceV2 {

  private final BookRepository bookRepository;
  private final BookMapper bookMapper;
  private static final String CACHE_PREFIX = "books:";


  @PostConstruct
  @Cacheable(value = CACHE_PREFIX, key = "'*'")
  public void loadUpCache() {
    List<Book> books = this.bookMapper.toDomain(bookRepository.findAll());
  }

  @CachePut(value = CACHE_PREFIX, key="#book.isbn")
  public void create(Book book) {
    bookRepository.save(this.bookMapper.toEntity(book));
  }

  @Cacheable(value = CACHE_PREFIX, key = "'*'")
  public List<Book> retrieve() {
    return this.bookMapper.toDomain(bookRepository.findAll());
  }

  @Cacheable(value = CACHE_PREFIX, key = "#isbn")
  public Book retrieveById(String isbn) throws BookNotFoundException {
    Book book = bookRepository
        .findById(isbn)
        .map(bookMapper::toDomain)
        .orElseThrow(() -> new BookNotFoundException("Book not found with ISBN: " + isbn));
    return book;
  }

  @Cacheable(value = CACHE_PREFIX, key = "#author")
  public List<Book> retrieveByAuthor(String author) {
    List<Book> books  = bookRepository
        .findByAuthor(author)
        .map(bookMapper::toDomain)
        .orElseGet(Collections::emptyList);
    return books;
  }

  @Caching(evict = {
    @CacheEvict(value = CACHE_PREFIX, key = "'*'"),
    @CacheEvict(value = CACHE_PREFIX, key = "#book.isbn"),
    @CacheEvict(value = CACHE_PREFIX, key = "#book.author")
  })
  public void update(Book book) throws BookNotFoundException {
    if (bookRepository.findById(book.getIsbn()).isPresent()) {
      bookRepository.save(bookMapper.toEntity(book));
    } else {
      throw new BookNotFoundException("Book with ISBN " + book.getIsbn() + " was not Found");
    }
  }

  @Caching(evict = {
    @CacheEvict(value = CACHE_PREFIX, key = "'*'"),
    @CacheEvict(value = CACHE_PREFIX, key = "#book.isbn"),
    @CacheEvict(value = CACHE_PREFIX, key = "#book.author")
  })
  public boolean deleteByIsbn(String isbn) throws BookNotFoundException {
    if (bookRepository.existsById(isbn)) {
      bookRepository.deleteById(isbn);
      return true;
    } else {
      throw new BookNotFoundException("Book with ISBN " + isbn + " was not Found");
    }
  }
}
