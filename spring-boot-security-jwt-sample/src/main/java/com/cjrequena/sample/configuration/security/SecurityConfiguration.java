package com.cjrequena.sample.configuration.security;

import com.cjrequena.sample.security.JWTApplicationPrincipalAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
  private final JWTApplicationPrincipalAuthenticationFilter jwtApplicationPrincipalAuthenticationFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
      .csrf(AbstractHttpConfigurer::disable)
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .formLogin(AbstractHttpConfigurer::disable)
      .logout(AbstractHttpConfigurer::disable)
      .httpBasic(Customizer.withDefaults())
      .addFilterBefore(jwtApplicationPrincipalAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
      .securityMatcher("/foo-service/**")
      .authorizeHttpRequests(registry -> registry
        .anyRequest().authenticated()
      )
      .build();
  }
}
