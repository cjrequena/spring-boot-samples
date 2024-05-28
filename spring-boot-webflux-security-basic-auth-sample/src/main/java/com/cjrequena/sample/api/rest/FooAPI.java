package com.cjrequena.sample.api.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.cjrequena.sample.api.rest.FooAPI.ACCEPT_VERSION;
import static com.cjrequena.sample.api.rest.FooAPI.ENDPOINT;
import static com.cjrequena.sample.common.Constants.VND_SAMPLE_SERVICE_V1;
import static org.springframework.http.HttpHeaders.CACHE_CONTROL;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping(value = ENDPOINT, headers = {ACCEPT_VERSION})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FooAPI {

  public static final String ENDPOINT = "/foo-service/api/";
  public static final String ACCEPT_VERSION = "Accept-Version=" + VND_SAMPLE_SERVICE_V1;

  @GetMapping(
    path = "/fooes",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<String> retrieve() {
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set(CACHE_CONTROL, "no store, private, max-age=0");
    return new ResponseEntity<>("Hello Fooes", responseHeaders, HttpStatus.OK);
  }

}
