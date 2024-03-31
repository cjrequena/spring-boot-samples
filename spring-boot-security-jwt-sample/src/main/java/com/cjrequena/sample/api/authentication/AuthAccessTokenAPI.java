package com.cjrequena.sample.api.authentication;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.cjrequena.sample.security.JWTComponent;
import com.cjrequena.sample.model.dto.AuthAccessTokenDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cjrequena.sample.api.authentication.AuthAccessTokenAPI.ACCEPT_VERSION;
import static com.cjrequena.sample.api.authentication.AuthAccessTokenAPI.ENDPOINT;
import static com.cjrequena.sample.common.Constants.VND_SAMPLE_SERVICE_V1;
import static org.springframework.http.HttpHeaders.CACHE_CONTROL;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping(value = ENDPOINT, headers = {ACCEPT_VERSION})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthAccessTokenAPI {

  private final JWTComponent jwtComponent;

  public static final String ENDPOINT = "/foo-service/api";
  public static final String ACCEPT_VERSION = "Accept-Version=" + VND_SAMPLE_SERVICE_V1;

  @PostMapping(
    path = "/auth/token",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<AuthAccessTokenDTO> accessToken() {
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set(CACHE_CONTROL, "no store, private, max-age=0");

    String userId = "user-1";
    String keyId = "key-1";

    Map<String, Object> claims = new HashMap<>();
    claims.put("email", "admin@admin.com");
    claims.put("roles", List.of("ADMIN", "USER"));
    claims.put("authorities", List.of("ADMIN", "USER"));

    DecodedJWT decodedJWT = jwtComponent.decode(jwtComponent.create(keyId, userId, claims));

    AuthAccessTokenDTO authAccessTokenDTO = new AuthAccessTokenDTO();
    authAccessTokenDTO.setTokenType("Bearer");
    authAccessTokenDTO.setClientId(decodedJWT.getSubject());
    authAccessTokenDTO.setAccessToken(decodedJWT.getToken());
    authAccessTokenDTO.setIssuedAt(decodedJWT.getIssuedAt().getTime());
    authAccessTokenDTO.setExpiresAt(decodedJWT.getExpiresAt().getTime());
    return new ResponseEntity<>(authAccessTokenDTO, responseHeaders, HttpStatus.OK);
  }
}
