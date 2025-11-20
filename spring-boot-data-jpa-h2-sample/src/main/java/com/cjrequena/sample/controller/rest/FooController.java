package com.cjrequena.sample.controller.rest;

import com.cjrequena.sample.controller.dto.FooDTO;
import com.cjrequena.sample.controller.exception.NotFoundException;
import com.cjrequena.sample.domain.exception.FooNotFoundException;
import com.cjrequena.sample.domain.mapper.FooMapper;
import com.cjrequena.sample.service.FooService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static com.cjrequena.sample.shared.common.Constant.VND_SAMPLE_SERVICE_V1;
import static org.springframework.http.HttpHeaders.CACHE_CONTROL;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping(
  value = FooController.ENDPOINT,
  headers = {FooController.ACCEPT_VERSION}
)
@RequiredArgsConstructor
public class FooController {

  public static final String ENDPOINT = "/foo-service/api";
  public static final String ACCEPT_VERSION = "Accept-Version=" + VND_SAMPLE_SERVICE_V1;

  private final FooService fooService;
  private final FooMapper fooMapper;

  private HttpHeaders noCacheHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.set(CACHE_CONTROL, "no-store, private, max-age=0");
    return headers;
  }

  @Operation(summary = "Create a new Foo")
  @PostMapping(path = "/fooes", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> create(
    @Valid @RequestBody FooDTO dto,
    ServerHttpRequest request,
    UriComponentsBuilder ucBuilder
  ) {

    var created = fooService.create(fooMapper.toDomain(dto));

    URI location = ucBuilder
      .path(request.getPath() + "/{id}")
      .buildAndExpand(created.getId())
      .toUri();

    HttpHeaders headers = noCacheHeaders();
    headers.setLocation(location);

    return ResponseEntity.status(HttpStatus.CREATED).headers(headers).build();
  }

  @Operation(summary = "Get a Foo by id")
  @GetMapping(path = "/fooes/{id}", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<FooDTO> retrieveById(@PathVariable Long id) {
    try {
      var foo = fooService.retrieveById(id);
      return ResponseEntity
        .ok()
        .headers(noCacheHeaders())
        .body(fooMapper.toDTO(foo));
    } catch (FooNotFoundException ex) {
      throw new NotFoundException(ex.getMessage());
    }
  }

  @Operation(summary = "Get all Fooes")
  @GetMapping(path = "/fooes", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<List<FooDTO>> retrieve() {
    var list = fooMapper.toDtoList(fooService.retrieve());
    return ResponseEntity.ok().headers(noCacheHeaders()).body(list);
  }

  @Operation(summary = "Update Foo by id")
  @PutMapping(path = "/fooes/{id}", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> update(
    @PathVariable Long id,
    @Valid @RequestBody FooDTO dto
  ) {
    try {
      fooService.update(fooMapper.toDomain(dto.toBuilder().id(id).build()));
      return ResponseEntity.noContent().headers(noCacheHeaders()).build();
    } catch (FooNotFoundException ex) {
      throw new NotFoundException(ex.getMessage());
    }
  }

  @Operation(summary = "Delete Foo by id")
  @DeleteMapping(path = "/fooes/{id}", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    try {
      fooService.delete(id);
      return ResponseEntity.noContent().headers(noCacheHeaders()).build();
    } catch (FooNotFoundException ex) {
      throw new NotFoundException(ex.getMessage());
    }
  }
}
