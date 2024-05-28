package com.cjrequena.sample.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static com.cjrequena.sample.common.Constants.WHITELISTED_PATHS;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

  @Bean
  SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity serverHttpSecurity) {

    return serverHttpSecurity
      .csrf(ServerHttpSecurity.CsrfSpec::disable)
      .httpBasic(withDefaults())
      .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
      .logout(ServerHttpSecurity.LogoutSpec::disable)
      .authorizeExchange(exchanges ->
        exchanges
          .pathMatchers(WHITELISTED_PATHS)
          .permitAll()
          .anyExchange()
          .authenticated()
      )
      .build();
  }

  @Bean
  public MapReactiveUserDetailsService userDetailsService() {

    UserDetails admin = User
      .withUsername("admin")
      .password(passwordEncoder().encode("admin"))
      .roles("admin")
      .build();

    UserDetails user = User
      .withUsername("user")
      .password(passwordEncoder().encode("user"))
      .roles("user")
      .build();

    return new MapReactiveUserDetailsService(admin, user);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(8);
  }
}
