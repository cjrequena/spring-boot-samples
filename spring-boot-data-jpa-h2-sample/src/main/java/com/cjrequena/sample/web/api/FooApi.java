package com.cjrequena.sample.web.api;

import com.cjrequena.sample.dto.FooDTO;
import com.cjrequena.sample.exception.api.NotFoundApiException;
import com.cjrequena.sample.exception.service.FooNotFoundServiceException;
import com.cjrequena.sample.service.FooService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static org.springframework.http.HttpHeaders.CACHE_CONTROL;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/foo-api")
public class FooApi {

  private FooService fooService;

  @PostMapping(
    path = "/fooes",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<Void> create(
    @Valid @RequestBody FooDTO dto,
    HttpServletRequest request,
    UriComponentsBuilder ucBuilder,
    BindingResult bindingResult) {

    dto = fooService.create(dto);
    URI resourcePath = ucBuilder.path(new StringBuilder().append(request.getServletPath()).append("/{id}").toString()).buildAndExpand(dto.getId()).toUri();
    // Headers
    HttpHeaders headers = new HttpHeaders();
    headers.set(CACHE_CONTROL, "no store, private, max-age=0");
    headers.setLocation(resourcePath);
    return new ResponseEntity<>(headers, HttpStatus.CREATED);

  }

  @GetMapping(
    path = "/fooes/{id}",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<FooDTO> retrieveById(@PathVariable(value = "id") Long id) throws NotFoundApiException {
    try {
      //Headers
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.set(CACHE_CONTROL, "no store, private, max-age=0");
      FooDTO dto = this.fooService.retrieveById(id);
      return new ResponseEntity<>(dto, responseHeaders, HttpStatus.OK);
    } catch (FooNotFoundServiceException ex) {
      throw new NotFoundApiException();
    }
  }

  @GetMapping(
    path = "/fooes",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<List<FooDTO>> retrieve() {

    List<FooDTO> dtoList = this.fooService.retrieve();
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set(CACHE_CONTROL, "no store, private, max-age=0");
    return new ResponseEntity<>(dtoList, responseHeaders, HttpStatus.OK);
  }

  @PutMapping(
    path = "/fooes/{id}",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<Void> update(@PathVariable(value = "id") Long id, @Valid @RequestBody FooDTO dto) throws NotFoundApiException {
    try {
      dto.setId(id);
      this.fooService.update(dto);
      //Headers
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.set(CACHE_CONTROL, "no store, private, max-age=0");
      return new ResponseEntity<>(responseHeaders, HttpStatus.NO_CONTENT);
    } catch (FooNotFoundServiceException ex) {
      throw new NotFoundApiException();
    }
  }

  @DeleteMapping(
    path = "/fooes/{id}",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<Void> delete(@PathVariable(value = "id") Long id) throws NotFoundApiException {
    try {
      //Headers
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.set(CACHE_CONTROL, "no store, private, max-age=0");
      this.fooService.delete(id);
      return new ResponseEntity<>(responseHeaders, HttpStatus.NO_CONTENT);
    } catch (FooNotFoundServiceException ex) {
      throw new NotFoundApiException();
    }
  }

}
