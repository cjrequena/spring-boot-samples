package com.cjrequena.sample.web.api;

import com.cjrequena.sample.dto.FooDTO;
import com.cjrequena.sample.exception.api.NotFoundApiException;
import com.cjrequena.sample.exception.service.FooNotFoundServiceException;
import com.cjrequena.sample.service.FooService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static com.cjrequena.sample.common.Constants.VND_SAMPLE_SERVICE_V1;
import static com.cjrequena.sample.web.api.FooApi.ACCEPT_VERSION;
import static org.springframework.http.HttpHeaders.CACHE_CONTROL;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping(value = FooApi.ENDPOINT, headers = {ACCEPT_VERSION})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FooApi {

  public static final String ENDPOINT = "/foo-service/api/";
  public static final String ACCEPT_VERSION = "Accept-Version=" + VND_SAMPLE_SERVICE_V1;

  private final FooService fooService;

  @Operation(
    summary = "Create a new foo.",
    description = "Create a new foo.",
    parameters = {@Parameter(name= "accept-version", required = true, in = ParameterIn.HEADER,schema=@Schema(name = "accept-version", type = "string",implementation = String.class, allowableValues = {VND_SAMPLE_SERVICE_V1}))},
    requestBody =  @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FooDTO.class)))
  )
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "201", description = "Created - The request was successful, we created a new resource and the response body contains the representation."),
      @ApiResponse(responseCode = "204", description = "No Content - The request was successful, we created a new resource and the response body does not contains the representation."),
      @ApiResponse(responseCode = "400", description = "Bad Request - The data given in the POST failed validation. Inspect the response body for details."),
      @ApiResponse(responseCode = "401", description = "Unauthorized - The supplied credentials, if any, are not sufficient to access the resource."),
      @ApiResponse(responseCode = "408", description = "Request Timeout"),
      @ApiResponse(responseCode = "409", description = "Conflict - The request could not be processed because of conflict in the request"),
      @ApiResponse(responseCode = "429", description = "Too Many Requests - Your application is sending too many simultaneous requests."),
      @ApiResponse(responseCode = "500", description = "Internal Server Error - We couldn't create the resource. Please try again."),
      @ApiResponse(responseCode = "503", description = "Service Unavailable - We are temporarily unable. Please wait for a bit and try again. ")
    }
  )
  @PostMapping(
    path = "/fooes",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<Void> create(@Valid @RequestBody FooDTO dto, ServerHttpRequest request, UriComponentsBuilder ucBuilder) {
    dto = fooService.create(dto);
    URI resourcePath = ucBuilder.path(new StringBuilder().append(request.getPath()).append("/{id}").toString()).buildAndExpand(dto.getId()).toUri();
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
