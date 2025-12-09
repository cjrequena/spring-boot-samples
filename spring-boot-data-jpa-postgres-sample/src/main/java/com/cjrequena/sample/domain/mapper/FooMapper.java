package com.cjrequena.sample.domain.mapper;

import com.cjrequena.sample.controller.dto.FooDTO;
import com.cjrequena.sample.domain.model.Foo;
import com.cjrequena.sample.persistence.entity.FooEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

import java.util.List;

/**
 * <p>
 * <p>
 * <p>
 * <p>
 * @author cjrequena
 */
@Mapper(
  componentModel = "spring",
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface FooMapper {

  FooEntity toEntity(Foo domain);
  Foo toDomain(FooEntity entity);

  FooDTO toDTO(Foo domain);
  Foo toDomain(FooDTO dto);

  List<FooDTO> toDtoList(List<Foo> fooList);
}
