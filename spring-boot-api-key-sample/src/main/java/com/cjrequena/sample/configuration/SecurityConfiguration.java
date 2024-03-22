package com.cjrequena.sample.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

  @Autowired
  private SecurityApiKeyAuthenticationFilter securityApiKeyAuthenticationFilter;

  public static final String[] PERMITTED_URL = new String[]{"/management/**"};

  @Bean
  SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity serverHttpSecurity) {

    return serverHttpSecurity
      .csrf(ServerHttpSecurity.CsrfSpec::disable)
      .addFilterBefore(securityApiKeyAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
      .authorizeExchange(exchanges ->
        exchanges
          .pathMatchers(PERMITTED_URL)
          .permitAll()
          .anyExchange()
          .permitAll()
        //.authenticated()
      )
      .build();
  }
}
