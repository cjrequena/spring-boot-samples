package com.cjrequena.sample.domain.mapper;

import com.cjrequena.sample.controller.dto.BookDTO;
import com.cjrequena.sample.domain.model.aggregate.BookAggregate;
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
    BookAggregate toAggregate(BookDTO dto);
    
    // Aggregate to DTO
    BookDTO toDTO(BookAggregate aggregate);
    
    // Entity to Aggregate
    BookAggregate toAggregate(BookEntity entity);
    
    // Aggregate to Entity
    BookEntity toEntity(BookAggregate aggregate);
    
    // List conversions
    List<BookAggregate> toAggregateList(List<BookEntity> entities);
    List<BookDTO> toDTOList(List<BookAggregate> aggregates);
    
    // Update entity from aggregate
    void updateEntityFromAggregate(BookAggregate aggregate, @MappingTarget BookEntity entity);
}
