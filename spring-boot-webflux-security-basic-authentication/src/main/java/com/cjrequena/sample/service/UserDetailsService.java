package com.cjrequena.sample.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserDetailsService implements ReactiveUserDetailsService {

  @Autowired
  PasswordEncoder passwordEncoder;

  @Override
  public Mono<UserDetails> findByUsername(String username) {
    UserDetails userDetails = User
      .withUsername("admin")
      .password(passwordEncoder.encode("admin"))
      .roles("admin")
      .build();
    Mono<UserDetails> just = Mono.just(userDetails);
    return just;
  }
}
