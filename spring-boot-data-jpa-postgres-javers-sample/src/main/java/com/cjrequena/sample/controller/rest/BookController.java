package com.cjrequena.sample.controller.rest;

import com.cjrequena.sample.controller.dto.BookDTO;
import com.cjrequena.sample.controller.dto.audit.AuditShadowDTO;
import com.cjrequena.sample.controller.dto.audit.AuditSnapshotDTO;
import com.cjrequena.sample.controller.exception.NotFoundException;
import com.cjrequena.sample.domain.exception.BookNotFoundException;
import com.cjrequena.sample.domain.mapper.AuditMapper;
import com.cjrequena.sample.domain.mapper.BookMapper;
import com.cjrequena.sample.domain.model.aggregate.Book;
import com.cjrequena.sample.persistence.entity.BookEntity;
import com.cjrequena.sample.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javers.core.Javers;
import org.javers.repository.jql.QueryBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cjrequena.sample.shared.common.util.Constant.VND_SAMPLE_SERVICE_V1;
import static org.springframework.http.HttpHeaders.CACHE_CONTROL;

@RestController
@RequestMapping(
  value = BookController.ENDPOINT,
  headers = {BookController.ACCEPT_VERSION}
)
@RequiredArgsConstructor
@Slf4j
public class BookController {

  public static final String ENDPOINT = "/api/books";
  public static final String ACCEPT_VERSION = "Accept-Version=" + VND_SAMPLE_SERVICE_V1;

  private final BookService bookService;
  private final BookMapper bookMapper;
  private final AuditMapper auditMapper;
  private final Javers javers;

  private HttpHeaders noCacheHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.set(CACHE_CONTROL, "no-store, private, max-age=0");
    return headers;
  }

  @PostMapping
  public ResponseEntity<BookDTO> create(@RequestBody BookDTO bookDTO) {
    Book aggregate = bookMapper.toAggregate(bookDTO);
    Book createdAggregate = bookService.createBook(aggregate);
    BookDTO response = bookMapper.toDTO(createdAggregate);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<BookDTO>> retrieve() {
    List<Book> aggregates = bookService.getAllBooks();
    List<BookDTO> response = bookMapper.toDTOList(aggregates);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<BookDTO> retrieve(@PathVariable Long id) {
    try {
      Book book = bookService.getBookById(id);
      return ResponseEntity.ok(this.bookMapper.toDTO(book));
    } catch (BookNotFoundException ex) {
      throw new NotFoundException(ex.getMessage());
    }
  }

  @GetMapping("/isbn/{isbn}")
  public ResponseEntity<BookDTO> retrieveByIsbn(@PathVariable String isbn) {
    try {
      Book book = bookService.getBookByIsbn(isbn);
      return ResponseEntity.ok(this.bookMapper.toDTO(book));
    } catch (BookNotFoundException ex) {
      throw new NotFoundException(ex.getMessage());
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody BookDTO dto) {
    try {
      bookService.updateBook(id, bookMapper.toAggregate(dto));
      return ResponseEntity.noContent().headers(noCacheHeaders()).build();
    } catch (BookNotFoundException ex) {
      throw new NotFoundException(ex.getMessage());
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    try {
      bookService.deleteBook(id);
      return ResponseEntity.noContent().headers(noCacheHeaders()).build();
    } catch (BookNotFoundException ex) {
      throw new NotFoundException(ex.getMessage());
    }
  }

  // ------------------------------------------------------------
  //  Audit
  // -------------------------------------------------------------
  @GetMapping("/{id}/audit-changes")
  public ResponseEntity<List<AuditSnapshotDTO>> changes(@PathVariable Long id) {
    List<AuditSnapshotDTO> response = javers.findSnapshots(
        QueryBuilder.byInstanceId(id, BookEntity.class).build()
      )
      .stream()
      .map(auditMapper::toSnapshotDTO)
      .toList();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}/audit-history")
  public ResponseEntity<List<AuditShadowDTO<Object>>> history(@PathVariable Long id) {
    var shadows = javers.findShadows(
      QueryBuilder.byInstanceId(id, BookEntity.class).build()
    );

    List<AuditShadowDTO<Object>> response = shadows.stream()
      .map(this.auditMapper::toShadowDTO) // lambda needed for type inference
      .toList();

    return ResponseEntity.ok(response);
  }
}
