package com.cjrequena.sample.api.rest;

import com.cjrequena.sample.domain.Book;
import com.cjrequena.sample.dto.BookDTO;
import com.cjrequena.sample.exception.api.NotFoundApiException;
import com.cjrequena.sample.exception.service.BookNotFoundServiceException;
import com.cjrequena.sample.mapper.BookMapper;
import com.cjrequena.sample.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpHeaders.CACHE_CONTROL;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/books")
public class BookAPI {

  private final BookService bookService;
  private final BookMapper bookMapper;

  public BookAPI(BookService bookService, BookMapper bookMapper) {
    this.bookService = bookService;
    this.bookMapper = bookMapper;
  }

  @PostMapping
  public void create(@RequestBody BookDTO dto) {
    final Book book = this.bookMapper.toDomain(dto);
    bookService.create(book);
  }

  @GetMapping
  public List<BookDTO> retrieve() {
    return this.bookMapper.toDTO(bookService.retrieve());
  }

  @GetMapping("/{isbn}")
  public BookDTO retrieveByIsbn(@PathVariable String isbn) throws NotFoundApiException {
    try {
      return this.bookMapper.toDTO(bookService.retrieveById(isbn));
    } catch (BookNotFoundServiceException ex) {
      throw new NotFoundApiException(ex.getMessage());
    }
  }

  @GetMapping("/search")
  public List<BookDTO> retrieveByAuthor(@RequestParam String author) {
    return this.bookMapper.toDTO(bookService.retrieveByAuthor(author));
  }

  @PutMapping(
    path = "/{isbn}",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<Void> update(@PathVariable(value = "isbn") String isbn, @Valid @RequestBody BookDTO dto) throws NotFoundApiException {
    final Book book = this.bookMapper.toDomain(dto);
    book.setIsbn(isbn);
    try {
      this.bookService.update(book);
    } catch (BookNotFoundServiceException ex) {
      throw new NotFoundApiException(ex.getMessage());
    }
    //Headers
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set(CACHE_CONTROL, "no store, private, max-age=0");
    return new ResponseEntity<>(responseHeaders, HttpStatus.NO_CONTENT);
  }

  @DeleteMapping("/{isbn}")
  public boolean deleteBook(@PathVariable String isbn) throws NotFoundApiException {
    try {
      return bookService.deleteByIsbn(isbn);
    } catch (BookNotFoundServiceException ex) {
      throw new NotFoundApiException(ex.getMessage());
    }
  }
}
