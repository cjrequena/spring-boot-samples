package com.cjrequena.sample.security;

import com.cjrequena.sample.exception.service.UserNotFoundServiceException;
import com.cjrequena.sample.model.entity.UserEntity;
import com.cjrequena.sample.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AccessTokenPrincipalUserDetailsService implements UserDetailsService {

  private final UserService userService;

  @Override
  public AccessTokenPrincipalUserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
    Optional<UserEntity> userEntityOptional = null;
    try {
      UserEntity userEntity = userService.retrieveUserName(userName).get();
      return AccessTokenPrincipalUserDetails.builder()
        .clientId(userEntity.getUserName())
        .email(userEntity.getEmail())
        .password(userEntity.getPassword())
        .roles(getRoles(userEntity))
        .authorities(getAuthorities(userEntity))
        .build();
    } catch (UserNotFoundServiceException e) {
      throw new UsernameNotFoundException(userName);
    }
  }

  private List<String> getRoles(UserEntity user) {
    return (user.getRoles() != null && !user.getRoles().isEmpty()) ? Arrays.stream(user.getRoles().split(",")).toList() : new ArrayList<>();
  }
  private List<SimpleGrantedAuthority> getAuthorities(UserEntity user) {
    return Arrays.stream(user.getAuthorities().split(","))
      .toList()
      .stream()
      .map(SimpleGrantedAuthority::new)
      .toList();
  }
}
