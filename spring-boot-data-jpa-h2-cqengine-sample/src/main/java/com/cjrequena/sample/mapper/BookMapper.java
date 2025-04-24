package com.cjrequena.sample.mapper;

import com.cjrequena.sample.domain.Book;
import com.cjrequena.sample.dto.BookDTO;
import com.cjrequena.sample.entity.BookEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

import java.util.List;

@Mapper(
  componentModel = "spring",
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface BookMapper {

  BookDTO toDTO(Book domain);

  List<BookDTO> toDTO(List<Book> domains);

  Book toDomain(BookDTO dto);

  Book toDomain(BookEntity entity);

  List<Book> toDomain(List<BookEntity> entities);

  BookEntity toEntity(Book domain);

  List<BookEntity> toEntity(List<Book> books);
}
