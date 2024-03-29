package com.cjrequena.sample.web.rest;

import com.cjrequena.sample.model.entity.UserEntity;
import com.cjrequena.sample.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserAPI {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @PostMapping(
    path = "/users",
    produces = {APPLICATION_JSON_VALUE}
  )
  public UserEntity createUser(@RequestBody UserEntity user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.save(user);
  }
}
