package com.cjrequena.sample.controller;

import com.cjrequena.sample.controller.dto.BookDTO;
import com.cjrequena.sample.controller.exception.NotFoundException;
import com.cjrequena.sample.domain.exception.BookNotFoundException;
import com.cjrequena.sample.domain.mapper.BookMapper;
import com.cjrequena.sample.domain.model.Book;
import com.cjrequena.sample.service.BookServiceV1;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cjrequena.sample.shared.common.Constant.VND_SAMPLE_SERVICE_V1;
import static org.springframework.http.HttpHeaders.CACHE_CONTROL;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = BookController.ENDPOINT, headers = {BookController.ACCEPT_VERSION})
@Slf4j
public class BookController {

  public static final String ENDPOINT = "/api/books";
  public static final String ACCEPT_VERSION = "Accept-Version=" + VND_SAMPLE_SERVICE_V1;

  private final BookServiceV1 bookServiceV1;
  private final BookMapper bookMapper;

  public BookController(BookServiceV1 bookServiceV1, BookMapper bookMapper) {
    this.bookServiceV1 = bookServiceV1;
    this.bookMapper = bookMapper;
  }

  @PostMapping
  public void create(@RequestBody BookDTO dto) {
    final Book book = this.bookMapper.toDomain(dto);
    bookServiceV1.create(book);
  }

  @GetMapping
  public List<BookDTO> retrieve() {
    return this.bookMapper.toDTO(bookServiceV1.retrieve());
  }

  @GetMapping("/{id}")
  public BookDTO retrieveById(@PathVariable String id) throws NotFoundException {
    try {
      return this.bookMapper.toDTO(bookServiceV1.retrieveById(id));
    } catch (BookNotFoundException ex) {
      throw new NotFoundException(ex.getMessage());
    }
  }

  @PutMapping(
    path = "/{id}",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<Void> update(@PathVariable(value = "id") String id, @Valid @RequestBody BookDTO dto) throws NotFoundException {
    final Book book = this.bookMapper.toDomain(dto);
    book.setId(id);
    try {
      this.bookServiceV1.update(book);
    } catch (BookNotFoundException ex) {
      throw new NotFoundException(ex.getMessage());
    }
    //Headers
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set(CACHE_CONTROL, "no store, private, max-age=0");
    return new ResponseEntity<>(responseHeaders, HttpStatus.NO_CONTENT);
  }

  @DeleteMapping("/{id}")
  public boolean deleteBook(@PathVariable String id) throws NotFoundException {
    try {
      return bookServiceV1.deleteById(id);
    } catch (BookNotFoundException ex) {
      throw new NotFoundException(ex.getMessage());
    }
  }

  @GetMapping("/search")
  public List<Book> autocomplete(@RequestParam("q") String query) {
    return bookServiceV1.search(query);
  }
}
