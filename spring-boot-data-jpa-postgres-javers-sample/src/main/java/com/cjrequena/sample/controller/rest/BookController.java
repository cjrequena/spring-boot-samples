package com.cjrequena.sample.controller.rest;

import com.cjrequena.sample.controller.dto.BookDTO;
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
import org.javers.shadow.Shadow;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.cjrequena.sample.shared.common.util.Constant.VND_SAMPLE_SERVICE_V1;

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

  @PostMapping
  public ResponseEntity<BookDTO> createBook(@RequestBody BookDTO bookDTO) {
    Book aggregate = bookMapper.toAggregate(bookDTO);
    Book createdAggregate = bookService.createBook(aggregate);
    BookDTO response = bookMapper.toDTO(createdAggregate);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<BookDTO>> getAllBooks() {
    List<Book> aggregates = bookService.getAllBooks();
    List<BookDTO> response = bookMapper.toDTOList(aggregates);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) {
    try {
      Book book = bookService.getBookById(id);
      return ResponseEntity.ok(this.bookMapper.toDTO(book));
    } catch (BookNotFoundException ex) {
      throw new NotFoundException(ex.getMessage());
    }
  }

  @GetMapping("/isbn/{isbn}")
  public ResponseEntity<BookDTO> getBookByIsbn(@PathVariable String isbn) {
    return bookService.getBookByIsbn(isbn)
      .map(bookMapper::toDTO)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}")
  public ResponseEntity<BookDTO> updateBook(
    @PathVariable Long id,
    @Valid @RequestBody BookDTO bookDTO) {
    log.info("PUT /api/v1/books/{} - Updating book", id);
    Book aggregate = bookMapper.toAggregate(bookDTO);
    Book updatedAggregate = bookService.updateBook(id, aggregate);
    BookDTO response = bookMapper.toDTO(updatedAggregate);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
    log.info("DELETE /api/v1/books/{} - Deleting book", id);
    bookService.deleteBook(id);
    return ResponseEntity.noContent().build();
  }

  // ------------------------------------------------------------
  //  Audit
  // -------------------------------------------------------------
  @GetMapping("/{id}/audit-changes")
  public ResponseEntity<List<AuditSnapshotDTO>> getBookChanges(@PathVariable Long id) {
    List<AuditSnapshotDTO> response = javers.findSnapshots(
        QueryBuilder.byInstanceId(id, BookEntity.class).build()
      )
      .stream()
      .map(auditMapper::toSnapshotDTO)
      .toList();
    return ResponseEntity.ok(response);
  }
}
