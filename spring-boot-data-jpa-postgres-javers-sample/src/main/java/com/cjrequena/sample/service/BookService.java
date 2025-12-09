package com.cjrequena.sample.service;

import com.cjrequena.sample.domain.exception.BookNotFoundException;
import com.cjrequena.sample.domain.mapper.BookMapper;
import com.cjrequena.sample.domain.model.aggregate.BookAggregate;
import com.cjrequena.sample.persistence.entity.BookEntity;
import com.cjrequena.sample.persistence.repository.BookRepository;
import com.cjrequena.sample.shared.common.util.Auditable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BookService {

  private final BookRepository bookRepository;
  private final BookMapper bookMapper;

  @Auditable(action = "CREATE_BOOK")
  public BookAggregate createBook(BookAggregate aggregate) {
    log.info("Creating book: {}", aggregate.getTitle());
    BookEntity entity = bookMapper.toEntity(aggregate);
    BookEntity savedEntity = bookRepository.save(entity);
    return bookMapper.toAggregate(savedEntity);
  }

  @Transactional(readOnly = true)
  public List<BookAggregate> getAllBooks() {
    log.info("Fetching all books");
    List<BookEntity> entities = bookRepository.findAll();
    return bookMapper.toAggregateList(entities);
  }

  @Transactional(readOnly = true)
  public Optional<BookAggregate> getBookById(Long id) {
    log.info("Fetching book by id: {}", id);
    return bookRepository.findById(id)
      .map(bookMapper::toAggregate);
  }

  @Transactional(readOnly = true)
  public Optional<BookAggregate> getBookByIsbn(String isbn) {
    log.info("Fetching book by isbn: {}", isbn);
    return bookRepository.findByIsbn(isbn)
      .map(bookMapper::toAggregate);
  }

  @Auditable(action = "UPDATE_BOOK")
  public BookAggregate updateBook(Long id, BookAggregate aggregate) {
    log.info("Updating book with id: {}", id);
    BookEntity entity = bookRepository
      .findById(id)
      .orElseThrow(() -> new BookNotFoundException("Book " + id + " not found"));

    aggregate.setId(id);
    bookMapper.updateEntityFromAggregate(aggregate, entity);
    BookEntity updatedEntity = bookRepository.save(entity);
    return bookMapper.toAggregate(updatedEntity);
  }

  @Auditable(action = "DELETE_BOOK")
  public BookAggregate deleteBook(Long id) {
    log.info("Deleting book with id: {}", id);
    BookEntity entity = bookRepository
      .findById(id)
      .orElseThrow(() -> new BookNotFoundException("Book " + id + " not found"));
    BookAggregate aggregate = bookMapper.toAggregate(entity);
    bookRepository.delete(entity);
    return aggregate;
  }
}
