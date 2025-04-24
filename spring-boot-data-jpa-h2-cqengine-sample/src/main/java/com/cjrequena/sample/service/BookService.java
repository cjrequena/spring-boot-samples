package com.cjrequena.sample.service;

import com.cjrequena.sample.domain.Book;
import com.cjrequena.sample.exception.service.BookNotFoundServiceException;
import com.cjrequena.sample.mapper.BookMapper;
import com.cjrequena.sample.repository.BookRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

  private final BookRepository bookRepository;
  private final BookMapper bookMapper;
  private final BookCacheService bookCacheService;

  public BookService(BookRepository bookRepository, BookMapper bookMapper, BookCacheService bookCacheService) {
    this.bookRepository = bookRepository;
    this.bookMapper = bookMapper;
    this.bookCacheService = bookCacheService;
  }

  @PostConstruct
  public void loadUpCache() {
    List<Book> books = this.bookMapper.toDomain(bookRepository.findAll());
    bookCacheService.load(books);
  }

  public void create(Book book) {
    bookRepository.save(this.bookMapper.toEntity(book));
    bookCacheService.add(book); // write-through
  }

  public List<Book> retrieve() {
    if (bookCacheService.isEmpty()) {
      loadUpCache(); // recovery logic
    }
    return bookCacheService.retrieve();
  }

  public Book retrieveByIsbn(String isbn) {
    Book book = bookCacheService.retrieveByIsbn(isbn);
    if (book == null) {
      book = bookRepository.findById(isbn)
        .map(bookMapper::toDomain)
        .orElse(null);
      if (book != null) {
        bookCacheService.add(book); // cache update
      }
    }
    return book;
  }

  public List<Book> retrieveByAuthor(String author) {
    return bookCacheService.retrieveByAuthor(author);
  }

  public void update(Book book) throws BookNotFoundServiceException {
    if (bookRepository.findById(book.getIsbn()).isPresent()) {
      bookRepository.save(bookMapper.toEntity(book));
      bookCacheService.removeByIsbn(book.getIsbn()); // Cleanly replace in cache
      bookCacheService.add(book);
    } else {
      throw new BookNotFoundServiceException("Book with ISBN " + book.getIsbn() + " was not Found");
    }
  }

  public boolean deleteByIsbn(String isbn) throws BookNotFoundServiceException {
    bookCacheService.removeByIsbn(isbn);
    if (bookRepository.existsById(isbn)) {
      bookRepository.deleteById(isbn);
      return true;
    }else{
      throw new BookNotFoundServiceException("Book with ISBN " + isbn + " was not Found");
    }
  }
}
