package com.cjrequena.sample.api;

import com.cjrequena.sample.service.FooService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.cjrequena.sample.common.Constants.VND_SAMPLE_SERVICE_V1;
import static org.springframework.http.HttpHeaders.CACHE_CONTROL;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping(value = FooAPI.ENDPOINT, headers = {FooAPI.ACCEPT_VERSION})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FooAPI {

  public static final String ENDPOINT = "/foo-service/rest/";
  public static final String ACCEPT_VERSION = "Accept-Version=" + VND_SAMPLE_SERVICE_V1;

  private final FooService fooService;


  @GetMapping(
    path = "/fooes",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<String> fooes() {
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set(CACHE_CONTROL, "no store, private, max-age=0");
    //String response = this.fooService.sayHello();
    String response = this.fooService.sayHello1();
    return new ResponseEntity<>(response, responseHeaders, HttpStatus.OK);
  }

}
