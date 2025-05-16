package com.cjrequena.sample.mapper;

import com.cjrequena.sample.domain.Account;
import com.cjrequena.sample.entity.AccountEntity;
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
public interface AccountMapper {

  AccountEntity toEntity(Account account);

  Account toDomain(AccountEntity entity);

}
