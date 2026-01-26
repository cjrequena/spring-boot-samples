package com.cjrequena.sample.controller;

import com.cjrequena.sample.controller.dto.BookDTO;
import com.cjrequena.sample.controller.exception.NotFoundException;
import com.cjrequena.sample.domain.exception.BookNotFoundException;
import com.cjrequena.sample.domain.mapper.BookMapper;
import com.cjrequena.sample.domain.model.Book;
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
public class BookController {

  private final BookService bookService;
  private final BookMapper bookMapper;

  public BookController(BookService bookService, BookMapper bookMapper) {
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
  public BookDTO retrieveByIsbn(@PathVariable String isbn) throws NotFoundException {
    try {
      return this.bookMapper.toDTO(bookService.retrieveById(isbn));
    } catch (BookNotFoundException ex) {
      throw new NotFoundException(ex.getMessage());
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
  public ResponseEntity<Void> update(@PathVariable(value = "isbn") String isbn, @Valid @RequestBody BookDTO dto) throws NotFoundException {
    final Book book = this.bookMapper.toDomain(dto);
    book.setIsbn(isbn);
    try {
      this.bookService.update(book);
    } catch (BookNotFoundException ex) {
      throw new NotFoundException(ex.getMessage());
    }
    //Headers
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set(CACHE_CONTROL, "no store, private, max-age=0");
    return new ResponseEntity<>(responseHeaders, HttpStatus.NO_CONTENT);
  }

  @DeleteMapping("/{isbn}")
  public boolean deleteBook(@PathVariable String isbn) throws NotFoundException {
    try {
      return bookService.deleteByIsbn(isbn);
    } catch (BookNotFoundException ex) {
      throw new NotFoundException(ex.getMessage());
    }
  }
}
