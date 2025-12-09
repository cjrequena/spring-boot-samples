package com.cjrequena.sample.controller.rest;

import com.cjrequena.sample.controller.dto.BookDTO;
import com.cjrequena.sample.domain.mapper.BookMapper;
import com.cjrequena.sample.domain.model.aggregate.Book;
import com.cjrequena.sample.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    
    @PostMapping
    public ResponseEntity<BookDTO> createBook(@RequestBody BookDTO bookDTO) {
        log.info("POST /api/v1/books - Creating book: {}", bookDTO.getTitle());
        Book aggregate = bookMapper.toAggregate(bookDTO);
        Book createdAggregate = bookService.createBook(aggregate);
        BookDTO response = bookMapper.toDTO(createdAggregate);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        log.info("GET /api/v1/books - Fetching all books");
        List<Book> aggregates = bookService.getAllBooks();
        List<BookDTO> response = bookMapper.toDTOList(aggregates);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) {
        log.info("GET /api/v1/books/{} - Fetching book by id", id);
        return bookService.getBookById(id)
            .map(bookMapper::toDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookDTO> getBookByIsbn(@PathVariable String isbn) {
        log.info("GET /api/v1/books/isbn/{} - Fetching book by isbn", isbn);
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
}
