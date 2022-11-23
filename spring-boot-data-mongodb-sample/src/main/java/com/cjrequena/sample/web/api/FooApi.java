package com.cjrequena.sample.web.api;

import com.cjrequena.sample.common.Constants;
import com.cjrequena.sample.dto.FooDTO;
import com.cjrequena.sample.exception.api.NotFoundApiException;
import com.cjrequena.sample.exception.service.FooNotFoundServiceException;
import com.cjrequena.sample.service.FooService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;

import static com.cjrequena.sample.web.api.FooApi.ACCEPT_VERSION;
import static org.springframework.http.HttpHeaders.CACHE_CONTROL;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = FooApi.ENDPOINT, headers = {ACCEPT_VERSION})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FooApi {

  public static final String ENDPOINT = "/foo-service/api/";
  public static final String ACCEPT_VERSION = "Accept-Version=" + Constants.VND_SAMPLE_SERVICE_V1;

  private final FooService fooService;

  @PostMapping(
    path = "/fooes",
    produces = {APPLICATION_JSON_VALUE}
  )
  public Mono<ResponseEntity<Void>> create(@Valid @RequestBody FooDTO dto, ServerHttpRequest request, UriComponentsBuilder ucBuilder) {
    return fooService.create(dto)
      .map(entity -> {
        HttpHeaders headers = new HttpHeaders();
        headers.set(CACHE_CONTROL, "no store, private, max-age=0");
        headers.set("id", entity.getId());
        final URI location = ucBuilder.path(new StringBuilder().append(request.getPath()).append("/{id}").toString()).buildAndExpand(entity.getId()).toUri();
        return ResponseEntity.created(location).headers(headers).build();
      });
  }

  @GetMapping(
    path = "/fooes/{id}",
    produces = {APPLICATION_JSON_VALUE}
  )
  public Mono<ResponseEntity<FooDTO>> retrieveById(@PathVariable(value = "id") String id) throws NotFoundApiException {

    HttpHeaders headers = new HttpHeaders();
    headers.set(CACHE_CONTROL, "no store, private, max-age=0");
    return fooService.retrieveById(id)
      .map(dto -> ResponseEntity.ok().headers(headers).body(dto))
      .onErrorMap(ex -> {
          if (ex instanceof FooNotFoundServiceException) {
            return new NotFoundApiException();
          }
          return ex;
        }
      );
  }

  @GetMapping(
    path = "/fooes",
    produces = {APPLICATION_JSON_VALUE}
  )
  public Mono<ResponseEntity<Flux<FooDTO>>> retrieve() {
    HttpHeaders headers = new HttpHeaders();
    headers.set(CACHE_CONTROL, "no store, private, max-age=0");
    final Flux<FooDTO> fooDTOV1Flux = this.fooService.retrieve();
    return Mono.just(ResponseEntity.ok().headers(headers).body(fooDTOV1Flux));
  }

  @PutMapping(
    path = "/fooes/{id}",
    produces = {APPLICATION_JSON_VALUE}
  )
  public Mono<ResponseEntity<Object>> update(@PathVariable(value = "id") String id, @Valid @RequestBody FooDTO dto) throws NotFoundApiException {
    dto.setId(id);
    HttpHeaders headers = new HttpHeaders();
    headers.set(CACHE_CONTROL, "no store, private, max-age=0");
    return this.fooService.update(dto)
      .map(entity -> ResponseEntity.noContent().headers(headers).build())
      .onErrorMap(ex -> {
          if (ex instanceof FooNotFoundServiceException) {
            return new NotFoundApiException();
          }
          return ex;
        }
      );
  }

  @DeleteMapping(
    path = "/fooes/{id}",
    produces = {APPLICATION_JSON_VALUE}
  )
  public Mono<ResponseEntity<Object>> delete(@PathVariable(value = "id") String id) throws NotFoundApiException {
    HttpHeaders headers = new HttpHeaders();
    headers.set(CACHE_CONTROL, "no store, private, max-age=0");
    return this.fooService.delete(id)
      .map(entity -> ResponseEntity.noContent().headers(headers).build())
      .onErrorMap(ex -> {
        if (ex instanceof FooNotFoundServiceException) {
          return new NotFoundApiException();
        } else {
          return ex;
        }
      });
  }

}
