package com.cjrequena.sample.service;

import com.cjrequena.sample.exception.service.UserNotFoundServiceException;
import com.cjrequena.sample.model.entity.UserEntity;
import com.cjrequena.sample.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;

  public Optional<UserEntity> retrieveUserName(String userName) throws UserNotFoundServiceException {
    Optional<UserEntity> optional = this.userRepository.findByUserName(userName);
    if (!optional.isPresent()) {
      throw new UserNotFoundServiceException("Foo Not Found");
    }
    return optional;
  }
}
