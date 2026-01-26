package com.cjrequena.sample.service;

import com.cjrequena.sample.domain.exception.BookNotFoundException;
import com.cjrequena.sample.domain.mapper.BookMapper;
import com.cjrequena.sample.domain.model.Book;
import com.cjrequena.sample.persistence.repository.BookRepository;
import com.cjrequena.sample.persistence.repository.cache.BookCacheRedisRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
//@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookService {

  private final BookRepository bookRepository;
  private final BookMapper bookMapper;
  private final BookCacheRedisRepository bookCacheRepository;
  //private final CacheRepository<String, Book> bookCacheRepository;

  public BookService(
    BookMapper bookMapper,
    BookRepository bookRepository,
    BookCacheRedisRepository bookCacheRepository
    //@Qualifier("bookCacheRedisRepository") CacheRepository<String, Book> bookCacheRepository
  ) {
    this.bookRepository = bookRepository;
    this.bookMapper = bookMapper;
    this.bookCacheRepository = bookCacheRepository;
    //this.bookCacheRepository = cacheRepository;
  }

  @PostConstruct
  public void loadUpCache() {
    List<Book> books = this.bookMapper.toDomain(bookRepository.findAll());
    bookCacheRepository.load(books);
  }

  public void create(Book book) {
    bookRepository.save(this.bookMapper.toEntity(book));
    bookCacheRepository.add(book); // write-through
  }

  public List<Book> retrieve() {
    if (bookCacheRepository.isEmpty()) {
      loadUpCache(); // recovery logic
    }
    return bookCacheRepository.retrieve();
  }

  public Book retrieveById(String isbn) throws BookNotFoundException {
    Book book = bookCacheRepository.retrieveById(isbn);
    if (book == null) {
      book = bookRepository
        .findById(isbn)
        .map(bookMapper::toDomain)
        .orElseThrow(() -> new BookNotFoundException("Book not found with ISBN: " + isbn));
      if (book != null) {
        bookCacheRepository.add(book); // cache update
      }
    }
    return book;
  }

  public List<Book> retrieveByAuthor(String author) {
    List<Book> books = bookCacheRepository.retrieveByAuthor(author);
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
      bookCacheRepository.removeById(book.getIsbn()); // Cleanly replace in cache
      bookCacheRepository.add(book);
    } else {
      throw new BookNotFoundException("Book with ISBN " + book.getIsbn() + " was not Found");
    }
  }

  public boolean deleteByIsbn(String isbn) throws BookNotFoundException {
    bookCacheRepository.removeById(isbn);
    if (bookRepository.existsById(isbn)) {
      bookRepository.deleteById(isbn);
      return true;
    } else {
      throw new BookNotFoundException("Book with ISBN " + isbn + " was not Found");
    }
  }
}
