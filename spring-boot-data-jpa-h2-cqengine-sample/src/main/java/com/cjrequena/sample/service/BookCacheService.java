package com.cjrequena.sample.service;

import com.cjrequena.sample.domain.Book;

import java.util.List;

public interface BookCacheService  {

  void load(List<Book> books);

  void add(Book book);

  List<Book> retrieve();

  Book retrieveByIsbn(String isbn);

  List<Book> retrieveByAuthor(String author);

  void removeByIsbn(String isbn);

  boolean isEmpty();
}
