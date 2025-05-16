package com.cjrequena.sample.mapper;

import com.cjrequena.sample.domain.Transaction;
import com.cjrequena.sample.entity.TransactionEntity;
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
public interface TransactionMapper {

  TransactionEntity toEntity(Transaction transaction);

  Transaction toDomain(TransactionEntity entity);

}
