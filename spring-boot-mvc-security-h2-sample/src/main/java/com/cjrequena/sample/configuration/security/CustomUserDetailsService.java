package com.cjrequena.sample.configuration.security;

import com.cjrequena.sample.model.entity.UserEntity;
import com.cjrequena.sample.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
    Optional<UserEntity> userEntityOptional = userRepository.findByUserName(userName);
    if (userEntityOptional.isPresent()) {
      var userEntity = userEntityOptional.get();
      return User.builder()
        .username(userEntity.getUserName())
        .password(userEntity.getPassword())
        .roles(getRoles(userEntity))
        .build();
    } else {
      throw new UsernameNotFoundException(userName);
    }
  }

  private String[] getRoles(UserEntity user) {
    return (user.getRoles() != null && !user.getRoles().isEmpty()) ? user.getRoles().split(",") : new String[] {"USER"};
  }
}
