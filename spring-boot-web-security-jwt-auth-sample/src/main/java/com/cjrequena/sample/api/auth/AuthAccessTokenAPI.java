package com.cjrequena.sample.api.auth;

import com.cjrequena.sample.model.dto.AuthAccessTokenDTO;
import com.cjrequena.sample.configuration.security.BasicAuthUserDetails;
import com.cjrequena.sample.service.AuthAccessTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.cjrequena.sample.api.auth.AuthAccessTokenAPI.ACCEPT_VERSION;
import static com.cjrequena.sample.api.auth.AuthAccessTokenAPI.ENDPOINT;
import static com.cjrequena.sample.common.Constants.VND_SAMPLE_SERVICE_V1;
import static org.springframework.http.HttpHeaders.CACHE_CONTROL;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping(value = ENDPOINT, headers = {ACCEPT_VERSION})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthAccessTokenAPI {

  private final AuthAccessTokenService authAccessTokenService;

  public static final String ENDPOINT = "/foo-service/api/auth/token";
  public static final String ACCEPT_VERSION = "Accept-Version=" + VND_SAMPLE_SERVICE_V1;

  @PostMapping(
    //path = "/auth/token",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<AuthAccessTokenDTO> accessToken(@AuthenticationPrincipal BasicAuthUserDetails basicAuthUserDetails) {
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set(CACHE_CONTROL, "no store, private, max-age=0");
    AuthAccessTokenDTO authAccessTokenDTO = authAccessTokenService.generateAccessToken(basicAuthUserDetails);
    return new ResponseEntity<>(authAccessTokenDTO, responseHeaders, HttpStatus.OK);
  }
}
