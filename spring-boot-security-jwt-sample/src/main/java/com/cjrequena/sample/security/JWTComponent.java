package com.cjrequena.sample.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cjrequena.sample.configuration.security.JWTConfigurationProperties;
import com.cjrequena.sample.security.ApplicationPrincipalUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Component
@RequiredArgsConstructor
public class JWTComponent {

  public final String CLAIM_EMAIL = "email";
  public final String CLAIM_ROLES = "roles";
  public final String CLAIM_AUTHORITIES = "authorities";

  private final JWTConfigurationProperties jwtConfigurationProperties;


  public String create(String clientId, Map<String, Object> claims) {
    Instant now = Instant.now();

    return JWT
      .create()
      .withSubject(clientId)
      .withIssuedAt(now)
      .withExpiresAt(now.plus(jwtConfigurationProperties.getTokenDuration()))
      .withPayload(claims)
      .sign(Algorithm.HMAC256(jwtConfigurationProperties.getSecretKey()));
  }

  public DecodedJWT decode(String token) {
    return JWT.require(Algorithm.HMAC256(jwtConfigurationProperties.getSecretKey()))
      .build()
      .verify(token);
  }

  public ApplicationPrincipalUserDetails convertToApplicationPrincipalUserDetails(DecodedJWT decodedJWT) {
    List<SimpleGrantedAuthority> authorities = decodedJWT
      .getClaim(CLAIM_AUTHORITIES)
      .asList(String.class)
      .stream()
      .map(SimpleGrantedAuthority::new)
      .toList();

    List<String> roles = decodedJWT
      .getClaim(CLAIM_ROLES)
      .asList(String.class);

    return ApplicationPrincipalUserDetails.builder()
      .clientId(decodedJWT.getSubject())
      .email(decodedJWT.getClaim(CLAIM_EMAIL).asString())
      .authorities(authorities)
      .roles(roles)
      .build();
  }
  
}
