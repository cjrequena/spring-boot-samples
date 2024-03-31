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

import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Component
@RequiredArgsConstructor
public class JWTComponent {

  private final JWTConfigurationProperties jwtConfigurationProperties;

  public String create(String keyId, String userId, Map<String, Object> claims) {
    Instant now = Instant.now();

    return JWT
      .create()
      .withKeyId(keyId)
      .withSubject(userId)
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
      .getClaim("authorities")
      .asList(String.class)
      .stream()
      .map(SimpleGrantedAuthority::new)
      .toList();

    List<String> roles = decodedJWT
      .getClaim("roles")
      .asList(String.class);

    return ApplicationPrincipalUserDetails.builder()
      .id(decodedJWT.getSubject())
      .email(decodedJWT.getClaim("email").asString())
      .authorities(authorities)
      .roles(roles)
      .build();
  }
  
}
