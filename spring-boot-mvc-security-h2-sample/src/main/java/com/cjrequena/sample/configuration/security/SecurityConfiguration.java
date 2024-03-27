package com.cjrequena.sample.configuration.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
      .authorizeHttpRequests(registry -> {
        //registry.requestMatchers("/home", "/register/**").permitAll();
        registry.requestMatchers(toH2Console()).permitAll();
        registry.requestMatchers("/admin/**").hasRole("ADMIN");
        registry.requestMatchers("/user/**").hasRole("USER");
        registry.anyRequest().authenticated();
      })
      .formLogin(httpSecurityFormLoginConfigurer -> {
        httpSecurityFormLoginConfigurer
          .loginPage("/login")
          .successHandler(new AuthenticationSuccessHandler())
          .permitAll();
      })
      .headers(headersConfigurer -> headersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
      .csrf(AbstractHttpConfigurer::disable)
      .build();
  }

  @Bean
  public UserDetailsService userDetailsService() {
    UserDetails normalUser = User.builder()
      .username("admin")
      .password(passwordEncoder().encode("admin"))
      .roles("ADMIN", "USER")
      .build();
    UserDetails adminUser = User.builder()
      .username("user")
      .password(passwordEncoder().encode("user"))
      .roles("USER")
      .build();
    return new InMemoryUserDetailsManager(normalUser, adminUser);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
