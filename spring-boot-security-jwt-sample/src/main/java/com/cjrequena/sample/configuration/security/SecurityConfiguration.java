package com.cjrequena.sample.configuration.security;

import com.cjrequena.sample.security.AccessTokenPrincipalUserDetailsService;
import com.cjrequena.sample.security.JWTApplicationPrincipalAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

  private final JWTApplicationPrincipalAuthenticationFilter jwtApplicationPrincipalAuthenticationFilter;
  private final AccessTokenPrincipalUserDetailsService accessTokenPrincipalUserDetailsService;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
      .csrf(AbstractHttpConfigurer::disable)
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .formLogin(AbstractHttpConfigurer::disable)
      .logout(AbstractHttpConfigurer::disable)
      .httpBasic(Customizer.withDefaults())
      .addFilterBefore(jwtApplicationPrincipalAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
      //.securityMatcher("/foo-service/**")
      .authorizeHttpRequests(registry -> {
        registry.requestMatchers(toH2Console()).permitAll();
        registry.requestMatchers("/foo-service/api/fooes").hasAnyAuthority("authority-1", "authority-2", "authority-x")
          .anyRequest().authenticated();
      })
      .build();
  }

  @Bean
  public AccessTokenPrincipalUserDetailsService accessTokenPrincipalUserDetails() {
    return this.accessTokenPrincipalUserDetailsService;
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(accessTokenPrincipalUserDetails());
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
