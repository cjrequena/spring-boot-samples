package com.cjrequena.sample.configuration.security;

import com.cjrequena.sample.common.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SecurityConfiguration {

  private final CustomUserDetailsService customUserDetailsService;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
      .csrf(AbstractHttpConfigurer::disable)
      .headers(headersConfigurer -> headersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
      .authorizeHttpRequests(registry -> {
        registry.requestMatchers(Constants.WHITELISTED_PATHS).permitAll();
        registry.requestMatchers(toH2Console()).permitAll();
        registry.requestMatchers("/admin/**").hasAuthority("ADMIN");
        registry.requestMatchers("/user/**").hasAuthority("USER");
        registry.anyRequest().authenticated();
      })
      .formLogin(httpSecurityFormLoginConfigurer -> {
        httpSecurityFormLoginConfigurer
          .loginPage("/login")
          .successHandler(new AuthenticationSuccessHandler())
          .permitAll();
      })
      .build();
  }

//    @Bean
//    public UserDetailsService userDetailsService() {
//      UserDetails normalUser = User.builder()
//        .username("admin")
//        .password(passwordEncoder().encode("admin"))
//        .roles("ADMIN", "USER")
//        .build();
//      UserDetails adminUser = User.builder()
//        .username("user")
//        .password(passwordEncoder().encode("user"))
//        .roles("USER")
//        .build();
//      return new InMemoryUserDetailsManager(normalUser, adminUser);
//    }

  @Bean
  public UserDetailsService userDetailsService() {
    return this.customUserDetailsService;
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
