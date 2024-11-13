package com.cjrequena.sample.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.cjrequena.sample.configuration.security.AuthUserDetails;
import com.cjrequena.sample.configuration.security.JWTComponent;
import com.cjrequena.sample.exception.service.UserNotFoundServiceException;
import com.cjrequena.sample.model.dto.AuthAccessTokenDTO;
import com.cjrequena.sample.model.entity.UserEntity;
import com.cjrequena.sample.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserService {
  private final UserRepository userRepository;
  private final JWTComponent jwtComponent;

  public Optional<UserEntity> retrieveUser(String userName) throws UserNotFoundServiceException {
    Optional<UserEntity> optional = this.userRepository.findByUserName(userName);
    if (optional.isEmpty()) {
      throw new UserNotFoundServiceException("Foo Not Found");
    }
    return optional;
  }

  public AuthAccessTokenDTO generateAccessToken(AuthUserDetails authUserDetails) {
    String userName = authUserDetails.getUsername();
    Map<String, Object> claims = new HashMap<>();
    claims.put(jwtComponent.CLAIM_USER_ID, authUserDetails.getUserId());
    claims.put(jwtComponent.CLAIM_USER_NAME, authUserDetails.getUsername());
    claims.put(jwtComponent.CLAIM_EMAIL, authUserDetails.getEmail());
    claims.put(jwtComponent.CLAIM_ROLES, authUserDetails.getRoles());
    claims.put(jwtComponent.CLAIM_AUTHORITIES, authUserDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
    DecodedJWT decodedJWT = jwtComponent.decode(jwtComponent.create(userName, claims));

    AuthAccessTokenDTO authAccessTokenDTO = new AuthAccessTokenDTO();
    authAccessTokenDTO.setTokenType("Bearer");
    authAccessTokenDTO.setClientId(decodedJWT.getSubject());
    authAccessTokenDTO.setAccessToken(decodedJWT.getToken());
    authAccessTokenDTO.setIssuedAt(decodedJWT.getIssuedAt().getTime());
    authAccessTokenDTO.setExpiresAt(decodedJWT.getExpiresAt().getTime());
    return authAccessTokenDTO;
  }
}
