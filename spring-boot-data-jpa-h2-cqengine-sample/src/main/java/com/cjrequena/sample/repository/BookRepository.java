package com.cjrequena.sample.repository;

import com.cjrequena.sample.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<BookEntity, String> {

  Optional<List<BookEntity>> findByAuthor(String author);
}
