package com.cjrequena.sample.configuration.security;

import com.cjrequena.sample.security.AccessTokenPrincipalUserDetails;
import com.cjrequena.sample.security.ApplicationPrincipalUserDetails;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

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

  @Bean
  public UserDetailsService userDetailsService() {
    AccessTokenPrincipalUserDetails client1 = AccessTokenPrincipalUserDetails.builder()
      .clientId("client-id")
      .password(passwordEncoder().encode("admin"))
      .roles(List.of("ADMIN", "USER"))
      .authorities(List.of("ADMIN", "USER").stream().map(SimpleGrantedAuthority::new).toList())
      .build();
    return new InMemoryUserDetailsManager(client1);
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService());
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
