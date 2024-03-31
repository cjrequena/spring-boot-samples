package com.cjrequena.sample.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.cjrequena.sample.model.dto.AuthAccessTokenDTO;
import com.cjrequena.sample.security.AccessTokenPrincipalUserDetails;
import com.cjrequena.sample.security.JWTComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthAccessTokenService {

  private final JWTComponent jwtComponent;

  public AuthAccessTokenDTO generateAccessToken(AccessTokenPrincipalUserDetails accessTokenPrincipalUserDetails) {
    String clientId = accessTokenPrincipalUserDetails.getClientId();
    Map<String, Object> claims = new HashMap<>();
    claims.put(jwtComponent.CLAIM_EMAIL,accessTokenPrincipalUserDetails.getEmail());
    claims.put(jwtComponent.CLAIM_ROLES, accessTokenPrincipalUserDetails.getRoles());
    claims.put(jwtComponent.CLAIM_AUTHORITIES, accessTokenPrincipalUserDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
    DecodedJWT decodedJWT = jwtComponent.decode(jwtComponent.create(clientId, claims));

    AuthAccessTokenDTO authAccessTokenDTO = new AuthAccessTokenDTO();
    authAccessTokenDTO.setTokenType("Bearer");
    authAccessTokenDTO.setClientId(decodedJWT.getSubject());
    authAccessTokenDTO.setAccessToken(decodedJWT.getToken());
    authAccessTokenDTO.setIssuedAt(decodedJWT.getIssuedAt().getTime());
    authAccessTokenDTO.setExpiresAt(decodedJWT.getExpiresAt().getTime());
    return authAccessTokenDTO;
  }
}
