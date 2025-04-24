package com.cjrequena.sample.repository;

import com.cjrequena.sample.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<BookEntity, String> {
}
