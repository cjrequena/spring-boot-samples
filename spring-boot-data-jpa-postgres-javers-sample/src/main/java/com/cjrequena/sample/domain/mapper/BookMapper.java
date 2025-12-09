package com.cjrequena.sample.domain.mapper;

import com.cjrequena.sample.controller.dto.BookDTO;
import com.cjrequena.sample.domain.model.aggregate.Book;
import com.cjrequena.sample.persistence.entity.BookEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface BookMapper {
    
    // DTO to Aggregate
    Book toAggregate(BookDTO dto);
    
    // Aggregate to DTO
    BookDTO toDTO(Book aggregate);
    
    // Entity to Aggregate
    Book toAggregate(BookEntity entity);
    
    // Aggregate to Entity
    BookEntity toEntity(Book aggregate);
    
    // List conversions
    List<Book> toAggregateList(List<BookEntity> entities);
    List<BookDTO> toDTOList(List<Book> aggregates);
    
    // Update entity from aggregate
    void updateEntityFromAggregate(Book aggregate, @MappingTarget BookEntity entity);
}
