package com.cjrequena.sample.mapper;

import com.cjrequena.sample.db.entity.FooEntity;
import com.cjrequena.sample.dto.FooDTO;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

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

  FooEntity toEntity(FooDTO dto);

  FooDTO toDTO(FooEntity entity);

}
